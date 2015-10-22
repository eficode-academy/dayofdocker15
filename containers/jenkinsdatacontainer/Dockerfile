FROM busybox
# Create a jenkins user with a home directory.
# This command below will create the user ,
# and will also create the home dir /var/jenkins_home with correct ownership and permissions.
# adduser is busybox command. -D means do not assign a password.
# Jenkins user never has to actually log into this container. So the shell is /bin/false.
RUN adduser -u 1000 -h /var/jenkins_home -s /bin/false -D jenkins

# Announce Jenkins home as a volume to service containers using "--volumes-from datacontainer" . 
# So they (service containers) know which volume to mount from this data container.
VOLUME /var/jenkins_home

# For some reason (which I don't know) we need to specify a CMD in the data containers's docker file.
# We just specify something which does not exist at all.
CMD NoCommand

