package com.pavikumbhar.javaheart.util;

import static com.pavikumbhar.javaheart.util.UtilExceptions.declareToThrow;
import static com.pavikumbhar.javaheart.util.UtilExceptions.undeclareCheckedException;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.Session;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreparedStatementFactory {
    
    /**
     * 
     * @param connection
     * @param queryStringWithNamedParameters
     * @return
     * @throws SQLException
     */
    public static PreparedStatement create(final Connection connection, final String queryStringWithNamedParameters) throws SQLException {
        final String parsedQuery = parse(queryStringWithNamedParameters);
        return connection.prepareStatement(parsedQuery);
    }
    
    /**
     * 
     * @param query
     * @return
     */
    private static final String parse(final String query) {
        
        final int length = query.length();
        final StringBuffer parsedQuery = new StringBuffer(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        
        for (int i = 0; i < length; ++i) {
            char c = query.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(query.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        ++j;
                    }
                    final String name = query.substring(i + 1, j);
                    c = '?'; // replace the parameter with a question mark  
                    i += name.length(); // skip past the end of the parameter  
                }
            }
            parsedQuery.append(c);
        }
        return parsedQuery.toString();
    }
    
    /**
     * 
     * @param queryString
     * @param em
     * @return
     * @throws SQLException
     */
    private Map<String, Integer> getColumnNameToIndexMap(final String queryString, final EntityManager em) throws SQLException {
        final Session session = em.unwrap(Session.class); // ATTENTION! This is Hibernate-specific!  
        final AtomicReference<ResultSetMetaData> msRef = new AtomicReference<>();
        session.doWork((c) -> {
            try (final PreparedStatement statement = PreparedStatementFactory.create(c, queryString)) {
                // I'm not setting parameters here, because I just want to find out about the return values' column names  
                msRef.set(statement.getMetaData());
            }
        });
        final ResultSetMetaData metaData = msRef.get();
        // LinkedHashmap preserves order of insertion:  
        final Map<String, Integer> columnNameToColumnIndex = new LinkedHashMap<>();
        for (int t = 0; t < metaData.getColumnCount(); ++t) {
            // important, first index in the metadata is "1", the first index for the result array must be "0"  
            columnNameToColumnIndex.put(metaData.getColumnName(t + 1), t);
        }
        return columnNameToColumnIndex;
    }
    
    /**
     * 
     * @param rowValueArray
     * @param columnNameToIndexMap
     * @return
     */
    private Map<String, Object> getColumNameToValueMapFromRowValueArray(final Object[] rowValueArray, final Map<String, Integer> columnNameToIndexMap) {
        // stream().collect(toMap(keyFunct, valueFunct)...) will not accept "null" values, so we do it this way:  
        final Map<String, Object> result = new LinkedHashMap<>();
        columnNameToIndexMap.entrySet().forEach(nameToIndexEntry -> result.put(nameToIndexEntry.getKey(), rowValueArray[nameToIndexEntry.getValue()]));
        return result;
    }
    
    private List<Map<String, Object>> asListOfMapsBACk(final List<Object[]> queryResultAsListOfObjectArrays, final Map<String, Integer> columnNameToIndexMap) {
        final Function<Object[], Map<String, Object>> rowValueArrayToColumnNameToValueMap = rowValueArray -> {
            return getColumNameToValueMapFromRowValueArray(rowValueArray, columnNameToIndexMap);
        };
        return queryResultAsListOfObjectArrays.stream().collect(mapping(rowValueArrayToColumnNameToValueMap, toList()));
    }
    
    /**
     * 
     * @param queryResultAsListOfObjectArrays
     * @param columnNameToIndexMap
     * @param mapToObject
     * @return
     */
    public <T> List<T> asMapped(final List<Object[]> queryResultAsListOfObjectArrays, final Map<String, Integer> columnNameToIndexMap,
            final Function<Map<String, Object>, T> mapToObject) {
        final Function<Object[], Map<String, Object>> arrayToMap = rowValueArray -> {
            return getColumNameToValueMapFromRowValueArray(rowValueArray, columnNameToIndexMap);
        };
        final Function<Object[], T> mapper = arrayToMap.andThen(mapToObject);
        return queryResultAsListOfObjectArrays.stream().collect(mapping(mapper, toList()));
    }
    
    /**
     * 
     * @param queryResultAsListOfObjectArrays
     * @param columnNameToIndexMap
     * @return
     */
    public List<Map<String, Object>> asListOfMaps(final List<Object[]> queryResultAsListOfObjectArrays, final Map<String, Integer> columnNameToIndexMap) {
        return asMapped(queryResultAsListOfObjectArrays, columnNameToIndexMap, Function.identity());
    }
    
    /**
     * 
     * @param queryResultAsListOfObjectArrays
     * @param columnNameToIndexMap
     * @param targetPojoFactory
     * @return
     */
    public <T> List<T> asListOfPojos(final List<Object[]> queryResultAsListOfObjectArrays, final Map<String, Integer> columnNameToIndexMap,
            final Supplier<T> targetPojoFactory) {
        final Function<Map<String, Object>, T> mapToPojo = (rowMap) -> createPojoAndMapValues(targetPojoFactory, rowMap);
        return asMapped(queryResultAsListOfObjectArrays, columnNameToIndexMap, mapToPojo);
    }
    
    /**
     * 
     * @param targetPojoFactory
     * @param rowMap
     * @return
     */
    private <T> T createPojoAndMapValues(final Supplier<T> targetPojoFactory, final Map<String, Object> rowMap) {
        final T pojo = targetPojoFactory.get();
        rowMap.entrySet().stream().forEach(columnNameValueEntry -> setToField(columnNameValueEntry, pojo));
        return pojo;
    }
    
    /**
     * 
     * @param columnNameValueEntry
     * @param pojo
     */
    private <T> void setToField(final Entry<String, Object> columnNameValueEntry, final T pojo) {
        final String columnName = columnNameValueEntry.getKey();
        Object columnValue = columnNameValueEntry.getValue();
        try {
            final Field field = getField(pojo.getClass(), columnName);
            /// columnValue = optionallyConvertValueToFieldType(field, columnValue);  
            field.set(pojo, columnValue);
        } catch (final IllegalAccessException | IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("Could not set Value " + columnValue + (columnValue == null ? "" : "of type: " + columnValue.getClass())
                    + " to pojo of type " + pojo.getClass(), e);
        }
    }
    
    /**
     * 
     * @param pojoClass
     * @param columnName
     * @return
     */
    private Field getField(final Class<? extends Object> pojoClass, final String columnName) {
        try {
            declareToThrow(NoSuchFieldException.class); // because it sneakily does down at "pojoClass.getDeclaredField"  
            return Arrays.stream(pojoClass.getDeclaredFields())//  
                    .filter(field -> field.getAnnotation(Column.class) != null)// Column name is dominant  
                    .filter(field -> columnName.equalsIgnoreCase(field.getAnnotation(Column.class).name()))//  
                    .findAny()//  
                    .orElseGet(() -> Arrays.stream(pojoClass.getDeclaredFields())// @XmlAnnotation is a second choice  
                            .filter(field -> field.getAnnotation(XmlAttribute.class) != null)//  
                            .filter(field -> columnName.equalsIgnoreCase(field.getAnnotation(XmlAttribute.class).name()))//  
                            .findAny()//  
                            .orElseGet(() -> Arrays.stream(pojoClass.getDeclaredFields())// @JsonProperty is a third choice  
                                    .filter(field -> field.getAnnotation(JsonProperty.class) != null)//  
                                    .filter(field -> columnName.equalsIgnoreCase(field.getAnnotation(JsonProperty.class).value()))//  
                                    .findAny()//  
                                    .orElseGet(undeclareCheckedException(() -> pojoClass.getDeclaredField(columnName))))); // field name is our last option  
        } catch (final SecurityException e) {
            throw new IllegalStateException("Could not map column " + columnName + " to class " + pojoClass, e);
        } catch (final NoSuchFieldException e) {
            // ignore, because we will throw an exception anyway  
        }
        throw new IllegalArgumentException("Could not map column to class " + pojoClass
                + ". Reason: No declared field found that is either annotated with @Column(name=\"" + columnName + "\") or @XmlAttribute(name=\"" + columnName
                + "\") or @JsonProperty(\"" + columnName + "\") ignoring case, and no declared Field is named \"" + columnName + "\".");
    }
    
    /**
     * @SuppressWarnings("unchecked") private Object optionallyConvertValueToFieldType(final Field
     * field, final Object columnValue) { final Class<?> fieldType = field.getType(); if
     * (columnValue == null) { //@formatter:off return Long.TYPE.equals(fieldType) ?
     * Long.valueOf(0l) : Integer.TYPE.equals(fieldType) ? Integer.valueOf(0) :
     * Short.TYPE.equals(fieldType) ? Short.valueOf((short)0) : Byte.TYPE.equals(fieldType) ?
     * Byte.valueOf((byte)0) : Double.TYPE.equals(fieldType) ? Double.valueOf(0d) :
     * Float.TYPE.equals(fieldType) ? Float.valueOf(0f) : (Object) null; //note for some reason we
     * get an NPE without the cast }//@formatter:on final Class<?> valueType =
     * columnValue.getClass(); if (!fieldType.isAssignableFrom(valueType))
     * { @SuppressWarnings("rawtypes") final Optional<TypeConverter> optionalConverter =
     * converters.stream().filter(c -> c.canConvert(valueType, fieldType)).findFirst(); if
     * (optionalConverter.isPresent()) { return optionalConverter.get().convert(columnValue); } }
     * return columnValue; }
     **/
}
