FROM drifty-base as build

WORKDIR /app

COPY . .

RUN gcc -c /build/config/missing_symbols.c -o /build/config/missing_symbols-ubuntu-latest.o
RUN mvn -Pbuild-drifty-cli-for-ubuntu-latest package

FROM python:3.11-slim

WORKDIR /app
COPY --from=build /app/target/CLI/linux /app

CMD [ "/app/Drifty CLI" ]
