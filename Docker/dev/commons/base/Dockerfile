FROM debian:bookworm-slim

WORKDIR /build

COPY ./config /build/config
RUN apt-get update -y --no-install-recommends

RUN apt-get install gcc build-essential -y --no-install-recommends && \
    apt-get install wget tar gzip -y --no-install-recommends && \
    apt-get install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libfreetype6-dev \
    libgl-dev libglib2.0-dev libgtk-3-dev libpango1.0-dev libx11-dev libxtst-dev zlib1g-dev -y --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

ARG GRAALVM_VERSION_MAJOR
ARG MAVEN_MAJOR
ARG MAVEN_VERSION

# Certificates are not checked to avoid issues with SSL. Both the domains are trusted and are not malicious.
# Also, this is a development environment and not a production environment.
RUN wget --progress=bar:force --no-check-certificate https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    wget --progress=bar:force --no-check-certificate https://download.oracle.com/graalvm/${GRAALVM_VERSION_MAJOR}/latest/graalvm-jdk-${GRAALVM_VERSION_MAJOR}_linux-x64_bin.tar.gz && \
    tar -xzf apache-maven-*.tar.gz -C /opt && \
    tar -xzf graalvm-jdk-*.tar.gz -C /opt &&\
    ln -s /opt/apache-maven* /opt/maven &&\
    ln -s /opt/graalvm-* /opt/graalvm &&\
    rm apache-maven-*.tar.gz && \
    rm graalvm-jdk-*.tar.gz

# Setting environment variables
ENV GRAALVM_HOME="/opt/graalvm"
ENV JAVA_HOME="/opt/graalvm"
ENV PATH="/opt/graalvm/bin:/opt/maven/bin:$PATH"
