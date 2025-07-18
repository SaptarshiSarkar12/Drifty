FROM oraclelinux:10-slim as runner

RUN microdnf upgrade -y --nodocs && \
    microdnf install libSM-1.2.3 libXext-1.3.4 gtk3-3.24.31 -y --nodocs && \
    microdnf clean all
