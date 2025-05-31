#!/bin/bash
{ ./application $JAVA_OPTS 2>&1 | tee /dev/stderr; } | ./filebeat/filebeat -e -c /data/$APP_NAME/filebeat/filebeat.yml > /dev/null 2>&1