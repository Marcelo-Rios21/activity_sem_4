FROM jenkins/jenkins:2.541.3-jdk21

USER root

RUN apt-get update && \
    apt-get install -y maven git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER jenkins