package com.datastax.astra.boot.utils;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import com.typesafe.config.Config;

/**
 * Configuration Object.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DataStaxDriverSpringConfig {

    /** Prefix to watch for custom driver configuration. */
    private static final String DRIVER_CONFIG_PREFIX = "astra.cql.driver-config";
    /** Prefix to watch for custom driver configuration. */
    private static final String DRIVER_CONFIG_CORE   = "datastax-java-driver";
    
    /**
     * Pattern to match "some-path.some-key[#]" to convert list value properties to TypeSafe format.
     * Example list:
     *
     * <pre>
     * datastax-java-driver.basic.contact-points[0] = 127.0.0.1:9042
     * datastax-java-driver.basic.contact-points[1] = 127.0.0.2:9042
     * </pre>
     *
     * <p>would need to be converted to
     *
     * <pre>
     * datastax-java-driver.basic.contact-points.0 = 127.0.0.1:9042
     * datastax-java-driver.basic.contact-points.1 = 127.0.0.2:9042
     * </pre>
     *
     * <p>for TypeSafe to process "datastax-java-driver.basic.contact-points" as a list.
     */
    private static final Pattern TYPESAFE_LIST_PATTERN = Pattern.compile("(.+)\\[(\\d+)]");
    
     /**
     * {@link Map} that holds all driver configuration properties that have been set through a Spring
     * properties mechanism. These can come from a properties file (or YAML or XML file), system
     * properties, or some other mechanism by which Spring initializes properties.
     *
     * @param env
     *      current environment
     * @return {@link Map} object with driver-related configuration property values.
     */
    public static Map<String, String> driverConfigFromSpring(ConfigurableEnvironment env) {
      return Collections.unmodifiableMap(
          env.getPropertySources()
             // Get all Keys (astra.cql.driver-config)
             .stream()
             .filter(EnumerablePropertySource.class::isInstance)
             .map(EnumerablePropertySource.class::cast)
             .flatMap(propertySource -> Arrays.stream(propertySource.getPropertyNames()))
             .filter(key -> key.startsWith(DRIVER_CONFIG_PREFIX + "."))
             .distinct()
             // Map if needed for properties
             .map(key ->new SimpleEntry<>(mapAsTypeSafe(key), env.getProperty(key)))
             // Put back as a MAP
             .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
    }
    
    /**
     * Converts a given key to one that can be used for backing a TypeSafe {@link Config}.
     *
     * <p>For TypeSafe, list elements in a properties file are expected to be denoted by a property
     * name that has an <i>index</i> appended as a <i>path</i> element. In a Spring properties file
     * (or YML file), a list element, when converted to a String key, is represented by the key name
     * followed by an index in square brackets.
     *
     * <p>For example, if you have a property key called {@code myList} with 2 values in a list,
     * Spring would yield the following two properties:
     *
     * <pre>
     * myList[0]=value1
     * myList[1]=value2
     * </pre>
     *
     * <p>TypeSafe wants them to look like this:
     *
     * <pre>
     * myList.0=value1
     * myList.1=value2
     * </pre>
     *
     * <p>This method converts any property key for list values form the way Spring builds them into
     * the way TypeSafe expects them.
     *
     * @param key a Java map key the way Spring would create from various Spring PropertySources.
     * @return a Java map key the way TypeSafe can parse them.
     */
    public static String mapAsTypeSafe(String key) {
      Matcher matcher = TYPESAFE_LIST_PATTERN.matcher(key);
      if (matcher.matches()) {
        key = String.format("%s.%s", matcher.group(1), matcher.group(2));
      }
      key = key.replace(DRIVER_CONFIG_PREFIX, DRIVER_CONFIG_CORE);
      return key;
    }
    
    
}
