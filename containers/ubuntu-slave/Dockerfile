FROM ubuntu:14.04


# Exposes port 22 for incoming SSH connections.
EXPOSE 22

## The following line is not needed on centos. It is needed on ubuntu.
RUN mkdir /var/run/sshd


RUN apt-get update && apt-get -y install openssh-server openjdk-7-jdk git wget tar build-essential golang 


## Setup SSH host keys, which are required for sshd to start properly.
# RUN /usr/bin/ssh-keygen -q -t rsa1 -f /etc/ssh/ssh_host_key     -C '' -N '' >&/dev/null
# RUN /usr/bin/ssh-keygen -q -t rsa  -f /etc/ssh/ssh_host_rsa_key -C '' -N '' >&/dev/null
# RUN /usr/bin/ssh-keygen -q -t dsa  -f /etc/ssh/ssh_host_dsa_key -C '' -N '' >&/dev/null
# RUN /usr/bin/ssh-keygen -q -t ecdsa -f /etc/ssh/ssh_host_ecdsa_key -C '' -N '' >&/dev/null
# RUN /usr/bin/ssh-keygen -q -t ed25519 -f /etc/ssh/ssh_host_ed25519_key -C '' -N '' >&/dev/null

# This is kinf of a hack. For DayofDocker Bosse wants to run docker commands.
# The builduser logs in through ssh (not as root) so builduser needs to be a member of the group docker 
# The docker group on the Zetta host has the id 999. 
# When this user is created as following,  it will already be a member of docker group.
# I use 999 for a reason. If you decide to mount the groups file from the host, that will also have the same group id.

RUN groupadd -g 999 docker
RUN useradd -G 999 -m builduser \
  && echo "builduser:dayofdocker" | /usr/sbin/chpasswd

COPY dotgitconfig /home/builduser/.gitconfig


# Allow root to login
# RUN sed -i "s/PermitRootLogin without-password/PermitRootLogin yes/g"  /etc/ssh/sshd_config 

############################################################################
# build it with: 
# sudo docker build --rm -t dod/ubuntu-slave .

