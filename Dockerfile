FROM rust:latest AS builder

ARG version="unknown"
ARG commit_hash="unknown"

LABEL MAINTAINER="Noel <cutie@floofy.dev>"
LABEL dev.floofy.kanata.version=${version}
LABEL dev.floofy.kanata.commit=${commit_hash}

WORKDIR /kanata/build
COPY . .
RUN cargo build --release

FROM alpine:latest

WORKDIR /app/kanata
COPY --from=builder /kanata/build/target/release/kanata /app/kanata/kanata
ENV RUST_LOG=info

CMD ["/app/kanata/kanata"]
