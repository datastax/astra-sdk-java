package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.org.domain.*;
import com.dtsx.astra.sdk.org.iam.RoleClient;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrgRolesTest  extends AbstractDevopsApiTest {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgCoreTest.class);

    @Test
    @Order(1)
    public void shouldListRoles() {
        LOGGER.info("Should List Roles");
        // When
        List<Role> listRoles = new OrganizationsClient(getToken()).roles().collect(Collectors.toList());
        // Then
        Assertions.assertTrue(listRoles.size() > 5);
        LOGGER.info("Roles can be retrieved");
        for (Role r : listRoles) {
            Assertions.assertNotNull(r);
            Assertions.assertNotNull(r.getName());
            LOGGER.info("+ " + r.getName() + "=" + r.getId());
        }
        LOGGER.info("Roles are populated");
    }

    @Test
    @Order(2)
    public void shouldFindRoleByName() {
        LOGGER.info("Find a role by its Name");
        // When
        Optional<Role> role = new OrganizationsClient(getToken())
                .findRoleByName(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
        LOGGER.info("Role found");
    }

    @Test
    @Order(3)
    public void shouldFindDefaultRoles() {
        LOGGER.info("Find a role by its Name");
        // When
        Optional<Role> role = new OrganizationsClient(getToken()).role(DefaultRoles.DATABASE_ADMINISTRATOR).find();
        // Then (it is a default role, should be there)
        Assertions.assertTrue(role.isPresent());
        LOGGER.info("Role found");
    }

    @Test
    @Order(4)
    public void shouldFindRoleById() {
        LOGGER.info("Find a role by its ids");
        Optional<Role> role = new OrganizationsClient(getToken()).role(DefaultRoles.DATABASE_ADMINISTRATOR).find();

        Optional<Role> role2 = new OrganizationsClient(getToken())
                .role(role.get().getId())
                .find();
        Assertions.assertTrue(role2.isPresent());
        LOGGER.info("Role found");
    }

    private static String customRole;
    private static String customRoleId;

    @Test
    @Order(5)
    public void shouldCreateRole() {
        LOGGER.info("Creating a role");
        customRole = "sdk_java_junit_role" + UUID.randomUUID().toString().substring(0,7);
        OrganizationsClient iamClient = new OrganizationsClient(getToken());

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
        Assertions.assertTrue(new OrganizationsClient(getToken()).role(customRoleId).find().isPresent());
        LOGGER.info("Role created name=" + customRole + ", id=" + customRoleId);
    }

    @Test
    @Order(6)
    public void shouldDeleteRole() {
        LOGGER.info("Deleting a role");
        // Given
        RoleClient rc = new OrganizationsClient(getToken()).role(customRoleId);
        Assertions.assertTrue(rc.exist());
        LOGGER.info("Role found");
        // When
        rc.delete();
        // Then
        Assertions.assertFalse(rc.exist());
        LOGGER.info("Role deleted name=" + customRole + ", id=" + customRoleId);
    }

    @Test
    @Order(7)
    public void shouldUpdateRole() {
        LOGGER.info("Deleting a role");
        OrganizationsClient clientOrg = new OrganizationsClient(getToken());
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
}
