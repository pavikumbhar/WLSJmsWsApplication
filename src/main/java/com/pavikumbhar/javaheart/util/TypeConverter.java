package com.pavikumbhar.javaheart.util;

public interface TypeConverter {
    
    boolean canConvert(Class<?> valueType, Class<?> fieldType);
    
    Object convert(Object columnValue);
    
}
