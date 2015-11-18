This was the directory originally used to hold home directories. But now the home / data directories for jenkins and artifactory are relocated to /opt/containers.
Those directories (complete tree below /opt/containers must be owned by uid 1000 and gid 1000, irrespective of the user and group (1000) exist on the system or not.

.
├── artifactory
│   ├── backup
│   ├── data
│   └── logs
└── jenkins_home


