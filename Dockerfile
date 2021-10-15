FROM rust:alpine AS builder

ARG version="unknown"
ARG commit_hash="unknown"

LABEL MAINTAINER="Noel <cutie@floofy.dev>"
LABEL dev.floofy.kanata.version=${version}
LABEL dev.floofy.kanata.commit=${commit_hash}

WORKDIR /kanata/build
COPY . .
RUN cargo build --release

FROM alpine:latest

WORKDIR /app
COPY --from=builder /kanata/build/target/release/kanata /app/kanata
ENV RUST_LOG=info

CMD ["/app/kanata"]
