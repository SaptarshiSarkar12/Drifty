FROM --platform=linux/amd64 python:3.11-slim as runner

WORKDIR /app
COPY . .
RUN apt-get update --no-install-recommends && apt-get install ffmpeg libsm6 libxext6 libgtk-3-0 -y --no-install-recommends
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
RUN chmod +x ./Drifty_GUI

CMD /app/Drifty_GUI