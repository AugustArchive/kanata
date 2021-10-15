extern crate pretty_env_logger;
#[macro_use] extern crate log;

pub mod kube;
pub mod config;
pub mod routing;

#[tokio::main]
async fn main() {
    // Setup logging
    pretty_env_logger::init();
    info!("initializing kubernetes client...");
}
