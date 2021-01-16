package org.datastax.astra.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Unmarshalling payload using Jackson
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <PAYLOAD>
 *      serializable object 
 */
public class JsonBodyHandler<PAYLOAD> implements HttpResponse.BodyHandler<PAYLOAD> {
    
    /** Object <=> Json marshaller as a Jackson Mapper. */
    private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    
    /**
     * Working class.
     */
    private final Class<PAYLOAD> wClass;
   
    /**
     * Specialization of handler using JACKSON.
     */
    public JsonBodyHandler(Class<PAYLOAD> wClass) {
        this.wClass = wClass;
    }

    /** {@inheritDoc} */
    @Override
    public HttpResponse.BodySubscriber<PAYLOAD> apply(HttpResponse.ResponseInfo responseInfo) {
        return asJSON(wClass);
    }
    
    /**
     * Parser.
     */
    public static <PAYLOAD> HttpResponse.BodySubscriber<PAYLOAD> asJSON(Class<PAYLOAD> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(
                upstream, (String body) -> {
                    try {
                        return JACKSON_MAPPER.readValue(body, targetType);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
        });
    }
    
}

