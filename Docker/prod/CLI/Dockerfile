FROM --platform=linux/amd64 python:3.11-slim as runner

WORKDIR /app
COPY . .
RUN chmod +x ./Drifty_CLI

CMD [ "/app/Drifty_CLI" ]