FROM python:3.11-slim as runner

WORKDIR /app

RUN apt-get update --no-install-recommends && apt-get install ffmpeg libsm6 libxext6 libgtk-3-0 wget -y --no-install-recommends
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
RUN wget https://github.com/SaptarshiSarkar12/Drifty/releases/download/VERSION/Drifty-GUI_linux -O Drifty_GUI &&\
    chmod +x ./Drifty_GUI

CMD /app/Drifty_GUI