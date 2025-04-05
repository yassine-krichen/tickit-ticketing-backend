package com.sofrecom.tickit.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;
import org.jasypt.util.text.AES256TextEncryptor;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public class EncryptedJsonbType implements UserType, DynamicParameterizedType {

    private static final AES256TextEncryptor encryptor = new AES256TextEncryptor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Ideally, load this password from a secure configuration (environment variable, secrets vault, etc.)
        encryptor.setPassword("YourSecretKey"); 
    }

    // We'll store the encrypted JSON as a VARCHAR (text) in the database.
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class<?> returnedClass() {
        return JsonNode.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String encryptedJson = rs.getString(names[0]);
        if (encryptedJson == null) {
            return null;
        }
        String decryptedJson = encryptor.decrypt(encryptedJson);
        try {
            return objectMapper.readTree(decryptedJson);
        } catch (Exception e) {
            throw new SQLException("Failed to convert decrypted JSON to JsonNode", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            try {
                // Convert JsonNode to JSON string
                String json = objectMapper.writeValueAsString(value);
                // Encrypt the JSON string
                String encryptedJson = encryptor.encrypt(json);
                st.setString(index, encryptedJson);
            } catch (Exception e) {
                throw new SQLException("Failed to convert JsonNode to JSON string", e);
            }
        }
    }

    @Override
    public Object deepCopy(Object value) {
        // JsonNode is immutable in practice (or you can implement a proper deep copy if needed)
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

    // If you need to receive additional parameters (for instance, encryption password), you can implement DynamicParameterizedType#setParameterValues
    @Override
    public void setParameterValues(Properties parameters) {
        // You can retrieve additional parameters from the mapping if needed.
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'equals'");
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }
}
