package com.rizky.pubkeyinfra.cli;

import java.io.File;
import java.io.FileReader;
import java.util.Base64;
import java.util.concurrent.Callable;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = "OcspCheck",mixinStandardHelpOptions = true,
description = "OCSP Checking for Certificate Valid/Not")
public class OcspCheck implements Callable<Integer>{

    @Option(names= {"-c","--certPem"},
            description = "PEM File of certificate to check",required = true)
    File certPem;

    @Option(names= {"-i","--issuerPem"},
            description = "PEM File of certificate's Issuer(CA)",required = true)
    File issuerPem;

    @Option(names= {"-u","--urlOcsp"},
            description = "Url of the OCSP Responder",required = true)
    String urlOcsp;

    Logger logApp=LoggerFactory.getLogger(OcspCheck.class);

    public Integer call() throws Exception {

        PEMParser readerCert=null;
        PEMParser readerIssuer=null;
        try {
            OCSPReqBuilder  reqBuilder=new OCSPReqBuilder();

            readerCert=new PEMParser(new FileReader(certPem));

            readerIssuer=new PEMParser(new FileReader(issuerPem));


            X509CertificateHolder cert1=(X509CertificateHolder) readerCert.readObject();
            X509CertificateHolder cert2=(X509CertificateHolder) readerIssuer.readObject();

            DigestCalculator digestCalc = new BcDigestCalculatorProvider().get(CertificateID.HASH_SHA1);
            CertificateID certId=new CertificateID(digestCalc, cert2, cert1.getSerialNumber());
            String reqString=Base64.getEncoder().encodeToString(
                    reqBuilder.addRequest(certId).build().getEncoded());

            String urlToCall=urlOcsp+"/{ocsp}";
            byte[] resp1=Unirest.get(urlToCall)
                    .routeParam("ocsp",reqString)
                    .asBytes().getBody();

            OCSPResp ocspResp=new OCSPResp(resp1);

            if(ocspResp.getStatus()!=0) {
                logApp.error("Response Status Error {} from OCSPResponder",ocspResp.getStatus());
                return 10;
            }


            BasicOCSPResp basicOcsp=(BasicOCSPResp) ocspResp.getResponseObject();
            SingleResp[] singleResp1=basicOcsp.getResponses();

            if(singleResp1.length<1)    {
                logApp.error("Response Empty from OCSPResponder",ocspResp.getStatus());
                return 10;
            }

            if(singleResp1[0].getCertStatus()==null)    {
                logApp.info("Certificate SN {} valid as of {} ",
                        cert1.getSerialNumber().toString(16),
                        singleResp1[0].getThisUpdate());
            }
            else    {
                logApp.info("Certificate SN {} not valid as of {} ",
                        cert1.getSerialNumber().toString(16),
                        singleResp1[0].getThisUpdate());
            }

            return 0;
        }
        catch(Exception e)  {
            throw e;
        }
        finally {
            readerCert.close();readerIssuer.close();
        }


    }


}
