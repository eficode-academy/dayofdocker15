FROM ubuntu:15.10
MAINTAINER Jan Krag "jak@praqma.net"

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update \
    && apt-get --no-install-recommends -y install \
    siege \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists 

COPY .siegerc /root/
 
ENTRYPOINT ["siege"]
CMD ["--help"]