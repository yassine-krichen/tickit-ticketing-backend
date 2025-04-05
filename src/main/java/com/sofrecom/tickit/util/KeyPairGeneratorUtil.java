package com.sofrecom.tickit.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairGeneratorUtil {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Key size: 2048 bits is common for RSA
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Use keyPair.getPrivate() and keyPair.getPublic() as needed
    }
}
