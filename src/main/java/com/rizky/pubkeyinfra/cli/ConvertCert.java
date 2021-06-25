package com.rizky.pubkeyinfra.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ConvertCert",mixinStandardHelpOptions = true,
description = "Convert Certificate Format")
public class ConvertCert implements Callable<Integer> {

    @Option(names= {"-p","--pemFile"},
            description = "PEM File of certificate",required = true)
    File certPem;

    @Option(names= {"-d","--derFile"},
            description = "DER File of certificate",required = true)
    File certDer;

    @Option(names= {"-dp","--derToPem"},description = "Convert DER to PEM")
    boolean derToPem;

    @Option(names= {"-pd","--pemToDer"},description = "Convert PEM to DER")
    boolean pemToDer;

    Logger logApp=LoggerFactory.getLogger(ConvertCert.class);

    public Integer call() throws Exception {

        if(derToPem)    {
            derToPem(certDer);
            logApp.info("Converting into PEM file {}",certPem.getAbsolutePath());
            return 0;
        }
        else    {
            pemToDer(certPem);
            logApp.info("Converting into DER file {}",certDer.getAbsolutePath());
            return 0;
        }
        
    }

    private void derToPem(File derFile) throws Exception  {


        String pemContent=derToPem(Base64.getEncoder().encodeToString(
                Files.readAllBytes(
                        Paths.get(derFile.getAbsolutePath()))));
        Files.write(Paths.get(certPem.getAbsolutePath()), 
                pemContent.getBytes(), StandardOpenOption.CREATE_NEW);
    }



    private  String derToPem(String derString)   {
        StringBuilder sb2=new StringBuilder("-----BEGIN CERTIFICATE-----"+System.lineSeparator());
        int i=0;
        while(i<derString.length())    {
            int start=i;
            int end=i+64;
            if(derString.length()<end)    {
                end=derString.length();
            }
            sb2.append(derString.substring(start,end)+System.lineSeparator());
            i+=64;
        }
        sb2.append("-----END CERTIFICATE-----");

        return sb2.toString();
    }

    private void pemToDer(File pemFile) throws IOException {
        byte[] pemContent=Files.readAllBytes(Paths.get(pemFile.getAbsolutePath()));
        pemToDer(new String(pemContent));
    }

    private void pemToDer(String pemString) throws IOException   {
        String derString=pemString.replaceAll(System.lineSeparator(), "")
                .replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "");
        Files.write(Paths.get(certDer.getAbsolutePath()), 
                Base64.getDecoder().decode(derString.getBytes()),
                StandardOpenOption.CREATE_NEW);

    }

}
