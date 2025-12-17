package com.akash.embedqa.converter;

import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@Converter(autoApply = false)
public class AuthConfigConverter implements AttributeConverter<AuthConfigDTO, String> {


    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(AuthConfigDTO attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize AuthConfig", e);
            return null;
        }
    }


    @Override
    public AuthConfigDTO convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return objectMapper.readValue(dbData, AuthConfigDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize AuthConfig", e);
            return null;
        }
    }
}
