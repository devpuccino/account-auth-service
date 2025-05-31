FROM eclipse-temurin:21
WORKDIR /data

RUN ln -fs /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    echo 'Asia/Bangkok' > /etc/timezone

RUN curl -L -O https://artifacts.elastic.co/downloads/logstash/logstash-9.0.1-linux-x86_64.tar.gz && \
    tar xzvf logstash-9.0.1-linux-x86_64.tar.gz && \
    rm -f logstash-9.0.1-linux-x86_64.tar.gz && \
    mv logstash-9.0.1 /data/logstash

COPY ./pipeline.conf /etc/logstash/pipeline.conf
COPY startup.sh /data/startup.sh

RUN chmod +x /data/startup.sh
RUN chmod +x /data/logstash/bin/logstash-plugin
RUN chmod +x /data/logstash/bin/logstash

RUN ./logstash/bin/logstash-plugin install logstash-output-opensearch logstash-filter-multiline logstash-input-beats
RUN groupadd -r logstash && \
    useradd -m -r -g logstash logstash
RUN chown -R logstash:logstash /data/logstash
EXPOSE 5044
WORKDIR /data/logstash
USER logstash
ENTRYPOINT [ "/data/startup.sh" ]