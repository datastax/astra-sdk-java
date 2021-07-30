package com.datastax.astra.sdk.utils;

import java.util.Optional;

import com.datastax.oss.driver.shaded.guava.common.base.Strings;

/**
 * Utility class to read environment variables.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class EnvironmentVariableUtils {
    
    /**
     * Hide Default contructor.
     */
    private EnvironmentVariableUtils() {}

    /**
     * Reading a System property. 
     * Reading then a Property '-D' than can override system
     * 
     * @param varname
     *      name of the variable to read.
     * @param defaultValue
     *      provide a default value
     * @return
     *      optional value or default value
     */
    public static String get(String varname, String defaultValue) {
        String value = defaultValue;
        if (!Strings.isNullOrEmpty(System.getenv(varname))) {
            value = System.getenv(varname);
        }
        if (!Strings.isNullOrEmpty(System.getProperty(varname))) {
            value = System.getProperty(varname);
        }
        return value;
    }
    
    /**
     * Look for variable or returned default value.
     *
     * @param varname
     *       name of the variable to read.
     * @return
     *      value of the variable if found or empty
     */
    public static Optional<String> get(String varname) {
        return Optional.ofNullable(get(varname, null));
    }
}
