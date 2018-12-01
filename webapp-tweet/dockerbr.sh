#!/bin/bash

sudo docker build --tag=webapp-tweet .
sudo docker run -it --rm -p 8080:8080 -p 9990:9990 -p 8787:8787 -v /home/twitter/Bureau/syam/webapp-tweet/target:/opt/jboss/wildfly/standalone/deployments/ webapp-tweet
