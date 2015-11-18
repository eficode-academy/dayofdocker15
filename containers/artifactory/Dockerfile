FROM mattgruter/artifactory

# It is better to use use the mattgruter/artifactory and pull a new one when you want to upgrade.

# The following is needed to fix the timezone:
USER root
ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

