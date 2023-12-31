FROM debian:bookworm-slim

WORKDIR /build

COPY ./config /build/config
RUN apt-get update -y

RUN apt-get install gcc build-essential -y && \
    apt-get install wget tar gzip -y && \
    apt-get install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libfreetype6-dev \
    libgl-dev libglib2.0-dev libgtk-3-dev libpango1.0-dev libx11-dev libxtst-dev zlib1g-dev -y --no-install-recommends &&\
    apt-get clean && rm -rf /var/lib/apt/lists/*

ARG GRAAL_VERSION
ARG MAVEN_MAJOR
ARG MAVEN_VERSION

RUN wget https://apache.osuosl.org/maven/maven-${MAVEN_MAJOR}/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-${GRAAL_VERSION}/graalvm-community-jdk-${GRAAL_VERSION}_linux-x64_bin.tar.gz && \
    tar -xzf apache-maven*.tar.gz -C /opt && \
    tar -xzf graalvm-community-jdk*.tar.gz -C /opt &&\
    ln -s /opt/apache-maven* /opt/maven &&\
    ln -s /opt/graalvm-community* /opt/graalvm &&\
    rm apache-maven*.tar.gz && \
    rm graalvm-community-jdk*.tar.gz

# Setting environment variables
ENV GRAALVM_HOME="/opt/graalvm"
ENV JAVA_HOME="/opt/graalvm"
ENV PATH="/opt/graalvm/bin:/opt/maven/bin:$PATH"
