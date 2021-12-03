#!/bin/bash

. "$PWD/scripts/lib/liblog.sh"

COMMIT="$(git rev-parse --short HEAD)"
VERSION=$(cat "$PWD/version.json" | jq '.version' | tr -d '"')

info "docker: publishing auguwu/kanata:latest, auguwu/kanata:$VERSION, and auguwu/kanata:$COMMIT..."
docker push auguwu/kanata:latest
docker push "auguwu/kanata:$VERSION"
docker push "auguwu/kanata:$COMMIT"

info "docker: done!"
