FROM --platform=linux/amd64 python:3.11-slim as runner

LABEL org.opencontainers.image.title="Drifty CLI"
LABEL org.opencontainers.image.description="Drifty is an Open-Source Interactive File Downloader System built with Java"
LABEL org.opencontainers.image.source="https://github.com/SaptarshiSarkar12/Drifty"
LABEL org.opencontainers.image.url="https://github.com/SaptarshiSarkar12/Drifty/pkgs/container/drifty-cli"
LABEL org.opencontainers.image.documentation="https://github.com/SaptarshiSarkar12/Drifty?tab=readme-ov-file#using-drifty-application-via-docker-"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.vendor="Saptarshi Sarkar"

WORKDIR /app
COPY . .
RUN chmod +x ./Drifty_CLI

CMD [ "/app/Drifty_CLI" ]