package com.rizky.pubkeyinfra.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Callable;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "Timestamp",mixinStandardHelpOptions = true,
description = "Send timestamp request")
public class TimestampService implements Callable<Integer>{

    @Option(names= {"-f","--fileToHash"},
            description = "File to be hashed")
    File fileName;

    @Option(names= {"-t","--tsrFile"},
            description = "TSR File Output",required = true)
    File tsrFileName;



    @Option(names= {"-a","--hashAlgo"},
            description = "Hash Algorithm to use (SHA-224/SHA-256/SHA-512)")
    String hashAlgorithm="SHA-256";

    @Option(names= {"-u","--url"},
            description = "TSA Server URL")
    String tsaUrl;

    @Option(names= {"-hd","--hashedData"},
            description = "Hashed Data")
    String hashedData;

    Logger logApp=LoggerFactory.getLogger(TimestampService.class);

    public Integer call() throws Exception {

        // Get File Hash
        byte[] hashResult=null;
        if(hashedData==null)    {
            MessageDigest md=MessageDigest.getInstance(hashAlgorithm);
            hashResult=md.digest(
                    Files.readAllBytes(Paths.get(fileName.getAbsolutePath())));
        }
        else    {
            hashResult=Base64.getDecoder().decode(hashedData.getBytes());
        }
        
        AlgorithmIdentifier algoOid=null;
        if(hashAlgorithm.equals("SHA-256")) {
            algoOid=new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        }
        else if(hashAlgorithm.equals("SHA-512"))    {
            algoOid=new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);
        }
        else if(hashAlgorithm.equals("SHA-1"))  {
            algoOid=new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224);
        }
        else    {
            logApp.error("Hash Algorithm is not supported");
            return 10;
        }

        MessageImprint mi=new MessageImprint(algoOid, hashResult);
        TimeStampReq req=new TimeStampReq(mi, null, null, ASN1Boolean.TRUE, null);
        byte[] resp=Unirest.post(tsaUrl).contentType("application/timestamp-query")
                .body(req.getEncoded()).asBytes().getBody();

        ASN1StreamParser asn1Sp = new ASN1StreamParser(resp);
        TimeStampResp tspResp = TimeStampResp.getInstance(asn1Sp.readObject());
        TimeStampResponse tsr = new TimeStampResponse(tspResp);
        Files.write(Paths.get(tsrFileName.getAbsolutePath()), 
                tsr.getEncoded(), StandardOpenOption.CREATE_NEW);
        TimeStampToken token = tsr.getTimeStampToken();
        logApp.info("TSA at:{}",token.getTimeStampInfo().getGenTime());

        return 0;

    }


}
