#!/bin/bash

. "$PWD/scripts/lib/liblog.sh"

VERSION=$(cat "$PWD/version.json" | jq .version | tr -d '"')
echo "pub const VERSION: &str = \"${VERSION}\";" >> src/version.rs
info "done. :3"
