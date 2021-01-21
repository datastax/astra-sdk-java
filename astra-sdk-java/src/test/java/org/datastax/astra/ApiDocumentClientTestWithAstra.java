package org.datastax.astra;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Namespace;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentClientTestWithAstra extends ApiTester {
    
    public static final String WORKING_NAMESPACE = "astra_sdk_namespace_test";
    public static final DataCenter ASTRA_DC      = new DataCenter("dc-1", 1);
    
    @BeforeAll
    public static void should_init_reusable_api_client() {
        initApiDevopsClient();
        initApiDocumentApiClient();
        // Create the namespace if not present
        if (!apiDocClient.namespace(WORKING_NAMESPACE).exist()) {
            apiDevopsClient.createKeyspace(dbId, WORKING_NAMESPACE);
            System.out.println("Creating Namespace " + WORKING_NAMESPACE);
            waitForSeconds(5);
        }
        Assert.assertTrue(apiDocClient.namespace(WORKING_NAMESPACE).exist());
    }
    
    @Test
    public void builderParams_should_not_be_empty() {
        assertAll("Required parameters",
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseId(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseId(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseRegion(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseRegion(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().username(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().username(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().password(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().password(null); })
        );
    }
    
    @Test
    public void should_connect_to_astra_with_builder() {
        Assert.assertTrue(AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .build().apiDocument().testConnection());
    }
    
    @Test
    @Disabled("Not reproductible on CI/CD but works")
    public void should_connect_to_astra_with_envvar() {
        assertTrue(System.getenv().containsKey("ASTRA_DB_ID"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_REGION"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_USERNAME"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_PASSWORD"));
        assertTrue(AstraClient.builder().build().apiDocument().testConnection());
    }
    
    @Test
    @Disabled("An Astra document Api does not have enough permissions, "
            + "to create/delete namespaces you should use devops API.")
    public void should_create_namespace() {
        // Given
        if (apiDocClient.namespace("namespace2").exist() ) {
            apiDocClient.namespace("namespace2").delete();
            Assert.assertFalse(apiDocClient.namespace("namespace2").exist());
            apiDocClient.namespace("namespace2").create(ASTRA_DC);
        }
        // Then
        Assert.assertTrue(apiDocClient.namespace("namespace2").exist());
    }
    
    // ---- Using devops to create a namespace if not present (reproductability)

    @Test
    public void working_namespace_should_exist() {
        // When
        Set<String> namespaces = apiDocClient.namespaceNames().collect(Collectors.toSet());
        // Then
        Assert.assertTrue(namespaces.contains(WORKING_NAMESPACE));
    }
    
    @Test
    public void working_namespace_should_have_dc() {
        // When
        Map<String, Namespace> namespaces = apiDocClient.namespaces().collect(
                Collectors.toMap(Namespace::getName,  Function.identity()));
        // Then
        Assert.assertTrue(namespaces.containsKey(WORKING_NAMESPACE));
        Assert.assertFalse(namespaces.get(WORKING_NAMESPACE).getDatacenters().isEmpty());
    }
    
    

}
