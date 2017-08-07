FROM docker:17.05-git

MAINTAINER Bjoern Eyselein <bjoern.eyselein@gmail.com>

ARG USERNAME
ARG DOCKER_GID

# Java
ENV LANG=C.UTF-8 JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk JAVA_VERSION=8u121 JAVA_ALPINE_VERSION=8.121.13-r0 SBT_VERSION=0.13.15 SBT_HOME=/usr/local/sbt

RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home

ENV PATH $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin:${SBT_HOME}/bin

RUN set -x \
	&& apk add --no-cache openjdk8="$JAVA_ALPINE_VERSION" \
	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]
	
# SBT - gafiatulin/alpine-sbt

RUN apk add --no-cache --update bash wget && mkdir -p "$SBT_HOME" && \
    wget -qO - --no-check-certificate "https://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" | tar xz -C $SBT_HOME --strip-components=1 && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built
    
# Play Framework specifics
WORKDIR /app

RUN addgroup -g $DOCKER_GID docker && \
	addgroup -g 1000 $USERNAME && \
	adduser -u 1000 -S -G docker -G $USERNAME $USERNAME && \
	chown $USERNAME:$USERNAME /app

USER $USERNAME:$DOCKER_GID

EXPOSE 9000
