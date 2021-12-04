use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Deserialize, Serialize, Debug, Clone)]
pub struct KanataConfig {
    /// Represents a [`HashMap`] of key-value pairs for the list
    /// of pod name -> Instatus component ID. You are not required
    /// to include the random hash in the pod (if it is a deployment).
    ///
    /// ```yaml
    /// components:
    ///     # The component ID from instatus, you can also invoke
    ///     # `kanata list` to view a list of the components.
    ///     nino-prod: <instatus id>
    /// ```
    pub components: HashMap<String, String>,

    /// Instatus developer key, you can retrieve it [`here`](https://instatus.com/app/developer)
    pub key: String,

    /// Returns the [webhook URL](https://instatus.com/help/monitoring/custom-service-webhook), if any. This will post to that endpoint
    pub webhook_url: Option<String>,

    /// Returns the namespace to use. This is required.
    pub ns: String,

    /// Returns the etcd configuration.
    pub etcd: EtcdConfig,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct EtcdConfig {
    /// Returns a list of etcd nodes to connect to.
    pub nodes: Vec<String>,

    /// Enables authentication to connect to an etcd node. This
    /// is fairly optional but recommended.
    pub auth: Option<(String, String)>,
}

impl KanataConfig {
    /// Creates a new [`KanataConfig`](KanataConfig) instance from
    /// reading a `config.yml` file and serialized using Serde YAML.
    pub fn new() -> KanataConfig {
        let data = std::fs::read_to_string("./config.yml")
            .expect("kanata: unable to read `config.yml` file.");

        serde_yaml::from_str(&data).expect("kanata: unable to serialize `KanataConfig` from file.")
    }
}

impl Default for KanataConfig {
    fn default() -> Self {
        Self::new()
    }
}
