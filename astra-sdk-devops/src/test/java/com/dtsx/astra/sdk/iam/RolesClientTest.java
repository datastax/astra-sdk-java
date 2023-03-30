package com.dtsx.astra.sdk.iam;

import com.dtsx.astra.sdk.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.ApiDevopsClientTest;
import com.dtsx.astra.sdk.org.RolesClient;
import com.dtsx.astra.sdk.org.domain.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolesClientTest extends AbstractDevopsApiTest {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDevopsClientTest.class);

    @Test
    @Order(1)
    public void shouldListRoles() {
        // When
        List<Role> listRoles =  getApiDevopsClient()
                .roles()
                .findAll()
                .collect(Collectors.toList());
        // Then
        Assertions.assertTrue(listRoles.size() > 5);
        for (Role r : listRoles) {
            Assertions.assertNotNull(r);
            Assertions.assertNotNull(r.getName());
            LOGGER.info("+ " + r.getName() + "=" + r.getId());
        }
    }

    @Test
    @Order(2)
    public void shouldFindRoleByName() {
        // When
        Optional<Role> role =  getApiDevopsClient().roles()
                .findByName(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        Assertions.assertTrue(role.isPresent());
    }

    @Test
    @Order(3)
    public void shouldFindDefaultRoles() {
        // When
        Optional<Role> role = getApiDevopsClient().roles().find(DefaultRoles.DATABASE_ADMINISTRATOR);
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
    }

    @Test
    @Order(4)
    public void shouldFindRoleById() {
        Optional<Role> role = getApiDevopsClient().roles().find(DefaultRoles.DATABASE_ADMINISTRATOR);
        Optional<Role> role2 = getApiDevopsClient().roles().find(role.get().getId());
        Assertions.assertTrue(role2.isPresent());
    }

    private static String customRole;
    private static String customRoleId;

    @Test
    @Order(5)
    public void shouldCreateRole() {
        customRole = "sdk_java_junit_role" + UUID.randomUUID().toString().substring(0,7);
        RoleDefinition cr = RoleDefinition.builder(getApiDevopsClient().getOrganizationId())
                .name(customRole)
                .description("Only the brave")
                .addPermision(Permission.db_all_keyspace_create)
                .addResourceAllDatabases()
                .addResourceAllKeyspaces()
                .addResourceAllTables()
                .build();
        CreateRoleResponse res = getApiDevopsClient().roles().create(cr);
        customRoleId = res.getRoleId();
        Assertions.assertTrue(getApiDevopsClient().roles().find(customRoleId).isPresent());
        LOGGER.info("Role created name=" + customRole + ", id=" + customRoleId);
    }

    @Test
    @Order(6)
    public void shouldDeleteRole() {
        // Given
        RolesClient rolesClient = getApiDevopsClient().roles();
        Assertions.assertTrue(rolesClient.exist(customRoleId));
        // When
        rolesClient.delete(customRoleId);
        // Then
        Assertions.assertFalse(rolesClient.exist(customRoleId));
    }

    @Test
    @Order(7)
    public void shouldUpdateRole() {
        RolesClient rolesClient = getApiDevopsClient().roles();
        // When
        CreateRoleResponse res = rolesClient
                .create(RoleDefinition.builder(getApiDevopsClient().getOrganizationId())
                .name("RoleTMP")
                .description("Only the brave")
                .addPermision(Permission.db_all_keyspace_create)
                .addResourceAllDatabases()
                .addResourceAllKeyspaces()
                .addResourceAllTables()
                .build());
        // Then
        Assertions.assertTrue(rolesClient.exist(res.getRoleId()));
        // When
        rolesClient.update(res.getRoleId(), RoleDefinition
                .builder(getApiDevopsClient().getOrganizationId())
                .name("RoleTMP")
                .description("updated descriptiom")
                .addPermision(Permission.db_cql)
                .build());
        // When
        Role r = rolesClient.find(res.getRoleId()).get();
        // Then
        Assertions.assertTrue(r.getPolicy().getActions().contains(Permission.db_cql.getCode()));
        Assertions.assertTrue(r.getPolicy().getDescription().equals("updated descriptiom"));
        // When
        rolesClient.delete(res.getRoleId());
        // Then
        Assertions.assertFalse(rolesClient.exist(res.getRoleId()));
    }
}
