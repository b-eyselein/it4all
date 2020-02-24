FROM hseeberger/scala-sbt:11.0.6_1.3.8_2.13.1 as builder

WORKDIR /app/source

COPY . .

# install npm
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y nodejs

RUN sbt packageZipTarball



FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=builder /app/source/target/universal/it4all.tgz /app/it4all.tgz

RUN tar -xzf it4all.tgz

WORKDIR /app/it4all

ENTRYPOINT bin/it4all -Dplay.http.secret.key="faßsdoiuvzyßxiovuzy"
