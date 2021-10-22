/// This is here since **`kube-rs`** doesn't have Node Metrics
/// apart as their API.
/// see: https://github.com/kube-rs/kube-rs/issues/492
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Usage {
    pub cpu: String,
    pub memory: String,
}

/*
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Usage {
    pub cpu: String,
    pub memory: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct NodeMetrics {
  metadata: kube::api::ObjectMeta,
  usage: Usage,
  timestamp: String,
  window: String,
}

    let request = Request::new("/apis/metrics.k8s.io/v1beta1/nodes");
    client
      .clone()
      .request::<ObjectList<NodeMetrics>>(request.list(&ListParams::default()).unwrap())
      .await
*/
