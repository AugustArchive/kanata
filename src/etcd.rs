use crate::config::KanataConfig;
use etcd_client::{Client, ConnectOptions, Error};
use std::boxed::Box;
use std::rc::Rc;

/// References a connection for etcd nodes.
#[derive(Clone)]
pub struct Etcd {
    /// Returns the [etcd client][Client] to connect towards all nodes.
    pub(crate) client: Box<Client>,
}

impl Etcd {
    pub async fn new(config: Rc<KanataConfig>) -> Result<Etcd, Error> {
        let cfg = config.clone();
        let connect_opts = if cfg.etcd.auth.is_some() {
            let (user, pass) = cfg.etcd.auth.as_ref().unwrap();
            Some(ConnectOptions::new().with_user(user, pass))
        } else {
            None
        };

        let nodes = Vec::<String>::from(cfg.etcd.nodes.as_ref());
        let client = Client::connect(nodes, connect_opts).await?;
        Ok(Etcd {
            client: Box::new(client),
        })
    }
}
