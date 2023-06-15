#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.devops;

import com.datastax.astra.sdk.AstraClient;
import ${package}.AbstractSdkTest;
import com.dtsx.astra.sdk.org.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AstraDevopsCoreApiTest extends AbstractSdkTest {

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
    }

    @Test
    public void shouldConnectWithGraphQL() {
        // A token is all you need
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)
                .build()) {
            astraClient.apiDevops()
                    .users()
                    .findAll()
                    .map(User::getEmail)
                    .forEach(System.out::println);
        }
    }
}

