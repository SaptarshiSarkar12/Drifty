FROM drifty-base:latest AS build

LABEL org.opencontainers.image.title="Drifty GUI (Development)"
LABEL org.opencontainers.image.description="Drifty is an Open-Source Interactive File Downloader System built with Java"
LABEL org.opencontainers.image.source="https://github.com/SaptarshiSarkar12/Drifty"
LABEL org.opencontainers.image.documentation="https://drifty.vercel.app/docs/usage/docker/drifty-in-docker"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.vendor="Saptarshi Sarkar"

# Set the working directory
WORKDIR /app

COPY . .

RUN gcc -c /app/config/missing_symbols.c -o /app/config/missing_symbols-ubuntu-latest.o
RUN mvn clean install
RUN mvn -P build-drifty-gui-for-ubuntu-latest gluonfx:build gluonfx:package -rf :GUI

FROM drifty-runner:latest
WORKDIR /app
COPY --from=build /app/GUI/target/gluonfx/x86_64-linux /app
RUN chmod +x "/app/Drifty GUI"
CMD ["/app/Drifty GUI"]