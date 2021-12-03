FROM rust:latest AS builder

ARG version="unknown"
ARG commit_hash="unknown"

WORKDIR /kanata/build
COPY . .
RUN cargo build --release

FROM alpine:latest

LABEL MAINTAINER="Noel <cutie@floofy.dev>"
LABEL gay.floof.kanata.version=${version}
LABEL gay.floof.kanata.commit=${commit_hash}

WORKDIR /opt/noel/kanata

COPY --from=builder /kanata/build/target/release/kanata /opt/noel/kanata
RUN chmod +x /opt/noel/kanata

ENTRYPOINT [ "/opt/noel/kanata" ]
