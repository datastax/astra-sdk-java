package com.datastax.stargate.sdk.test;

/**
 * Constant for test Document API.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiDocumentTest {

    /** Test constant. */
    String TEST_NAMESPACE = "java";
    
    /** Test constant. */
    String TEST_NAMESPACE_BIS = "javabis";
    
    /** Test constant. */
    String TEST_COLLECTION_PERSON = "person";
    
    /** Test constant. */
    String TEST_JSON_SCHEMA = "{\n"
            + "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n"
            + "  \"title\": \"Person\",\n"
            + "  \"description\": \"A person\",\n"
            + "  \"type\": \"object\",\n"
            + "  \"properties\": {\n"
            + "     \"lastname\": {\n"
            + "      \"description\": \"The persons firt name.\",\n"
            + "      \"type\": \"string\"\n"
            + "    },\n"
            + "    \"firstname\": {\n"
            + "      \"description\": \"The persons last name.\",\n"
            + "      \"type\": \"string\"\n"
            + "    },\n"
            + "    \"age\": {\n"
            + "      \"type\": \"number\",\n"
            + "      \"minimum\": 0,\n"
            + "      \"exclusiveMinimum\": true,\n"
            + "      \"description\": \"Age in years which must be equal to or greater than zero.\"\n"
            + "    }\n"
            + "  },\n"
            + "  \"required\": [\"age\", \"lastname\", \"firstname\"]\n"
            + "}";
    
}
