extern crate pretty_env_logger;
#[macro_use]
extern crate log;

pub mod config;
pub mod kube;

use std::env;
use crate::{config::KanataConfig, kube::Kubernetes};

#[tokio::main]
async fn main() {
    let cmd = env::args().collect::<Vec<_>>();
    info!("{:?}", cmd);

    let config = KanataConfig::new();

    // Setup logging
    pretty_env_logger::init();
    info!("initializing kubernetes client...");

    let mut k8s = Kubernetes::new()
        .await
        .expect("unable to create kubernetes client. :<");
    info!("initialized k8s client, now watching over pods...");

    loop {
        k8s.watch(&config).await.expect("k8s: unable to watch.");
    }
}
