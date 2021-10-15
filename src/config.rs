use std::collections::HashMap;

use serde::{Deserialize,Serialize};

#[derive(Deserialize, Serialize, Debug)]
struct KanataConfig {
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
    components: HashMap<String, String>,

    /// Instatus developer key, you can retrieve it [`here`](https://instatus.com/app/developer)
    key: String,

    /// Returns the [webhook URL](), if any. This will post to that endpoint
    #[serde(rename = "webhook_url")]
    webhookUrl: Option<String>
}
