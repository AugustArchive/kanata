VERSION=$(shell cat ./version.json | jq .version | tr -d '"')
COMMIT_HASH=$(shell git rev-parse --short HEAD)

build:
	cargo build

lint: lint.clippy lint.rustfmt

lint.clippy:
	cargo clippy --fix --allow-dirty

lint.rustfmt:
	cargo fmt --all -- --emit files

docker.build:
	docker build . -t auguwu/kanata:latest --build-arg version=$(VERSION) --build-arg commit_hash=$(COMMIT_HASH)
