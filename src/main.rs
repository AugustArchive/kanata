extern crate pretty_env_logger;
#[macro_use]
extern crate log;

pub mod config;
pub mod kube;

use crate::{config::KanataConfig, kube::Kubernetes};

#[tokio::main]
async fn main() {
    let _config = KanataConfig::new();

    // Setup logging
    pretty_env_logger::init();
    info!("initializing kubernetes client...");

    let _k8s = Kubernetes::new()
        .await
        .expect("unable to create kubernetes client. :<");
    info!("initialized k8s client, now watching over pods...");
}
