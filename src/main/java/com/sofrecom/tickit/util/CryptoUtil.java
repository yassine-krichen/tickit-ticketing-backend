package com.sofrecom.tickit.util;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class CryptoUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    /**
     * Constructs a CryptoUtil instance using provided keys.
     * In a real-world scenario, you might load these from a keystore or configuration.
     */
    public CryptoUtil(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * Computes a SHA-256 hash from the given input values.
     * 
     * @param data Inputs to be hashed (e.g., encrypted snapshot, previous hash, timestamp, etc.)
     * @return Base64-encoded hash string.
     */
    public String computeHash(String... data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String part : data) {
                digest.update(part.getBytes(StandardCharsets.UTF_8));
            }
            byte[] hashBytes = digest.digest();
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Signs the provided data using the private key.
     * 
     * @param data The data to be signed.
     * @return Base64-encoded digital signature.
     */
    public String signData(String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA"); // or "SHA256withECDSA"
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("Error signing data", e);
        }
    }

    /**
     * Verifies the digital signature for the given data.
     * 
     * @param data The original data.
     * @param signatureStr The Base64-encoded signature to verify.
     * @return true if the signature is valid, false otherwise.
     */
    public boolean verifySignature(String data, String signatureStr) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA"); // or "SHA256withECDSA"
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("Error verifying signature", e);
        }
    }

    
}
