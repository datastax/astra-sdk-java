package com.datastax.astra.sdk;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.iam.IamClient;
import com.datastax.astra.sdk.iam.domain.CreateRoleResponse;
import com.datastax.astra.sdk.iam.domain.CreateTokenResponse;
import com.datastax.astra.sdk.iam.domain.Permission;
import com.datastax.astra.sdk.iam.domain.RoleDefinition;
import com.datastax.astra.sdk.iam.domain.User;

import graphql.Assert;

@TestMethodOrder(OrderAnnotation.class)
public class T08_Devops_Iam_IntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[Astra DEVOPS ROLES Test Suite]" + ANSI_RESET);
        appToken = Optional.ofNullable("AstraCS:abjIANeldqrcOQmILeACwhOr:5daf79ff81bd667ea29179eac08ae98e047c0f42ad462bf66b2282b372eb641a");
    }
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        System.out.println(ANSI_YELLOW + "- Parameter validation" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(null));
    }
    
    @Test
    @Order(2)
    public void should_connect_to_astra_withAstraClient() {
        System.out.println(ANSI_YELLOW + "- Connection with AstraClient" + ANSI_RESET);
        // Given
        Assertions.assertTrue(appToken.isPresent());
        // When
        new IamClient(appToken.get()).roles().forEach(role -> {
                   System.out.println(role.getId() 
                       + "-" + role.getType() 
                       + "-" + role.getName() 
                       + "-" + role.getPolicy());
        });
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Can connect to ASTRA with token");
    }
    
    @Test
    @Order(3)
    public void should_find_role_byId() {
        System.out.println(ANSI_YELLOW + "- Connection with AstraClient" + ANSI_RESET);
        Assertions.assertTrue(appToken.isPresent());
        System.out.println(new IamClient(appToken.get())
            .role("b4ed0e9e-67e8-47b6-8b58-c6629be961a9")
            .find()
            .get()
            .getName());
    }
    
    @Test
    @Order(4)
    public void should_create_a_role() {
        System.out.println(ANSI_YELLOW + "- Creating a role" + ANSI_RESET);
        
        IamClient iamClient = new IamClient(appToken.get());
        
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
        IamClient iamClient = new IamClient(appToken.get());
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
        IamClient iamClient = new IamClient(appToken.get());
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
        IamClient iamClient = new IamClient(appToken.get());
        iamClient.tokens().forEach(t->System.out.println(t.getRoles()));
    }
    
    @Test
    @Order(8)
    public void should_create_token() {
        System.out.println(ANSI_YELLOW + "- Creating a Token" + ANSI_RESET);
        IamClient iamClient = new IamClient(appToken.get());
        CreateTokenResponse res = iamClient.createToken("write");
        System.out.println(res.getClientId());
        Assert.assertTrue(iamClient.token(res.getClientId()).exist());
    }
    
    @Test
    @Order(9)
    public void should_delete_token() {
        IamClient iamClient = new IamClient(appToken.get());
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
        IamClient iamClient = new IamClient(appToken.get());
        iamClient.users().map(User::getEmail).forEach(System.out::println);
    }
}
