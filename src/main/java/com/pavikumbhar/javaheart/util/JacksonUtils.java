package com.pavikumbhar.javaheart.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author pavikumbhar
 *
 */
public abstract class JacksonUtils {
    
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = newDefaultObjectMapper();
    private static final TypeReference<HashMap<String, String>> STRING_MAP = new TypeReference<HashMap<String, String>>() {
    };
    
    public JacksonUtils() {
    }
    
    public static final ObjectMapper newDefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        disableFeatures(objectMapper);
        enableFeatures(objectMapper);
        return objectMapper;
    }
    
    private static void disableFeatures(ObjectMapper objectMapper) {
        objectMapper.disable(new MapperFeature[] { MapperFeature.DEFAULT_VIEW_INCLUSION });
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    
    private static void enableFeatures(ObjectMapper objectMapper) {
    }
    
    public static final String toJson(Object object) {
        return toJson(DEFAULT_OBJECT_MAPPER, object);
    }
    
    public static final String toJson(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException var3) {
            throw new JacksonUtils.JsonException(var3);
        }
    }
    
    public static final <T> T jsonToObject(String json, Class<T> clazz) {
        return jsonToObject(DEFAULT_OBJECT_MAPPER, json, clazz);
    }
    
    public static final <T> T jsonToObject(ObjectMapper objectMapper, String json, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception var4) {
            throw new JacksonUtils.JsonException(var4);
        }
    }
    
    public static final <T> T jsonToObject(InputStream inputStream, Class<T> clazz) {
        return jsonToObject(DEFAULT_OBJECT_MAPPER, inputStream, clazz);
    }
    
    public static final <T> T jsonToObject(ObjectMapper objectMapper, InputStream inputStream, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception var4) {
            throw new JacksonUtils.JsonException(var4);
        }
    }
    
    public static final <T> List<T> jsonToList(String json, Class<T> clazz) {
        return jsonToList(DEFAULT_OBJECT_MAPPER, json, clazz);
    }
    
    public static final <T> List<T> jsonToList(ObjectMapper objectMapper, String json, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        
        try {
            return (List) objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
    
    public static final Map<String, Object> convertToMap(Object object) {
        return convertToMap(DEFAULT_OBJECT_MAPPER, object);
    }
    
    public static final Map<String, Object> convertToMap(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        return objectMapper.convertValue(object, Map.class);
    }
    
    public static final Map<String, String> convertToStringMap(Object object) {
        return convertToStringMap(DEFAULT_OBJECT_MAPPER, object);
    }
    
    public static final Map<String, String> convertToStringMap(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        return (Map) objectMapper.convertValue(object, STRING_MAP);
    }
    
    private static void assertObjectMapper(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "ObjectMapper must not be null.");
    }
    
    public static class JsonException extends RuntimeException {
        
        private static final long serialVersionUID = -8318031819390714507L;
        
        public JsonException() {
        }
        
        public JsonException(String message) {
            super(message);
        }
        
        public JsonException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public JsonException(Throwable cause) {
            super(cause);
        }
        
        public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
