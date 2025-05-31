FROM vegardit/graalvm-maven:latest-java21 AS builder
WORKDIR /build
COPY . /build
RUN mvn clean -Pnative native:compile

FROM ghcr.io/graalvm/graalvm-community:21

ARG JAVA_OPTS="-Xms100m -Xmx200m"
ENV APP_NAME=account-auth-service
ENV JAVA_OPTS=$JAVA_OPTS
ENV SPRING_PROFILES_ACTIVE=production
ENV LOGSTASH_HOSTS='["logstash:5044"]'
RUN ln -sf /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    echo "Asia/Bangkok" > /etc/timezone

COPY --from=builder /build/target/$APP_NAME /data/$APP_NAME/application
RUN chmod +x /data/$APP_NAME/application

WORKDIR /data/$APP_NAME
#install filebeat
RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-oss-9.0.1-linux-x86_64.tar.gz && \
    tar xzvf filebeat-oss-9.0.1-linux-x86_64.tar.gz && \
    rm -f filebeat-oss-9.0.1-linux-x86_64.tar.gz && \
    mv filebeat-9.0.1-linux-x86_64 /data/$APP_NAME/filebeat

COPY ./filebeat.yml /data/$APP_NAME/filebeat/filebeat.yml
COPY ./startup.sh /data/$APP_NAME/startup.sh
RUN chmod +x startup.sh
EXPOSE 8080
ENTRYPOINT [ "./startup.sh" ]