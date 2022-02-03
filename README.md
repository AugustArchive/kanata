# 💫 彼方 ("kanata")
> *Automative Kubernetes watcher to view pod phases and reflect them onto popular statuspages.*

## What is Kanata?
**Kanata** is an automative process to watch over Kubernetes pod phases and reflect it on popular status pages like
[Instatus](https://instatus.com) or [Statuspages by Atlassian](https://statuspages.io).

The name of the project is inspired by one of my favourite Hololive vtuber I watch... sometimes... [Amane Kanata](https://www.youtube.com/channel/UCZlDXzGoo7d44bwdNObFacg)

## Why did you build this?
No one likes to repeat code in any project OR wants to go to their statuspage site, tell something is down/reopened/etc, it
can be automated and this is the project for you!

## Infrastructure
**Kanata** is a Go CLI utility, if you execute the `kanata` binary with no commands, it will start up 2 goroutines:

- **daemon**
- **http service**

The **daemon**'s job is to connect to the Kubernetes API and watch over pod phases and report it back. This should
not be exposed to the end user.

The **http service** is a HTTP server that is safe to expose to have Prometheus metrics, health checks, profiling,
and much more, this is safe to expose.

## Installation
You can run **Kanata** from the following:

- [Helm Chart](#helm-chart) (**recommended**)
- [Docker Image](#docker-image)
- [Locally](#locally) ([Git](#git) or [Releases](#releases))

### Helm Chart
The easiest way to install **Kanata** on Kubernetes is using the Noelware [helm chart](https://charts.noelware.org/noel/kanata), maintained
by yours truely~

You are required Kubernetes **>=1.22** and Helm **3**.

```shell
# Pull the helm chart repository
$ helm repo add noel https://charts.noelware.org/noel

# Install it!
$ helm install <my-release> noel/kanata
```

The Helm chart will now install Kanata alongside with [etcd](https://github.com/coreos/etcd) since Kanata uses **etcd**
to keep a persistent pool for pod phases.

### Docker Image
This is mainly for people who want to spawn their own Kanata deployment since the **Kubernetes API** is required
to function properly.

You can retrieve the image from [Docker Hub](https://hub.docker.com/r/auguwu/kanata) or [GitHub Container Registry](https://github.com/auguwu/kanata/pkgs/container/kanata).

Kanata supports only Linux containers for **amd64**, **arm64** and **armv7**.

```shell
# Pull the image down
# Replace <image> with the Docker image you want to use.
# Examples:
#     - auguwu/kanata - Pulls from Docker Hub for the latest, x64 release
#     - ghcr.io/auguwu/kanata:1.0-arm64 - Pulls the v1.0 image from GitHub Container Registry for ARM64.
#     - auguwu/kanata:1.0.3-armv7 - Pulls a specific version (being v1.0.3) for ARMv7 builds.
$ docker pull <image>

# Now we run the image!
$ docker run -d -p 5555:2092 <image>
```

## Configuration
**Kanata** uses TOML as its main configuration file format.

```toml
# Valid DSN for Sentry to use with Kanata.
sentry_dsn = "something"

# If debug logs should be enabled or not
debug = true

[logging]
# Configures the level. If `debug` is enabled, then this is overrided to `DEBUG`.
level = "INFO"

# Returns the format to use when printing out. If the `logging.encoder` is set
# to `json`, this will not be used.
format = "[<$(time)>] <$(level)> :: $(message)"

# If colours should be used when using the default logging encoder.
# This is ignored if `logging.encoder` is set to json.
colors = true

# The logging encoder to use when printing out to stdout. By default,
# it will use a "pretty formatted" format, but you can set it to JSON if you wish.
encoder = "json"

# Configures a list of appenders to enable. You can configure Logstash
# or a Rolling File policy using `logging.appender.<key>`
# This is a list of what is enabled.
appenders = ["file", "logstash"]

# Configures Logstash to be used. Read the documentation on how to expose it using
# the ELK Stack: https://docs.floof.gay/kanata/self-hosting/config#using-the-elk-stack
[logging.appender.logstash]
# Kanata requires Logstash to be configured using the TCP or UDP inputs.
# This is the protocol to set when using the Logstash appender.
protocol = "tcp"

# The host to connect to
host = "0.0.0.0"

# The port to connect to
port = 7272

# Configures a Rolling File policy to be used.
[logging.appender.file]
# Sets up the file path to use when using this appender.
#
# For specific operating systems, you can use the ${<os>:<path>} key structure
# as defined in the example below.
path = "${unix:/var/log/kanata/kanata.log}|${windows:C:\\Users\\<user>\\AppData\\Local\\Kanata\\Logs}"

# Configures using Instatus to post to.
[statuspage.instatus]
# The API key. You can retrieve this from the dashboard.
api_key = "..."

# The component ID hash, this is mapped to "pod_name" => "component_id".
# You can retrieve a list from the instatus-cli I made!
#
# https://github.com/auguwu/instatus-cli
components = { "pod-name" = "component_id" }
```

## License
**kanata** is released under the **GPL-3.0** License! Read [here](/LICENSE) for more information.
