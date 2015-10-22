There is no home directories, as the service containers are using data containers to store their persistent data.

The persistent data from any particular data container is extracted using "tar" and stored in a folder called "databackup". The data from this "databackup" can be used to restore data in a data container.


