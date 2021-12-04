use crate::{config::KanataConfig, etcd::Etcd};
use k8s_openapi::api::core::v1::Pod;
use kube::{api::ListParams, api::WatchEvent, Api, Client, ResourceExt, Resource};
use rocket::futures::{StreamExt, TryStreamExt};
use std::{collections::HashMap, ops::Deref};

/// Represents a implementation of the Kubernetes client
/// for Kanata.
#[derive(Clone)]
pub struct Kubernetes {
    /// Returns a new [`KubernetesClient`](Client) to use for this struct.
    client: Client,

    /// Returns the pod state in-memory, so when the stream closes,
    /// this gets re-used but saved in Etcd.
    pod_states: HashMap<String, String>,

    /// Returns the http client to use to request to Instatus.
    request: reqwest::Client,

    /// Returns if this [Kubernetes] struct has been ran more than once.
    /// Since Kubernetes' watch API runs for ~5 minutes, it will re-run the
    /// loop from src/main.rs, so to preserve requests to Etcd, we have this
    /// variable which prevents from polluting the pod state tree with data that
    /// can be old. This will refresh the current pod state tree into the persisted data
    /// from Etcd. This can be easily overrided with the `KANATA_DISABLE_ETCD_RUN` environment
    /// variable if you wish to keep refreshing **pod states** with possible garbage data that
    /// isn't accurate.
    first_run: bool
}

impl Kubernetes {
    pub async fn new() -> Result<Kubernetes, kube::Error> {
        let client = Client::try_default().await?;

        Ok(Kubernetes {
            client,
            pod_states: HashMap::new(),
            request: reqwest::Client::new(),
            first_run: true,
        })
    }

    pub async fn update_pod_state(&mut self, name: &String, phase: String, config: Box<&KanataConfig>) {
        let old_phase = self.pod_states.get(name);
        let config = Box::clone(&config);

        if let Some(old) = old_phase {
            if &phase != old {
                info!("Pod {} has updated from phase \"{}\" => \"{}\"", name, old, phase);

                // update in-memory cache
                self.pod_states.insert(name.deref().to_string(), phase);

                // check if the pod is in the config tree
                // TODO: beg ice to do the regex for me
                if let Some((_, value)) = config.components.get_key_value(name) {
                    info!("found component ID ({}) from pod {}", value, name);
                }
            }
        } else {
            info!("Pod {} was not cached in-memory, phase is now {}.", name, phase);
            self.pod_states.insert(name.deref().to_string(), phase);
        }
    }

    fn disable_first_run(&mut self) {
        warn!("disabling first run...");
        self.first_run = false;
    }

    pub async fn watch(&mut self, config: &KanataConfig, etcd: &Etcd) -> Result<(), kube::Error> {
        let kube = self.clone();
        let api: Api<Pod> = Api::namespaced(kube.client, &config.ns);

        info!("getting last resource version...");
        let list_params = ListParams::default();
        let all_pods = api.list(&list_params).await?;
        let last_version = all_pods
            .metadata
            .clone()
            .resource_version
            .expect("kanata: resource version was not provided.");

        info!(
            "{} pods were found, last resource version was {}",
            all_pods.into_iter().len(),
            last_version
        );

        info!("retrieving data from etcd...");

        let mut etcd_client = etcd.clone().client;
        if kube.first_run {
            info!("this is the first run, populating in-memory cache...");

            let pod_states = etcd_client
                .clone()
                .get("kanata/pods", None)
                .await
                .ok();

            if let Some(current_state) = pod_states {
                let header = current_state.header();
                let (cluster_id, revisions) = if let Some(header) = header {
                    (header.cluster_id(), header.revision())
                } else {
                    (0u64, 0i64)
                };

                info!("received current state from cluster {} with revision #{}, now comparing from kubernetes...", cluster_id, revisions);

                // get pod phases
                let pod_tree = api.list(&list_params).await?;
                debug!("from etcd: 0 | from kubernetes: {}", pod_tree.items.len());

                for (index, pod) in pod_tree.items.into_iter().enumerate() {
                    let metadata = pod.meta();
                    if let Some(name) = &metadata.name {
                        info!("found pod {} from iter index #{}", name, index);
                    } else {
                        info!("skipping on unknown pod (no name available) | index from iter: {}", index);
                    }
                }
            } else {
                warn!("missing etcd pod table (assuming first installation), creating...");
                etcd_client.put("kanata/pods", "{}", None).await.expect("unable to create pod table.");
            }

            // Check the `first_run` property in Kubernetes struct
            // on why we are making it falsy.
            self.disable_first_run();
        }

        let mut pod_stream = api
            .watch(&list_params, last_version.as_str())
            .await?
            .boxed();

        info!("now watching pod stream...");
        while let Some(status) = pod_stream.try_next().await? {
            match status {
                WatchEvent::Modified(pod) => {
                    let status = pod.status.as_ref().expect("status exists on pod :(");
                    let phase = status.phase.clone().unwrap_or_default();
                    let pod_name = pod.name();

                    self.update_pod_state(&pod_name, phase, Box::new(config)).await;
                }

                WatchEvent::Deleted(pod) => {
                    let name = pod.name();
                    warn!("Pod {} was deleted, removing in-memory cache!", name);

                    self.pod_states.remove_entry(&name);
                }

                _ => {}
            }
        }

        info!("stream closed, storing old state in etcd...");

        let serialized_state = serde_json::to_string(&kube.pod_states).expect("unable to serialize pod state");
        etcd_client.put("kanata/pods", serialized_state, None).await.expect("unable to put kanata/pods into etcd");

        Ok(())
    }
}
