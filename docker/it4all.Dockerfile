FROM openjdk:8

MAINTAINER Björn Eyselein <bjoern.eyselein@uni-wuerzburg.de>

RUN apt update && apt -y dist-upgrade && apt -y autoremove \
	curl -O https://downloads.typesafe.com/typesafe-activator/1.3.12/typesafe-activator-1.3.12.zip \
	unzip typesafe-activator-1.3.12 -d / && rm typesafe-activator-1.3.12 && chmod a+x /activator-1.3.12/activator \
	mkdir /app

ENV PATH $PATH:/activator-1.3.12

EXPOSE 9000 8888

WORKDIR /app

CMD ["activator", "run"]