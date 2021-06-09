package com.datastax.astra.sdk.iam.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matching expected object to create a role.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RoleDefinition {
    
    /** Name of the new role. */
    private String name;
    
    /** Policy for our new custom role */
    private final RolePolicy policy;
    
    /**
     * Pattern builder.
     * 
     * @param orgId
     *      organiization identifiier
     * @return
     *      builder
     */
    public static RoleDefinitionBuilder builder(String orgId) {
        return new RoleDefinitionBuilder(orgId);
    }
    
    /**
     * Pattern builder.
     * 
     * @param builder
     *          constructor with builder
     */
    private RoleDefinition(RoleDefinitionBuilder builder) {
        this.name = builder.name;
        RolePolicy policy = new RolePolicy();
        policy.setEffect("allow");
        policy.setDescription(builder.description);
        policy.setActions(builder.permissions
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toList()));
        policy.setResources(builder.resources);
        this.policy = policy;
    }
    
    /**
     * Pattern builder for class {@link RoleDefinition}.
     * 
     * More infos
     * https://docs.datastax.com/en/astra/docs/db-devops-roles.html
     */
    public static class RoleDefinitionBuilder {
        private static final String RSC_PREFIX = "drn:astra:org:";
        private String name;
        private String description;
        private String organizationId;
        private List<Permission> permissions = new ArrayList<>();
        private List<String> resources = new ArrayList<>();
        
        public RoleDefinitionBuilder(String orgId) {
            this.organizationId = orgId;
            //drn:astra:org:__ORG_ID__
            this.resources.add(RSC_PREFIX + organizationId);
        }
        
        public RoleDefinitionBuilder organizationId(String o) {
            this.organizationId = o;
            return this;
        }
        public RoleDefinitionBuilder name(String n) {
            this.name = n;
            return this;
        }
        public RoleDefinitionBuilder description(String n) {
            this.description = n;
            return this;
        }
        public RoleDefinitionBuilder addPermision(Permission p) {
            this.permissions.add(p);
            return this;
        }
       
        //Dbs
        public RoleDefinitionBuilder addResourceDatabase(String dbName) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName);
            return this;
        }
        public RoleDefinitionBuilder addResourceAllDatabases() {
            return addResourceDatabase("*");
        }
        
        //Keyspaces
        public RoleDefinitionBuilder addResourceKeyspaceForDatabase(String dbName, String keyspace) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName + ":keyspace:" + keyspace);
            return this;
        }
        public RoleDefinitionBuilder addResourceAllKeyspaces() {
            return addResourceKeyspaceForDatabase("*", "*");
        }
        public RoleDefinitionBuilder addResourceAllKeyspacesForDatabase(String dbName) {
            return addResourceKeyspaceForDatabase(dbName, "*");
        }
        
        // Tables
        public RoleDefinitionBuilder addResourceTable(String dbName, String keyspace, String tableName) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName + ":keyspace:" + keyspace + ":table:" + tableName);
            return this;
        }
        public RoleDefinitionBuilder addResourceAllTablesKeyspaceForDatabase(String dbName, String keyspace) {
            return addResourceTable(dbName, keyspace, "*");
        }
        public RoleDefinitionBuilder addResourceAllTablesForDatabase(String dbName) {
            return addResourceTable(dbName, "*", "*");
        }
        public RoleDefinitionBuilder addResourceAllTables() {
            return addResourceTable("*", "*", "*");
        }
        
        public RoleDefinition build() {
            return new RoleDefinition(this);
        }
        
    }

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter accessor for attribute 'policy'.
     *
     * @return
     *       current value of 'policy'
     */
    public RolePolicy getPolicy() {
        return policy;
    }

    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }
    

}
