package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.UsersClient;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import com.dtsx.astra.sdk.org.domain.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersClientTest extends AbstractDevopsApiTest {

    /**
     * User idenii
     */
    private static String tmpUserid;
    private static String tmpUserEmail;

    @Test
    @Order(1)
    public void should_list_users() {
        // Given
        UsersClient usersClient = getApiDevopsClient().users();
        // When
        List<User> users = usersClient.findAll().collect(Collectors.toList());
        Assertions.assertTrue(users.size() >0);

        tmpUserid    = users.get(0).getUserId();
        tmpUserEmail = users.get(0).getEmail();
    }

    @Test
    @Order(2)
    public void should_find_user() {
        // Given
        UsersClient usersClient = getApiDevopsClient().users();
        // When
        Assertions.assertTrue(usersClient.exist(tmpUserid));
        // Then
        Assertions.assertTrue(usersClient.findByEmail(tmpUserEmail).isPresent());
    }

    @Test
    @Order(3)
    public void should_addRoles() {
        // Given
        UsersClient usersClient = getApiDevopsClient().users();
        Assertions.assertTrue(usersClient.exist(tmpUserid));
        // When
        usersClient.updateRoles(tmpUserid,
                DefaultRoles.DATABASE_ADMINISTRATOR.getName(),
                DefaultRoles.ORGANIZATION_ADMINISTRATOR.getName());
    }

}
