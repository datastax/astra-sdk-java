/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Custom implementation of serialization : faster + no jackson dependency
 * 
 * @author Cedrick Lunven (@clunven)
 */
@SuppressWarnings("deprecation")
public class JsonUtils {
    
    /** Object to Json marshaller as a Jackson Mapper. */
    private static final ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
                .setSerializationInclusion(Include.NON_NULL)
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    
    /**
     * Default constructor
     */
    private JsonUtils() {
    }
    
    /**
     * Code is built based on https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONValue.java
     * by FangYidong fangyidong@yahoo.com.cn THANK YOU ! ff4j core needs to stay with no dependency.
     * 
     * 
     * The following characters are reserved characters and can not be used in JSON and must be properly escaped to be used in strings.
     * - Backspace to be replaced with \b
     * - Form feed to be replaced with \f
     * - Newline to be replaced with \n
     * - Carriage return to be replaced with \r
     * - Tab to be replaced with \t
     * - Double quote to be replaced with \"
     * - Backslash to be replaced with \\
     * 
     * @param value
     *      string to be escaped
     * @return
     *      escaped JSON
     */
    public static final String escapeJson(String value) {
        if (value == null ) return null;
        StringBuilder output = new StringBuilder();
        final int len = value.length();
        for(int i=0;i<len;i++) {
            char ch=value.charAt(i);
            switch(ch){
            case '"':
                output.append("\\\"");
                break;
            case '\\':
                output.append("\\\\");
                break;
            case '\b':
                output.append("\\b");
                break;
            case '\f':
                output.append("\\f");
                break;
            case '\n':
                output.append("\\n");
                break;
            case '\r':
                output.append("\\r");
                break;
            case '\t':
                output.append("\\t");
                break;
            case '/':
                output.append("\\/");
                break;
            default:
                //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
                    String ss=Integer.toHexString(ch);
                    output.append("\\u");
                    for(int k=0;k<4-ss.length();k++){
                        output.append('0');
                    }
                    output.append(ss.toUpperCase());
                }
                else{
                    output.append(ch);
                }
            }
        }
        return output.toString();
    }
   
    /**
     * Target primitive displayed as JSON.
     *
     * @param value
     *      object value
     * @return
     *      target json expression
     */
    public static final String valueAsJson(Object value) {
        if (value == null )          return "null";
        if (value instanceof String) return "\"" + escapeJson(value.toString()) + "\"";
        return value.toString();
    }

    /**
     * Serialize a collection of object as Json. Element should eventually override <code>toString()</code> to produce JSON.
     * 
     * @param <T> T
     * @param pCollec input collection
     * @return collection as String
     */
    public static final <T> String collectionAsJson(final Collection < T > pCollec) {
        if (pCollec == null)   return "null";
        if (pCollec.isEmpty()) return "[]";
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (T element : pCollec) {
            json.append(first ? "" : ",");
            json.append(valueAsJson(element));
            first = false;
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Serialize a map of objects as Json. Elements should override <code>toString()</code> to produce JSON.
     * 
     * @param <K> K
     * @param <V> V
     * @param pMap target properties
     * @return target json expression
     */
    public static final <K,V> String mapAsJson(final Map<K,V> pMap) {
        if (pMap == null)   return "null";
        if (pMap.isEmpty()) return "{}";
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<K,V> mapEntry : pMap.entrySet()) {
            json.append(first ? "" : ",");
            json.append(valueAsJson(mapEntry.getKey()) + ":");
            json.append(valueAsJson(mapEntry.getValue()));
            first = false;
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Access the singletong ObjectMapper.
     *
     * @return
     *      object mapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    /**
     * Transform object as a String.
     * 
     * @param o
     *      object to be serialized.
     * @return
     *      body as String
     */
    public static String marshall(Object o) {
        Objects.requireNonNull(o);
        try {
            return getObjectMapper().writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall object " + o, e);
        } 
    }
    
    /**
     * Load body as expected object.
     * 
     * @param <T>
     *      parameter
     * @param body
     *      response body as String
     * @param ref
     *      type Reference to map the result
     * @return
     *      expected object
     */
    public static <T> T unmarshallType(String body, TypeReference<T> ref) {
        try {
            return getObjectMapper().readValue(body, ref);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Cannot unmarshall object " + body, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot unmarshall object " + body, e);
        }
    }
    
    /**
     * Load body as expected object.
     * 
     * @param <T>
     *      parameter
     * @param body
     *      response body as String
     * @param ref
     *      type Reference to map the result
     * @return
     *       expected objects
     */
    public static <T> T unmarshallBean(String body, Class<T> ref) {
        try {
            return getObjectMapper().readValue(body, ref);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Cannot unmarshall object " + body, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot unmarshall object " + body, e);
        }
    }
}