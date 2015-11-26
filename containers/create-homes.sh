#!/bin/bash
# Run this script as sudo
echo "You should not be running any containers in /opt/containers when you run this script."
echo "Removing old directory structure from /opt/containers"
rm /opt/containers/*
echo "Re-creating directory structure inside /opt/containers"
mkdir -p /opt/containers/jenkins_home 
mkdir -p /opt/containers/artifactory/data
mkdir -p /opt/containers/artifactory/logs
mkdir -p /opt/containers/artifactory/backup
mkdir -p /opt/containers/registry
echo "Changing ownership of /opt/containers to 1000:1000 recursively"
chown 1000:1000 /opt/containers -R
echo "Done."
