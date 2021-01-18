package org.datastax.astra;

import java.util.HashMap;
import java.util.Map;


/**
 * CollectionName is required to works with document API. When sorking with Java the class
 * should be good enough. This class use convention
 * 
 * org.datastax.astra.MySampleClassName <=> org_datastax_astra__my_sample_class_name
 * 
 * CamelCase to Snake Case and vice-versa + separator for class is '__'
 *
 * Mapping are kept in memory to avoid doing the work multiple times.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionUtils {

    /** Associate a collection to a className. */
    public static final Map<String, Class<?>> col2class = new HashMap<>();
    
    /** Associate a collection to a className. */
    public static final Map<Class<?>, String> class2collec = new HashMap<>();
    
    /**
     * Used if annotation has not been provided to marshall class.
     *
    public static String getCollectionName(Class<?> clazz) {
        if (!class2collec.containsKey(clazz)) {
            String collectioName = fromCamelCaseToSnakeCase(clazz.getName().replace(".", "_"));
            AstraCollection col  = clazz.getAnnotation(AstraCollection.class);
            if (null != col) {
                collectioName = col.value();
            }
            col2class.put(collectioName, clazz);
            class2collec.put(clazz, collectioName);
        }
        return class2collec.get(clazz);
    }
    
    /**
     * Map String to className. 
     *
    public static Class<?> mapCollectionToClass(String collectionName)
    throws ClassNotFoundException {
        Objects.requireNonNull(collectionName);
        if (!col2class.containsKey(collectionName)) {
            String[] chunks = collectionName.split("__");
            if (2 != chunks.length) {
                throw new IllegalArgumentException("Invalid format, expecting package_name__class_name");
            }
            String packageName = chunks[0].replaceAll("_", ".");
            String classname   = fromSnakeCaseToCamelCase(chunks[1]);
            return Class.forName(packageName + "." + classname);
        }
        return col2class.get(collectionName);
    }
    
    /**
     * Mapping from className to collection name (camelCase => snakeCase).
     *
    private static String fromCamelCaseToSnakeCase(String str) {
        Objects.requireNonNull(str);
        String result = "";
        // Append first character(in lower case)
        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);

        // Tarverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result = result + Character.toLowerCase(ch);
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result = result + ch;
            }
        }

        // return the result
        return result;
    }
    
    private static String fromSnakeCaseToCamelCase(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '_') {
                sb.deleteCharAt(i);
                sb.replace(i, i+1, String.valueOf(Character.toUpperCase(sb.charAt(i))));
            }
        }
        // First Letter is a capital
        sb.replace(0, 1, String.valueOf(Character.toUpperCase(sb.charAt(0))));
        return sb.toString();
    }*/

}
