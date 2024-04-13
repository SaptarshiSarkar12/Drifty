FROM oraclelinux:9-slim as runner

RUN microdnf upgrade -y && microdnf install libSM libXext gtk3 -y --nodocs && \
    microdnf clean all
