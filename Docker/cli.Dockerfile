FROM python:3.11-slim as runner

WORKDIR /app

RUN apt-get update --no-install-recommends && apt-get install wget -y --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    wget https://github.com/SaptarshiSarkar12/Drifty/releases/download/VERSION/Drifty-CLI_linux -O Drifty_CLI &&\
    chmod +x ./Drifty_CLI

CMD /app/Drifty_CLI