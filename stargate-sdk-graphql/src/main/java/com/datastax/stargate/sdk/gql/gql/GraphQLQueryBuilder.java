package com.datastax.stargate.sdk.gql.gql;

/**
 * Build queries for the GraphQL endpoints. 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class GraphQLQueryBuilder {
    
    /**
     * Hide default constructor.
     */
    private GraphQLQueryBuilder() {}
   
    /**
     * List tables in a keyspace.
     * 
     * @param keyspace
     *      keyspace name
     * @return
     *      list of tables++
     */
    public static String queryListTables(String keyspace) {
        return "query GetTables {\n"
                + "  keyspace(name: \"" + keyspace + "\") {\n"
                + "      name\n"
                + "      tables {\n"
                + "          name\n"
                + "          columns {\n"
                + "              name\n"
                + "              kind\n"
                + "              type {\n"
                + "                  basic\n"
                + "                  info {\n"
                + "                      name\n"
                + "                  }\n"
                + "              }\n"
                + "          }\n"
                + "      }\n"
                + "  }\n"
                + "}";
        
    }

}
