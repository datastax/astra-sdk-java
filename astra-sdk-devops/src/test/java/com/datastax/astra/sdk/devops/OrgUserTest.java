package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import com.dtsx.astra.sdk.org.domain.User;
import com.dtsx.astra.sdk.org.iam.UserClient;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrgUserTest  extends AbstractDevopsApiTest {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgUserTest.class);

    /**
     * User idenii
     */
    private static String tmpUserid;
    private static String tmpUserEmail;

    @Test
    @Order(1)
    public void should_list_users() {
        // Given
        LOGGER.info("- List users");
        OrganizationsClient iamClient = getOrganizationClient();
        List<User> users = iamClient.users().collect(Collectors.toList());
        LOGGER.info("Users retrieved ");
        Assertions.assertTrue(users.size() >0);
        tmpUserid = users.get(0).getUserId();
        System.out.println(tmpUserid);
        tmpUserEmail = users.get(0).getEmail();
    }

    @Test
    @Order(2)
    public void should_find_user() {
        // Given
        LOGGER.info("- Find users");
        OrganizationsClient iamClient = getOrganizationClient();
        Assertions.assertTrue(iamClient.user(tmpUserid).exist());
        LOGGER.info("User retrieved (by ID)");
        Assertions.assertTrue(iamClient.findUserByEmail(tmpUserEmail).isPresent());
        LOGGER.info("User retrieved (by email)");
    }

    @Test
    @Order(3)
    public void should_addRoles() {
        // Given
        OrganizationsClient iamClient = getOrganizationClient();
        UserClient uc = iamClient.user(tmpUserid);
        Assertions.assertTrue(uc.exist());
        uc.updateRoles(
                DefaultRoles.DATABASE_ADMINISTRATOR.getName(),
                DefaultRoles.ORGANIZATION_ADMINISTRATOR.getName());
    }

    @Test
    @Order(4)
    public void fixFindUser() {
        OrganizationsClient iamClient = getOrganizationClient();
        List<User> users = iamClient.users().collect(Collectors.toList());
        String tmpUserid = users.get(0).getUserId();
        UserClient uc = iamClient.user(tmpUserid);
        Assertions.assertTrue(iamClient.user(tmpUserid).exist());
        uc.find().get();
        uc.updateRoles(
                DefaultRoles.DATABASE_ADMINISTRATOR.getName(),
                DefaultRoles.ORGANIZATION_ADMINISTRATOR.getName());
    }

}
