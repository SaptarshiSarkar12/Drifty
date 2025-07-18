FROM oraclelinux:10-slim as runner

RUN microdnf upgrade -y --nodocs && \
    microdnf install libSM-1.2.4 libXext-1.3.6 gtk3-3.24.43 -y --nodocs && \
    microdnf clean all
