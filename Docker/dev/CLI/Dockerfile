FROM drifty-base:latest AS build

LABEL org.opencontainers.image.title="Drifty CLI (Development)"
LABEL org.opencontainers.image.description="Drifty is an Open-Source Interactive File Downloader System built with Java"
LABEL org.opencontainers.image.source="https://github.com/SaptarshiSarkar12/Drifty"
LABEL org.opencontainers.image.documentation="https://drifty.vercel.app/docs/usage/docker/drifty-in-docker"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.vendor="Saptarshi Sarkar"

WORKDIR /app

COPY . .

RUN gcc -c /build/config/missing_symbols.c -o /build/config/missing_symbols-ubuntu-latest.o
RUN mvn clean install
RUN mvn -P build-drifty-cli-for-ubuntu-latest package

FROM oraclelinux:9-slim

WORKDIR /app
COPY --from=build /app/CLI/target/CLI/linux /app

CMD [ "/app/Drifty CLI" ]
