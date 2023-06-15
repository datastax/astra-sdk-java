#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractSdkTest {

    protected static String ASTRA_DB_APPLICATION_TOKEN;
    protected static String ASTRA_DB_ID;
    protected static String ASTRA_DB_REGION;
    protected static String ASTRA_DB_KEYSPACE;

    public static void loadRequiredEnvironmentVariables() {
        ASTRA_DB_APPLICATION_TOKEN = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
        if (ASTRA_DB_APPLICATION_TOKEN == null) {
            throw new IllegalArgumentException("ASTRA_DB_APPLICATION_TOKEN variables is not defined");
        }
        ASTRA_DB_ID = System.getenv("ASTRA_DB_ID");
        if (ASTRA_DB_ID == null) {
            throw new IllegalArgumentException("ASTRA_DB_ID variables is not defined");
        }
        ASTRA_DB_REGION = System.getenv("ASTRA_DB_REGION");
        if (ASTRA_DB_REGION == null) {
            throw new IllegalArgumentException("ASTRA_DB_REGION variables is not defined");
        }
        ASTRA_DB_KEYSPACE = System.getenv("ASTRA_DB_KEYSPACE");
        if (ASTRA_DB_KEYSPACE == null) {
            throw new IllegalArgumentException("ASTRA_DB_KEYSPACE variables is not defined");
        }
    }
}
