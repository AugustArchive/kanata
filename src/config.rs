use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Deserialize, Serialize, Debug, Clone)]
pub struct KanataConfig {
    /// Represents a [`HashMap`] of key-value pairs for the list
    /// of Instatus component IDs -> pod names. You are not required
    /// to include the random hash in the pod (if it is a deployment).
    ///
    /// ```yml
    /// components:
    ///     # The component ID from instatus, you can also invoke
    ///     # `kanata list:components` to view a list of the components.
    ///     some_component_id: nino-prod
    /// ```
    pub(crate) components: HashMap<String, String>,

    /// Instatus developer key, you can retrieve it [`here`](https://instatus.com/app/developer)
    pub(crate) key: String,

    /// Returns the [webhook URL](https://instatus.com/help/monitoring/custom-service-webhook), if any. This will post to that endpoint
    pub(crate) webhook_url: Option<String>,

    /// Returns the namespace to use. This is required.
    pub(crate) ns: String,
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
