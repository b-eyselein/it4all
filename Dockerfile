
FROM docker:17.05-git

MAINTAINER Bjoern Eyselein <bjoern.eyselein@gmail.com>

ARG $USERNAME

# Java
ENV LANG C.UTF-8

RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin:${SBT_HOME}/bin
ENV SBT_VERSION 0.13.15
ENV SBT_HOME /usr/local/sbt

ENV JAVA_VERSION 8u121
ENV JAVA_ALPINE_VERSION 8.121.13-r0

RUN set -x \
	&& apk add --no-cache \
		openjdk8="$JAVA_ALPINE_VERSION" \
	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]
	
# SBT
RUN apk add --no-cache --update bash wget && mkdir -p "$SBT_HOME" && \
    wget -qO - --no-check-certificate "https://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" | tar xz -C $SBT_HOME --strip-components=1 && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built
    
# Play Framework specifics
WORKDIR app

RUN addgroup $USERNAME && addUSERNAME -S -G $USERNAME $USERNAME && \
	chown $USERNAME:$USERNAME /app

USER $USERNAME:$USERNAME

EXPOSE 9000
