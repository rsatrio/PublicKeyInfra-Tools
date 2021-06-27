
# Public Key Infrastructure CLI Tools

A simple CLI software for Public Key Infrastructure-related commands.

## Features
- OCSP Checking of Certificate to OCSP Responder,
- Convert certificate from DER to PEM,
- Convert certificate from PEM to DER
- Send timestamp request (RFC3161)
- Read CRL File (DER Encoded)

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
- This example used to send timestamp request using file as input: 
```shell
java -jar PubKeyInfra-CLI.jar Timestamp -a SHA-256 -f d:\file_to_hashed -u http://tsaurl -t d:\tsrFile.tsr
```

- This example used to send timestamp request using hashed data: 
```shell
java -jar PubKeyInfra-CLI.jar Timestamp -a SHA-256 -hd 85777f270ad7cf2a790981bbae3c4e484a1dc55e24a77390d692fbf1cffa12fa -u http://tsaurl -t d:\tsrFile.tsr
```

- This example used to send timestamp request using hashed data: 
```shell
java -jar PubKeyInfra-CLI.jar ReadCRL -u http://crlexample.crl
```

- This example used to send timestamp request using hashed data: 
```shell
java -jar PubKeyInfra-CLI.jar ReadCRL -c d:\test.crl
```

## Feedback
For feedback and feature request, please raise issues in the issue section of the repository. Enjoy!!.