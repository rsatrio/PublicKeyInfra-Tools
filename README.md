
# Public Key Infrastructure CLI Tools

A simple CLI software for Public Key Infrastructure-related commands.

## Features
- OCSP Checking of Certificate to OCSP Responder,
- Convert certificate from DER to PEM,
- Convert certificate from PEM to DER
- Send timestamp request (RFC3161)

## Build
Use mvn package to build the module into jar file
```shell
mvn clean package
```


## Usage
- This example used to check validity of a certificate through OCSP:
```shell
java -jar PubKeyInfra-CLI.jar OcspCheck -c d:\test.pem -i d:\ca.pem -u http://va.example.org
```
- This example used to convert DER certificate to PEM format: 
```shell
java -jar PubKeyInfra-CLI.jar ConvertCert -dp -d d:\original.der -p d:\output.pem
```
- This example used to convert DER certificate to PEM format: 
```shell
java -jar PubKeyInfra-CLI.jar ConvertCert -pd -p d:\original.pem -d d:\output.der
```
- This example used to send timestamp request: 
```shell
java -jar PubKeyInfra-CLI.jar Timestamp -a SHA-256 -f d:\file_to_hashed -u http://tsaurl -t d:\tsrFile.tsr
```


## Feedback
For feedback and feature request, please raise issues in the issue section of the repository. Enjoy!!.