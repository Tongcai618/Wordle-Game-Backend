FROM ubuntu:latest
LABEL authors="tongcai"

ENTRYPOINT ["top", "-b"]