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
