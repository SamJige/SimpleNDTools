#!/bin/bash
set -e

rm -rf log

docker run -i --rm --name SimpleNDTools \
  -v $(pwd)/log/:/var/my_logs \
  -v $(pwd):/sft/ \
  --net=host \
  -w /sft/ java:8u111-jdk-alpine java -jar SimpleNDTools-1.0.jar
