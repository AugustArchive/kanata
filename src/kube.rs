use std::{
    collections::HashMap,
    thread::{spawn, JoinHandle},
};

use crate::config::KanataConfig;
use k8s_openapi::api::core::v1::Pod;
use kube::{api::ListParams, api::WatchEvent, Api, Client, ResourceExt};
use rocket::futures::{StreamExt, TryStreamExt};

/// Represents a implementation of the Kubernetes client
/// for Kanata.
pub struct Kubernetes {
    /// Returns a new [`KubernetesClient`](Client) to use for this struct.
    client: Client,

    /// Returns a [`HashMap`](HashMap) of threads that this
    /// struct is controlling. This map only exists
    /// for pod memory / resource usage, since managing
    /// pod phases should be stuck in the main thread.
    threads: HashMap<i32, JoinHandle<()>>,
}

impl Kubernetes {
    pub async fn new() -> Result<Kubernetes, kube::Error> {
        let client = Client::try_default().await?;
        Ok(Kubernetes {
            client,
            threads: HashMap::new(),
        })
    }

    pub async fn watch(mut self, _config: KanataConfig) -> Result<(), kube::Error> {
        info!("kanata: now watching over pods and pod memory usage...");

        self.threads.insert(
            0,
            spawn(move || {
                info!("kanata: managing pod memory usage as handle id #0");
            }),
        );

        info!("kanata: spawned thread 0 (POD-MEMORY), now spawning pod resource usage thread...");
        self.threads.insert(
            1,
            spawn(move || {
                info!("kanata: managing pod resource usage as handle id #1");
            }),
        );

        info!("kanata: thread creation successful, now watching over pods!");
        let api: Api<Pod> = Api::namespaced(self.client, "august");
        let list_params = ListParams::default();
        let mut pod_stream = api.watch(&list_params, "0").await?.boxed();

        while let Some(status) = pod_stream.try_next().await? {
            match status {
                WatchEvent::Modified(pod) => {
                    let status = pod.status.as_ref().expect("status exists on pod :(");
                    let phase = status.phase.clone().unwrap_or_default();
                    warn!("pod {} phase was set to {}.", pod.name(), phase);
                }

                _ => {}
            }
        }

        Ok(())
    }
}
