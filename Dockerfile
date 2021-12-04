FROM rust:alpine AS builder

# thank you ben for helping me 
# source: https://github.com/Benricheson101/anti-phishing-bot/blob/single_server/Dockerfile
RUN apk update && apk add --no-cache build-base openssl-dev gcompat libc6-compat protobuf
WORKDIR /build/kanata

COPY Cargo.toml .
RUN echo "fn main() {}" >> dummy.rs
RUN sed -i 's#src/main.rs#dummy.rs#' Cargo.toml
ENV RUSTFLAGS=-Ctarget-feature=-crt-static
RUN cargo build --release
RUN rm dummy.rs && sed -i 's#dummy.rs#src/main.rs#' Cargo.toml

COPY . .
RUN cargo build --release

FROM alpine:latest

ARG version="unknown"
ARG commit_hash="unknown"

LABEL MAINTAINER="Noel <cutie@floofy.dev>"
LABEL gay.floof.kanata.version=${version}
LABEL gay.floof.kanata.commit=${commit_hash}

WORKDIR /opt/noel/kanata
RUN apk add build-base openssl
COPY --from=builder /build/kanata/target/release/kanata-bin /opt/noel/kanata/kanata

ENTRYPOINT [ "/opt/noel/kanata/kanata" ]
