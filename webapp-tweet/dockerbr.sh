#!/bin/bash

mvn clean package
sudo docker build --tag=webapp-tweet .
