---
title: Kanata
description: Welcome to the Kanata documentation!

---

Hello, developer. This is the documentation for the **Kanata** project, created by me, Noel.

**Kanata** is an application that takes care of posting to [Instatus](https://instatus.com) when your pods
goes down. This was built to automate this task, even though, it is not *perfect*, it works as-is.

## More Depth of Kanata
Like said above, Kanata adds additional abstraction to automate tasks to posting to any status-page to say, "hi, <app> is going under maintenance" if the pod is updating and whatnot.

Though, with Kubernetes, pods can die out whenever it is killed for whatever reason, so how does Kanata not create multiple API requests so we can have a clean page?

It depends on your Kubernetes cluster.

But, what about persistence? Persistence does exist when using Kanata, we decided to opt into [etcd](https://etcd.io) to try it out and to see how it performs.

So, you are required to install **etcd** on your machine.

## Installation
To install **Kanata**, you have two options to choose from:

- [Helm Chart](#installation-helm-chart)
- [Deployment](#installation-deployment)

Since Kanata requires the Kubernetes API to operate, you cannot use this under a Docker Swarm stack (maybe support for it, soon?)

### Helm Chart
Using the **Kanata** helm chart is easier than doing it yourself since it will install a 3-node **etcd** cluster for you, so you don't have to!

To do so, you will need to use the chart repository hosted at `helm.floof.gay/charts` and from Bitnami:

```sh
$ helm repo add noel helm.floof.gay/charts
$ helm repo add bitnami charts.bitnami.com
```

When you have the repository in your Helm chart, you can install **Kanata**:

```sh
# Install etcd!
$ helm install <name> bitnami/etcd
$ helm install <name> noel/kanata
```

Now, you should have a barebones instance of Kanata running! Review the [configuration](#config) section to configure Kanata.

### Deployment
To apply Kanata using the [Docker image](https://hub.docker.com/r/auguwu/kanata), you will need to setup **etcd** before running.

This documentation page will not go into how to install **etcd**, so... good luck!

You will need to create a deployment for Kanata, we provide a default [deployment](https://raw.githubusercontent.com/auguwu/kanata/master/contrib/kube/deploy.yml) for you to use, all you need to do is:

```sh
$ kube apply -f https://raw.githubusercontent.com/auguwu/kanata/master/contrib/kube/deploy.yml
```

and you're set!

## Configuration
Kanata uses **YAML** for a configuration file placed under `<directory>/config.yml`, for using Helm or the deployment above, you can put it in `/opt/noel/kanata/config.yml`.

```yaml
# Returns the webhook URL for Instatus.
# Optional: true
# Default: <not set>
webhook_url: ...

# Returns the Instatus key for retrieving component IDs.
# You can invoke this using `<kanata-bin> list`.
#
# Optional: false
key: ...

# Returns the namespace to check for.
# Optional: false
ns: ...

# Map of component-id -> pod-name to post on what component.
# Optional: false
components:
    <component-id-1>: <pod-name>

# Configuration for etcd.
etcd:
    # Authentication if you enabled it on your etcd cluster.
    auth:
        user: ...
        pass: ...

    # List of nodes to use when connecting
    nodes:
        - <host>:<port>
```
