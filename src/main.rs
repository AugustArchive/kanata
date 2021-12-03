extern crate pretty_env_logger;

#[macro_use]
extern crate log;

pub mod config;
pub mod etcd;
pub mod kube;
pub mod os_signals;
pub mod version;

use crate::{
    config::KanataConfig,
    etcd::Etcd,
    kube::Kubernetes,
    os_signals::init_signals,
    version::{BUILD_DATE, COMMIT, VERSION},
};

use std::{env, process::exit, rc::Rc};

#[tokio::main]
async fn main() {
    // used for docker <3
    unsafe {
        init_signals();
    }

    let cmd = env::args().collect::<Vec<_>>();
    let empty = &String::from("");
    let c = cmd.get(1).unwrap_or(empty);

    if c == "list" {
        info!("listing components from Instatus...");
        exit(0);
    }

    match env::var("RUST_LOG") {
        Ok(_) => {
            // do nothing since it's already set
        }

        Err(_) => {
            env::set_var("RUST_LOG", "INFO");
        }
    }

    pretty_env_logger::init();
    let config = Rc::new(KanataConfig::new());
    info!(
        "~ kanata v{} (commit: {}, built at: {}) ~",
        VERSION, COMMIT, BUILD_DATE
    );

    info!("if you find any bugs, report it to Noel! (https://github.com/auguwu/Kanata/issues)");
    info!("initializing kubernetes client...");

    let k8s = Kubernetes::new()
        .await
        .expect("unable to create kubernetes client. :<");

    info!("connecting to etcd...");
    let etcd = Etcd::new(Rc::new(KanataConfig::new()))
        .await
        .expect("unable to connect to etcd.");

    info!("initialized k8s client, now watching over pods...");
    loop {
        k8s.clone()
            .watch(&config, &etcd)
            .await
            .expect("k8s: unable to watch.");
    }
}
