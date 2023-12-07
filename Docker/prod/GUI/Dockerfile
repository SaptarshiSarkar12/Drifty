LABEL org.opencontainers.image.title="Drifty GUI"
LABEL org.opencontainers.image.description="Drifty is an Open-Source Interactive File Downloader System built with Java"
LABEL org.opencontainers.image.source="https://github.com/SaptarshiSarkar12/Drifty"
LABEL org.opencontainers.image.url="https://github.com/SaptarshiSarkar12/Drifty/pkgs/container/drifty-gui"
LABEL org.opencontainers.image.documentation="https://github.com/SaptarshiSarkar12/Drifty?tab=readme-ov-file#using-drifty-application-via-docker-"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.vendor="Saptarshi Sarkar"

FROM --platform=linux/amd64 python:3.11-slim as runner

WORKDIR /app
COPY . .
RUN apt-get update --no-install-recommends && apt-get install ffmpeg libsm6 libxext6 libgtk-3-0 -y --no-install-recommends
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
RUN chmod +x ./Drifty_GUI

CMD [ "/app/Drifty_GUI" ]