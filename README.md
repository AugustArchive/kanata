# 💫 Kanata
> **Small microservice to handle state changes of Kubernetes pods and post to Instatus**

## 🤔 Why?
I don't really want to implement and repeat code to report the pod's state on Instatus on every project I make,
so I made it as a small, scalable microservice that shouldn't be down most of the time unless the Kubernetes
cluster needs to restart.

The name is inspired by one my favourite vtubers I like to watch, [Amane Kanata](https://www.youtube.com/channel/UCZlDXzGoo7d44bwdNObFacg).

## 🖥️ Installation
You can install Kanata using your own machine or with Docker! Kanata
is meant supposed to be small, and easy to use.

You need **Rust** and [etcd](https://github.com/etcd-io/etcd). At the moment, I am using

```sh
$ rustc --version
# rustc 1.57.0-nightly (fdf65053e 2021-09-07)
```

I provide a Docker image over at [Docker Hub](https://hub.docker.com/-/noel/kanata) if you want to run
Kanata in a Docker container.

### Docker
All you really need to do is:

```shell
# 1. Pull the image off of Docker Hub
$ docker pull noelware/kanata:latest # you can also specify a version or commit hash

# 2. Run it!
$ docker run -d -p <host>:22903 --name kanata --restart always \
  -v /path/to/config:/app/noel/kanata/config.yml \
  auguwu/kanata:latest # or specify the version / hash you used
```

### Git Repository
You will need the Git command tools installed before proceeding.

```shell
# 1. Clone the repository
$ git clone https://github.com/auguwu/Kanata && cd Kanata

# 2. Run `build` to build the binary
$ cargo build --release

# 3. Run the binary!
$ ./target/release/kanata # or ./target/release/kanata.exe on Windows!
```

## ⚡ License
**Kanata** is released under the **GPL-3.0** License, read [here](/LICENSE) for more information!
