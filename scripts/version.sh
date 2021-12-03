#!/bin/bash

. "$PWD/scripts/lib/liblog.sh"

VERSION=$(cat "$PWD/version.json" | jq .version | tr -d '"')
COMMIT_HASH=$(git rev-parse --short HEAD)
BUILD_DATE=$(date +"%D - %r")

rm src/version.rs && touch src/version.rs

echo "pub const VERSION: &str = \"${VERSION}\";" >> src/version.rs
echo "pub const COMMIT: &str = \"${COMMIT_HASH}\";" >> src/version.rs
echo "pub const BUILD_DATE: &str = \"${BUILD_DATE}\";" >> src/version.rs

info "done. :3"
