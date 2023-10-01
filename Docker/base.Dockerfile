FROM debian:bookworm-slim
# Set the working directory
WORKDIR /build

COPY ./config /build/config
RUN apt-get update -y

RUN apt-get install gcc build-essential -y && \
    apt-get install wget tar gzip -y && \
    apt-get install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libfreetype6-dev \
    libgl-dev libglib2.0-dev libgtk-3-dev libpango1.0-dev libx11-dev libxtst-dev zlib1g-dev -y --no-install-recommends &&\
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download and install Maven 3.8.8
RUN wget https://apache.osuosl.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz && \
    tar -xzf apache-maven-3.8.8-bin.tar.gz && \
    rm apache-maven-3.8.8-bin.tar.gz

ENV PATH="/build/apache-maven-3.8.8/bin:$PATH"

# Download and install GraalVM
RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-20.0.2/graalvm-community-jdk-20.0.2_linux-x64_bin.tar.gz && \
    tar -xzf graalvm-community-jdk-20.0.2_linux-x64_bin.tar.gz &&\
    rm graalvm-community-jdk-20.0.2_linux-x64_bin.tar.gz

ENV JAVA_HOME="/build/graalvm-community-openjdk-20.0.2+9.1"
ENV GRAALVM_HOME="/build/graalvm-community-openjdk-20.0.2+9.1"
ENV PATH="/build/graalvm-community-openjdk-20.0.2+9.1/bin:$PATH"

