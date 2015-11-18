FROM centos:6.7

# Set the correct timezone:
ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Exposes port 22 for incoming SSH connections.
EXPOSE 22

## The following line is not needed on centos. It is needed on ubuntu.
## RUN mkdir /var/run/sshd

# We do not install git through yum because it is git-1.7 . For the excercises we need git-1.8 or later.
RUN yum -y groupinstall "Development Tools" \
 && yum -y install wget tar cpan vim-minimal vim-enhanced csh openssh-server java-1.8.0-openjdk-headless \
      zlib-devel \
 && yum -y update && yum clean all 

## Download latest git and save it inside /tmp/. Compile it.
RUN cd /tmp && wget https://github.com/git/git/archive/v2.4.6.tar.gz && tar xzf v2.4.6.tar.gz && cd git-2.4.6 && make configure && ./configure --prefix=/usr && make all && make install && cd /


# Setup SSH host keys, which are required for sshd to start properly.
RUN /usr/bin/ssh-keygen -q -t rsa1 -f /etc/ssh/ssh_host_key     -C '' -N '' >&/dev/null
RUN /usr/bin/ssh-keygen -q -t rsa  -f /etc/ssh/ssh_host_rsa_key -C '' -N '' >&/dev/null
RUN /usr/bin/ssh-keygen -q -t dsa  -f /etc/ssh/ssh_host_dsa_key -C '' -N '' >&/dev/null

RUN useradd -m builduser \
  && echo "builduser:dayofdocker" | /usr/sbin/chpasswd

COPY dotgitconfig /home/builduser/.gitconfig


######################################################################################
# Use image as:
# sudo docker run --rm -it  dod/centos:6.7 bash

# Build this image with: 
# sudo docker build --rm -t dod/centos:6.7 .


