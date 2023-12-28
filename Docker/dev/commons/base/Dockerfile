FROM debian:bookworm-slim

WORKDIR /build

COPY ./config /build/config
RUN apt-get update -y

RUN apt-get install gcc build-essential -y && \
    apt-get install wget tar gzip -y && \
    apt-get install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libfreetype6-dev \
    libgl-dev libglib2.0-dev libgtk-3-dev libpango1.0-dev libx11-dev libxtst-dev zlib1g-dev -y --no-install-recommends &&\
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download and install Maven 3.8.8 GraalVM
RUN wget https://apache.osuosl.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz && \
    wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.1/graalvm-community-jdk-21.0.1_linux-x64_bin.tar.gz && \
    tar -xzf apache-maven-3.8.8-bin.tar.gz && \
    tar -xzf graalvm-community-jdk-21.0.1_linux-x64_bin.tar.gz &&\
    rm apache-maven-3.8.8-bin.tar.gz && \
    rm graalvm-community-jdk-21.0.1_linux-x64_bin.tar.gz

# Setting environment variables
ENV PATH="/build/apache-maven-3.8.8/bin:$PATH"
ENV JAVA_HOME="/build/graalvm-community-openjdk-21.0.1+12.1"
ENV GRAALVM_HOME="/build/graalvm-community-openjdk-21.0.1+12.1"
ENV PATH="/build/graalvm-community-openjdk-21.0.1+12.1/bin:$PATH"