#!/bin/bash

. "$PWD/scripts/lib/liblog.sh"

COMMIT="$(git rev-parse HEAD)"
COMMIT_HASH="${COMMIT:0:8}"
VERSION=$(cat "$PWD/version.json" | jq '.version' | tr -d '"')

info "===================================="
info "|> git info: under branch $BRANCH <|"
info "|> git info: commit: ${COMMIT:0:8} <|"
info "===================================="

info "docker: now building for auguwu/kanata:latest..."
docker build . -t auguwu/kanata:latest --no-cache --build-arg version=$VERSION --build-arg commit=$COMMIT_HASH

info "docker: now building for auguwu/kanata:$VERSION..."
docker build . -t "auguwu/kanata:$VERSION" --no-cache --build-arg version=$VERSION --build-arg commit=$COMMIT_HASH

info "docker: now building for auguwu/kanata:$COMMIT_HASH..."
docker build . -t "auguwu/kanata:$COMMIT_HASH" --no-cache --build-arg version=$VERSION --build-arg commit=$COMMIT_HASH

info "docker: done!"
