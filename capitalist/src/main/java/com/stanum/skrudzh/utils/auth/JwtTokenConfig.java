package com.stanum.skrudzh.utils.auth;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class JwtTokenConfig {

    private final String issuedBy;
    private final int lifetime;
    private final PrivateKey signingKey;
    private final RSAPublicKey publicKey;

    private final String publicKeyString;

    public static final String USER_ID_CLAIM_KEY = "id";


    public JwtTokenConfig(String ib, int lt, PrivateKey sk, RSAPublicKey pk) {
        issuedBy = ib;
        lifetime = lt;
        signingKey = sk;
        publicKey = pk;

        Base64.Encoder encoder = Base64.getEncoder();

        publicKeyString =
                "-----BEGIN PUBLIC KEY-----\n" +
                        String.join("\n", encoder.encodeToString(publicKey.getEncoded()).split("(?<=\\G.{64})")) +
                        "\n-----END PUBLIC KEY-----\n";
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public int getLifetime() {
        return lifetime;
    }

    public PrivateKey getSigningKey() {
        return signingKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }


}