FROM python:3.12-slim as runner

RUN apt-get update --no-install-recommends && apt-get install ffmpeg libsm6 libxext6 libgtk-3-0 -y --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
