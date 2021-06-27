package com.rizky.pubkeyinfra.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ReadCRL",mixinStandardHelpOptions = true,
description = "Read CRL File and Display Revoked Certificate's Serial Number")
public class ReadCRL implements Callable<Integer> {

    @Option(names= {"-c","--crlFile"},
            description = "CRL File to Check (DER Format)")
    File crlFile;

    @Option(names= {"-u","--crlUrl"},
            description = "URL of the CRL (DER Format)")
    String urlCRL;
    Logger logApp=LoggerFactory.getLogger(ReadCRL.class);

    public Integer call() throws Exception {

        if(crlFile!=null)  {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(new FileInputStream(crlFile.getAbsolutePath()));
            logApp.info("Revoked Certificate SN as of {}:",crl.getThisUpdate());
            crl.getRevokedCertificates().forEach(a->System.out.println(a.getSerialNumber().toString(16)));
            return 0;
        }
        else if(urlCRL!=null)   {
            byte[] resp=Unirest.get(urlCRL).asBytes().getBody();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream(resp));
            logApp.info("Revoked Certificate SN as of {}:",crl.getThisUpdate());
            crl.getRevokedCertificates().forEach(a->System.out.println(a.getSerialNumber().toString(16)));
            return 0;
        }
        else  {
            logApp.error("Must passed parameter of CRLFile or URL of the CRL");
            throw new Exception();

        }

    }



}
