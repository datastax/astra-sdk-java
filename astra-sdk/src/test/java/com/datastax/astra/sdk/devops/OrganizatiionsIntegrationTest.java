package com.datastax.astra.sdk.devops;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.RoleClient;
import com.datastax.astra.sdk.organizations.UserClient;
import com.datastax.astra.sdk.organizations.domain.CreateRoleResponse;
import com.datastax.astra.sdk.organizations.domain.CreateTokenResponse;
import com.datastax.astra.sdk.organizations.domain.DefaultRoles;
import com.datastax.astra.sdk.organizations.domain.Permission;
import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.RoleDefinition;
import com.datastax.astra.sdk.organizations.domain.User;

@TestMethodOrder(OrderAnnotation.class)
public class OrganizatiionsIntegrationTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizatiionsIntegrationTest.class);
    
    private static OrganizationsClient clientOrg;
    
    private static AstraClient client;
    
    @BeforeAll
    public static void config() {
        client= AstraClient.builder().build();
        clientOrg = new OrganizationsClient(client.getToken().get());
    }
        
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        LOGGER.info("Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> new OrganizationsClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> new OrganizationsClient((String)null));
    }
    
    // ------ Working with Roles --------------------
    
    
    @Test
    @Order(2)
    public void should_list_roles() {
        LOGGER.info("\nShould List Roles");
        // When
        List<Role> listRoles = clientOrg.roles().collect(Collectors.toList());
        // Then
        Assertions.assertTrue(listRoles.size() > 5);
        LOGGER.info("Roles can be retrieved");
        for (Role r : listRoles) {
            Assertions.assertNotNull(r);
            Assertions.assertNotNull(r.getName());
            System.out.println("+ " + r.getName() + "=" + r.getId());
        }
        LOGGER.info("Roles are populated");
    }
    
    @Test
    @Order(3)
    public void should_find_role_byName() {
        LOGGER.info("Find a role by its Name");
        // When
        Optional<Role> role = clientOrg.findRoleByName(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
        LOGGER.info("Role found");
    }
    
    @Test
    @Order(4)
    public void should_find_defaultRoles() {
        LOGGER.info("Find a role by its Name");
        // When
        Optional<Role> role = clientOrg.role(DefaultRoles.DATABASE_ADMINISTRATOR).find();
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
        LOGGER.info("Role found");
    }
    
    @Test
    @Order(5)
    public void should_find_role_byId() {
        LOGGER.info("Find a role by its idsxs");
        Optional<Role> role = clientOrg.role(DefaultRoles.DATABASE_ADMINISTRATOR).find();
        
        Optional<Role> role2 = new OrganizationsClient(client.getToken().get())
            .role(role.get().getId())
            .find();
        Assertions.assertTrue(role2.isPresent());
        LOGGER.info("Role found");
    }
    
    private static String customRole;
    private static String customRoleId;
    
    @Test
    @Order(6)
    public void should_create_a_role() {
        System.out.println( "- Creating a role");
        
        customRole = "sdk_java_junit_role" + UUID.randomUUID().toString().substring(0,7);
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        
        RoleDefinition cr = RoleDefinition.builder(iamClient.organizationId())
                  .name(customRole)
                  .description("Only the brave")
                  .addPermision(Permission.db_all_keyspace_create)
                  .addResourceAllDatabases()
                  .addResourceAllKeyspaces()
                  .addResourceAllTables()
                  .build();
        CreateRoleResponse res = iamClient.createRole(cr);
        customRoleId = res.getRoleId();
        Assertions.assertTrue(clientOrg.role(customRoleId).find().isPresent());
        LOGGER.info("Role created name=" + customRole + ", id=" + customRoleId);
    }
    
    @Test
    @Order(7)
    public void should_delete_a_role() {
        System.out.println( "- Deleting a role");
        // Given
        RoleClient rc = clientOrg.role(customRoleId);
        Assertions.assertTrue(rc.exist());
        LOGGER.info("Role found");
        // When
        rc.delete();
        // Then
        Assertions.assertFalse(rc.exist());
        LOGGER.info("Role deleted name=" + customRole + ", id=" + customRoleId);
    }
    
    @Test
    @Order(8)
    public void should_update_a_role() {
        System.out.println( "- Deleting a role");
        // When
        CreateRoleResponse res = clientOrg.createRole(RoleDefinition.builder(clientOrg.organizationId())
                .name("RoleTMP")
                .description("Only the brave")
                .addPermision(Permission.db_all_keyspace_create)
                .addResourceAllDatabases()
                .addResourceAllKeyspaces()
                .addResourceAllTables()
                .build());
        // Then
        RoleClient roleTmp = clientOrg.role(res.getRoleId());
        Assertions.assertTrue(roleTmp.exist());
        LOGGER.info("RoleTMP created");
        // When
        roleTmp.update(RoleDefinition.builder(clientOrg.organizationId())
                                   .name("RoleTMP")
                                   .description("updated descriptiom")
                                   .addPermision(Permission.db_cql)
                                   .build()
                );
        Role r = roleTmp.find().get();
        Assertions.assertTrue(r.getPolicy().getActions().contains(Permission.db_cql.getCode()));
        Assertions.assertTrue(r.getPolicy().getDescription().equals("updated descriptiom"));
        LOGGER.info("Role updated");
        roleTmp.delete();
        LOGGER.info("Role deleted");
    }
    
    // ------ Working with Tokens --------------------
    
    private static String tmpClientId;
    
    @Test
    @Order(10)
    public void should_create_token() {
        System.out.println( "- Creating a Token");
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        CreateTokenResponse res = iamClient.createToken(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        tmpClientId = res.getClientId();
        LOGGER.info("Token created " + tmpClientId);
        Assertions.assertTrue(iamClient.token(res.getClientId()).exist());
        LOGGER.info("Token exist ");
    }
    
    
    @Test
    @Order(11)
    public void should_delete_token() {
        System.out.println( "- Deleting a Token");
        
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        // Given
        Assertions.assertTrue(iamClient.token(tmpClientId).exist());
        // When
        iamClient.token(tmpClientId).delete();
        LOGGER.info("Token deleted ");
        // Then
        Assertions.assertFalse(iamClient.token(tmpClientId).exist());
        LOGGER.info("Token does not exist ");
    }
    
    // ------ Working with Users --------------------
    
    private static String tmpUserid;
    private static String tmpUserEmail;
    
    @Test
    @Order(12)
    public void should_list_users() {
        // Given
        System.out.println( "- List users");
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        List<User> users = iamClient.users().collect(Collectors.toList());
        LOGGER.info("Users retrieved ");
        Assertions.assertTrue(users.size() >0);
        tmpUserid = users.get(0).getUserId();
        tmpUserEmail = users.get(0).getEmail();
    }
    
    @Test
    @Order(13)
    public void should_find_user() {
        // Given
        System.out.println( "- Find users");
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        Assertions.assertTrue(iamClient.user(tmpUserid).exist());
        LOGGER.info("User retrieved (by ID)");
        Assertions.assertTrue(iamClient.findUserByEmail(tmpUserEmail).isPresent());
        LOGGER.info("User retrieved (by email)");
    }
    
    @Test
    @Order(14)
    public void should_addRoles() {
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        UserClient uc = iamClient.user(tmpUserid);
        Assertions.assertTrue(uc.exist());
        uc.updateRoles(
               DefaultRoles.DATABASE_ADMINISTRATOR.getName(), 
               DefaultRoles.ORGANIZATION_ADMINISTRATOR.getName());
    }
}
