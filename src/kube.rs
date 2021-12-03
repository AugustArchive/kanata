use crate::{config::KanataConfig, etcd::Etcd};
use k8s_openapi::api::core::v1::Pod;
use kube::{api::ListParams, api::WatchEvent, Api, Client, ResourceExt};
use rocket::futures::{StreamExt, TryStreamExt};
use std::collections::HashMap;

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
    requester: reqwest::Client,
}

impl Kubernetes {
    pub async fn new() -> Result<Kubernetes, kube::Error> {
        let client = Client::try_default().await?;

        Ok(Kubernetes {
            client,
            pod_states: HashMap::new(),
            requester: reqwest::Client::new(),
        })
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
        let mut pod_stream = api
            .watch(&list_params, last_version.as_str())
            .await?
            .boxed();

        info!("retrieving data from etcd...");
        let etcd_cls = etcd.clone();
        let pod_states = etcd_cls
            .client
            .clone()
            .get("", None)
            .await
            .expect("unable to retrieve data from etcd");

        println!("{:#?}", pod_states);
        while let Some(status) = pod_stream.try_next().await? {
            match status {
                WatchEvent::Modified(pod) => {
                    let status = pod.status.as_ref().expect("status exists on pod :(");
                    let phase = status.phase.clone().unwrap_or_default();
                    let pod_name = pod.name();

                    if !self.pod_states.contains_key(&pod_name) {
                        let name = pod_name.clone();

                        self.pod_states.insert(pod_name, phase.clone());
                        info!("Pod {} phase was set to {} (cached in-memory)", name, phase);

                        // do logic here
                    } else {
                        let old_phase = self.pod_states.get(&pod_name);
                        if old_phase.is_none() {
                            warn!(
                                "Pod {} was cleaned from in-memory? (phase={})",
                                pod.name(),
                                phase
                            );
                        } else {
                            let mut curr_phase = phase.clone();
                            let old = old_phase.unwrap();

                            if curr_phase.as_mut() != old {
                                info!(
                                    "Pod {} has their phase changed from {} => {}.",
                                    pod_name, old, curr_phase
                                );
                                self.pod_states.insert(pod_name, curr_phase);

                                // now do bullshit logic here :D
                            }
                        }
                    }
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
        Ok(())
    }
}
