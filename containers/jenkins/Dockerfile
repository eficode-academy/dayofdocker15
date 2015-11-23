FROM jenkins

# There was no need for a custom image when jenkins official image was sufficient.

# However we need to add plugins to Jenkins
# So to make that work, we need a Dockerfile for jenkins and create our own jenkins image.


# The following passes the following parameter to the jenkins container. 
# This means we do not need to pass this to the run command.
CMD ["--prefix=/jenkins"]

# Add plugins to Jenkins:
COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt


# built it with: docker build --rm -t dod/jenkins .
