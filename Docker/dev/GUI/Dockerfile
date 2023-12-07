LABEL org.opencontainers.image.title="Drifty GUI (Development)"
LABEL org.opencontainers.image.description="Drifty is an Open-Source Interactive File Downloader System built with Java"
LABEL org.opencontainers.image.source="https://github.com/SaptarshiSarkar12/Drifty"
LABEL org.opencontainers.image.documentation="https://github.com/SaptarshiSarkar12/Drifty?tab=readme-ov-file#using-docker--for-development"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.vendor="Saptarshi Sarkar"

FROM drifty-base:latest AS build
# Set the working directory
WORKDIR /app

COPY . .

RUN gcc -c /app/config/missing_symbols.c -o /app/config/missing_symbols-ubuntu-latest.o

RUN mvn -Pbuild-drifty-gui-for-ubuntu-latest gluonfx:build gluonfx:package

FROM drifty-runner:latest
WORKDIR /app
COPY --from=build /app/target/gluonfx/x86_64-linux /app

CMD [ "/app/Drifty" ]
