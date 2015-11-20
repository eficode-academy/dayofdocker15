#!/bin/bash
# Run this script as sudo
mkdir -p /opt/containers/jenkins_home 
mkdir -p /opt/containers/artifactory/data
mkdir -p /opt/containers/artifactory/logs
mkdir -p /opt/containers/artifactory/backup
mkdir -p /opt/containers/registry

chown 1000:1000 /opt/containers -R
