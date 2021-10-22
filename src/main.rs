#![feature(thread_id_value)]

extern crate pretty_env_logger;
#[macro_use]
extern crate log;

pub mod config;
pub mod kube;
pub mod version;

use crate::{config::KanataConfig, kube::Kubernetes, version::VERSION};
use std::{env, process::exit};

#[tokio::main]
async fn main() {
    let cmd = env::args().collect::<Vec<_>>();
    let empty = &String::from("");
    let c = cmd.get(1).unwrap_or(empty);

    if c == "list" {
        info!("listing components from Instatus...");
        exit(0);
    }

    let config = KanataConfig::new();
    info!("running v{} of kanata!", VERSION);
    info!("if you find any bugs, report it to Noel! (https://github.com/auguwu/Kanata/issues)");

    // Setup logging
    pretty_env_logger::init();
    info!("initializing kubernetes client...");

    let k8s = Kubernetes::new()
        .await
        .expect("unable to create kubernetes client. :<");
    info!("initialized k8s client, now watching over pods...");

    loop {
        k8s.clone()
            .watch(&config)
            .await
            .expect("k8s: unable to watch.");
    }
}
