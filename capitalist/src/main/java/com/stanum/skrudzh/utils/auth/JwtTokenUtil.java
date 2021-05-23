package com.stanum.skrudzh.utils.auth;

import io.jsonwebtoken.*;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtTokenUtil {

    public static String issueJWTToken(Map<String, Object> claims, String issuedBy, PrivateKey signKey) {
        return issueJWTToken(claims, issuedBy, signKey, 24, SignatureAlgorithm.RS512);
    }

    public static String issueJWTToken(Map<String, Object> claims, String issuedBy, PrivateKey signKey, int lifetimeHours) {
        return issueJWTToken(claims, issuedBy, signKey, lifetimeHours, SignatureAlgorithm.RS512);
    }

    public static String issueJWTToken(Map<String, Object> claims, String issuedBy, PrivateKey signKey, int lifetimeHours, SignatureAlgorithm algo) {

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(issuedBy)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(lifetimeHours) * 3600L * 1000L))
                .signWith(algo, signKey).compact();
    }

    public static Jws<Claims> getClaims(String token, Key keyToCheckAgainst) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(keyToCheckAgainst).parseClaimsJws(token);
        } catch (Exception e) {
            if (
                    e instanceof MalformedJwtException ||
                            e instanceof ExpiredJwtException ||
                            e instanceof SignatureException) {
                return null;
            }
            throw e;
        }

        return claimsJws;
    }

    public static RSAPublicKey getJwtPublicKey(String jwtPubKey) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encodedKey = decoder.decode(jwtPubKey);
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
