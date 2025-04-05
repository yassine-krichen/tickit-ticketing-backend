package com.sofrecom.tickit.config;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sofrecom.tickit.util.CryptoUtil;

@Configuration
public class CryptoConfig {

    @Value("${crypto.private-key}")
    private String privateKeyString;

    @Value("${crypto.public-key}")
    private String publicKeyString;

    @Bean
    public CryptoUtil cryptoUtil() {
        try {
            // Remove header/footer and line breaks if needed
            String privateKeyPEM = privateKeyString
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            String publicKeyPEM = publicKeyString
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyPEM);
            byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateKey));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedPublicKey));

            return new CryptoUtil(privateKey, publicKey);
        } catch (Exception e) {
            throw new RuntimeException("Error creating CryptoUtil bean", e);
        }
    }
    
}
