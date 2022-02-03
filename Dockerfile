FROM alpine:latest

RUN apk update && apk add --no-cache bash musl-dev libc-dev gcompat

WORKDIR /app/noel/kanata
COPY docker/docker-entrypoint.sh /app/noel/kanata/scripts/docker-entrypoint.sh
COPY docker/scripts/liblog.sh    /app/noel/kanata/scripts/liblog.sh
COPY docker/runner.sh            /app/noel/kanata/scripts/runner.sh
COPY kanata                      /app/noel/kanata/kanata

RUN chmod +x /app/noel/kanata/scripts/docker-entrypoint.sh
RUN chmod +x /app/noel/kanata/scripts/runner.sh

USER 1001
ENTRYPOINT ["/app/noel/kanata/scripts/docker-entrypoint.sh"]
CMD ["/app/noel/kanata/scripts/runner.sh"]
