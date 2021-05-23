package com.stanum.skrudzh.saltage;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

@Service
@Slf4j
public class SignService {
    private static final String ALGORITHM = "SHA256withRSA";

    @Value("${threebaskets.saltedge.private-path}")
    private String privateKeyFilePath;

    @Value("${threebaskets.saltedge.sign-enabled}")
    private boolean signEnabled;

    private PEMKeyPair privateKey = null;

    SignService() {
    }

    public String sign(byte[] bytes) throws SignatureException {
        Signature signature = null;
        try {
            signature = Signature.getInstance(ALGORITHM);
            signature.initSign(new JcaPEMKeyConverter().getPrivateKey(privateKey.getPrivateKeyInfo()));
            signature.update(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | PEMException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return signature != null
                ? Base64.toBase64String(signature.sign())
                : null;

    }

    @PostConstruct
    public void postConstruct() {
        if(!signEnabled) {
            log.info("Saltedge sign disabled");
            return;
        }
        try {
            File f = new File(privateKeyFilePath);
            FileReader fileReader;
            fileReader = new FileReader(f);
            privateKey = (PEMKeyPair) new PEMParser(fileReader).readObject();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
