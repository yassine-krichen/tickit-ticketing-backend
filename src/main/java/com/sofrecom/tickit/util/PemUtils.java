package com.sofrecom.tickit.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class PemUtils {
    public static String convertToPem(PublicKey publicKey) {
        String base64Encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----\n");
        int index = 0;
        while (index < base64Encoded.length()) {
            pem.append(base64Encoded, index, Math.min(index + 64, base64Encoded.length()));
            pem.append("\n");
            index += 64;
        }
        pem.append("-----END PUBLIC KEY-----");
        return pem.toString();
    }

    public static String convertToPem(PrivateKey privateKey) {
        String base64Encoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PRIVATE KEY-----\n");
        int index = 0;
        while (index < base64Encoded.length()) {
            pem.append(base64Encoded, index, Math.min(index + 64, base64Encoded.length()));
            pem.append("\n");
            index += 64;
        }
        pem.append("-----END PRIVATE KEY-----");
        return pem.toString();
    }
}
