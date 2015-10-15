SSL setup for Apache:
--------------------

There are three files which we receive from Novelda System Admin.
* DigiCertCA.crt
* star_novelda_no.crt
* star_novelda_no.key

Apache uses all three files with three different directives in its ssl.conf file:
    SSLEngine On
    SSLCertificateFile /etc/apache2/certs/star_novelda_no.crt
    SSLCertificateKeyFile /etc/apache2/certs/star_novelda_no.key
    SSLCertificateChainFile /etc/apache2/certs/DigiCertCA.crt

