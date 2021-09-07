FROM quay.io/quarkus/centos-quarkus-maven:21.1.0-java11
USER root
COPY graalvm.tar.gz /graalvm.tar.gz
RUN mkdir /graalvm/ && tar xf /graalvm.tar.gz -C /graalvm
RUN cp -rf /graalvm/graalvm-ee-java11-21.2.0.1/* /opt/graalvm/