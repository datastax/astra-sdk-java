package com.datastax.astra.sdk;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.CreateRoleResponse;
import com.datastax.astra.sdk.organizations.domain.CreateTokenResponse;
import com.datastax.astra.sdk.organizations.domain.DefaultRoles;
import com.datastax.astra.sdk.organizations.domain.Permission;
import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.RoleDefinition;
import com.datastax.astra.sdk.organizations.domain.User;

import graphql.Assert;

@TestMethodOrder(OrderAnnotation.class)
public class T06_DevopsIamIntegrationTest extends AbstractAstraIntegrationTest {
    
    // Client Test
    private static final String TEST_DBNAME       = "sdk_test_api_devops";
    private static final String WORKING_KEYSPACE  = "ks1";
    private static OrganizationsClient clientOrg;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=          DevopsAPI IAM              =");
        printYellow("=======================================");
        createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_KEYSPACE);
        clientOrg = new OrganizationsClient(client.getToken().get());
    }
        
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        printYellow("Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OrganizationsClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OrganizationsClient(null));
    }
    
    @Test
    @Order(2)
    public void should_list_roles() {
        printYellow("\nShould List Roles");
        // When
        List<Role> listRoles = clientOrg.roles().collect(Collectors.toList());
        // Then
        Assertions.assertTrue(listRoles.size() > 5);
        printOK("Roles can be retrieved");
        for (Role r : listRoles) {
            Assertions.assertNotNull(r);
            Assertions.assertNotNull(r.getName());
            System.out.println("+ " + r.getName() + "=" + r.getId());
        }
        printOK("Roles are populated");
    }
    
    @Test
    @Order(3)
    public void should_find_role_byName() {
        printYellow("Find a role by its Name");
        // When
        Optional<Role> role = clientOrg.findRoleByName(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
    }
    
    @Test
    @Order(4)
    public void should_find_defaultRoles() {
        printYellow("Find a role by its Name");
        // When
        Optional<Role> role = clientOrg.role(DefaultRoles.DATABASE_ADMINISTRATOR).find();
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
    }
    
    @Test
    @Order(5)
    public void should_find_role_byId() {
        printYellow("Connection with AstraClient");
        Assertions.assertTrue(client.getToken().isPresent());
        System.out.println(new OrganizationsClient(client.getToken().get())
            .role("1faa93f2-b889-4190-9585-4bc6e3c3595a")
            .find()
            .get()
            .getName());
    }
    
    @Test
    @Order(6)
    public void should_create_a_role() {
        System.out.println(ANSI_YELLOW + "- Creating a role" + ANSI_RESET);
        
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        
        RoleDefinition cr = RoleDefinition.builder(iamClient.organizationId())
                  .name("My New Role")
                  .description("Only the brave")
                  .addPermision(Permission.db_all_keyspace_create)
                  .addResourceAllDatabases()
                  .addResourceAllKeyspaces()
                  .addResourceAllTables()
                  .build();
        
        CreateRoleResponse res = iamClient.createRole(cr);
        
        System.out.println(res.getRoleId());
    }
    
    @Test
    @Order(5)
    public void should_delete_a_role() {
        System.out.println(ANSI_YELLOW + "- Deleting a role" + ANSI_RESET);
        // Given
        String roleId= "74b847fe-6804-407f-b2d2-e748103ee851";
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        Assertions.assertTrue(iamClient.role(roleId).exist());
        // When
        iamClient.role(roleId).delete();
        // Then
        Assertions.assertFalse(iamClient.role(roleId).exist());
    }
    
    @Test
    @Order(6)
    public void should_update_a_role() {
        System.out.println(ANSI_YELLOW + "- Deleting a role" + ANSI_RESET);
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        // When
        CreateRoleResponse res = iamClient.createRole(RoleDefinition.builder(iamClient.organizationId())
                .name("RoleTMP")
                .description("Only the brave")
                .addPermision(Permission.db_all_keyspace_create)
                .addResourceAllDatabases()
                .addResourceAllKeyspaces()
                .addResourceAllTables()
                .build());
        // Then
        Assertions.assertTrue(iamClient.role(res.getRoleId()).exist());
        // When
        iamClient.role(res.getRoleId())
                 .update(RoleDefinition.builder(iamClient.organizationId())
                                   .name("RoleTMP")
                                   .description("updated descriptiom")
                                   .addPermision(Permission.db_cql)
                                   .build()
                );
    }
    
    @Test
    @Order(7)
    public void should_list_tokens() {
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        iamClient.tokens().forEach(t->System.out.println(t.getRoles()));
    }
    
    @Test
    @Order(8)
    public void should_create_token() {
        System.out.println(ANSI_YELLOW + "- Creating a Token" + ANSI_RESET);
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        CreateTokenResponse res = iamClient.createToken("write");
        System.out.println(res.getClientId());
        Assert.assertTrue(iamClient.token(res.getClientId()).exist());
    }
    
    @Test
    @Order(9)
    public void should_delete_token() {
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        // Given
        String token = "rmuoZHiMPejnZoBSBeAHFIid";
        Assert.assertTrue(iamClient.token(token).exist());
        // When
        iamClient.token(token).delete();
        // Then
        Assert.assertFalse(iamClient.token(token).exist());
    }
    
    @Test
    @Order(10)
    public void should_list_users() {
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        iamClient.users().forEach(u -> {
            System.out.println(u.getEmail() + "=" + u.getUserId());
        });
    }
    
    @Test
    @Order(11)
    public void should_find_user() {
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        Assert.assertTrue(iamClient.user("825bd3d3-82ae-404b-9aad-bbb4c53da315").exist());
        Assert.assertTrue(iamClient.findUserByEmail("cedrick.lunven@gmail.com").isPresent());
        Assert.assertFalse(iamClient.user("825bd3d3-82ae-404b-9aad-bbb4c53da318").exist());
        //Assert.assertTrue(iamClient.findRoleByName(Role.ORGANIZATION_ADMINISTRATOR).isPresent());
    }
    
    @Test
    @Order(12)
    public void should_addRoles() {
        // Given
        OrganizationsClient iamClient = new OrganizationsClient(client.getToken().get());
        Assert.assertTrue(iamClient.user("825bd3d3-82ae-404b-9aad-bbb4c53da315").exist());
        
        User u1 = iamClient.user("825bd3d3-82ae-404b-9aad-bbb4c53da315").find().get();
        u1.getRoles().stream().forEach(r -> System.out.println(r.getName() + "=" + r.getId()));
        
        //iamClient.user("825bd3d3-82ae-404b-9aad-bbb4c53da315")
        //         .updateRoles(Role.ORGANIZATION_ADMINISTRATOR, Role.USER_ADMIN_API);
    }
}
