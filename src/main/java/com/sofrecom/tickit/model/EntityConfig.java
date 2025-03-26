package com.sofrecom.tickit.model;

import javax.persistence.*;

@Entity
public class EntityConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityType;
    private String fieldName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "EntityConfig{" +
                "id=" + id +
                ", entityType='" + entityType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
