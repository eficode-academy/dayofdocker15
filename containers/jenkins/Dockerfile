FROM jenkins

# There was no need for a custom image when jenkins official image was sufficient.

# However there are two primary reasons:
# 1) Need to fix timezone
# 2) Need to add plugins to Jenkins
# So to make that work, we need a Dockerfile for jenkins and create our own jenkins image.
# named : dayofdocker/jenkins


# The following is necessary to set the correct time zone in the container:
USER root
ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
USER jenkins
## VOLUME /var/jenkins_home 


# The following passes the following parameter to the jenkins container. 
# This means we do not need to pass this to the run command.
CMD ["--prefix=/jenkins"]

# Add plugins to Jenkins:
COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt


# built it with: docker build --rm -t dayofdocker/jenkins .
