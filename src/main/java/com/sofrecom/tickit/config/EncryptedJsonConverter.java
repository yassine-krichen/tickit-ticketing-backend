package com.sofrecom.tickit.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.jasypt.util.text.AES256TextEncryptor;

@Converter
public class EncryptedJsonConverter implements AttributeConverter<JsonNode, String> {

    private static final AES256TextEncryptor encryptor = new AES256TextEncryptor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        encryptor.setPassword("YourStrongSecretKey"); // Replace with env variable
    }

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        if (attribute == null) return null;
        try {
            String jsonString = objectMapper.writeValueAsString(attribute);
            return encryptor.encrypt(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("JSON encryption error", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            String decryptedJson = encryptor.decrypt(dbData);
            return objectMapper.readTree(decryptedJson);
        } catch (Exception e) {
            throw new RuntimeException("JSON decryption error", e);
        }
    }
}
