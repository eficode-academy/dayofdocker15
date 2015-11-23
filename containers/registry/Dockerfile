FROM registry

# The following is needed to fix the timezone:
USER root
ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


# Instructions:
# Build: 
# docker build --rm -t dod/registry .

# Run as service:
#  docker run -d -p 5000:5000 -v /opt/containers/registry:/tmp/registry dod/registry 

