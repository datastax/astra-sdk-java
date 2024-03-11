package com.datastax.astra.devops.org.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matching expected object to create a role.
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
     * @see <a href="https://docs.datastax.com/en/astra/docs/db-devops-roles.html">Roles</a>
     */
    public static class RoleDefinitionBuilder {
        
        /** Prefix. */
        private static final String RSC_PREFIX = "drn:astra:org:";
        
        /** role name. */
        private String name;
        
        /** role description. */
        private String description;
        
        /** organization id. */
        private String organizationId;
        
        /** role permissions. */
        private List<Permission> permissions = new ArrayList<>();
        
        /** role resources. */
        private List<String> resources = new ArrayList<>();
        
        /**
         * Role builder.
         *
         * @param orgId
         *      organization id.
         */
        public RoleDefinitionBuilder(String orgId) {
            this.organizationId = orgId;
            //drn:astra:org:__ORG_ID__
            this.resources.add(RSC_PREFIX + organizationId);
        }
        
        /**
         * Provide organizationid.
         * 
         * @param o
         *      identifier
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder organizationId(String o) {
            this.organizationId = o;
            return this;
        }
        
        /**
         * Provide name.
         * 
         * @param n
         *      name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder name(String n) {
            this.name = n;
            return this;
        }
        
        /**
         * Provide description.
         * 
         * @param n
         *      description
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder description(String n) {
            this.description = n;
            return this;
        }
        
        /**
         * Provide addPermision.
         * 
         * @param p
         *      addPermision
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addPermision(Permission p) {
            this.permissions.add(p);
            return this;
        }
       
        /**
         * Provide dbName.
         * 
         * @param dbName
         *      database name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceDatabase(String dbName) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName);
            return this;
        }
        
        /**
         * Add resources.
         * 
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceAllDatabases() {
            return addResourceDatabase("*");
        }
        
        /**
         * Add keyspace database.
         * 
         * @param dbName
         *      db name
         * @param keyspace
         *      keyspace name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceKeyspaceForDatabase(String dbName, String keyspace) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName + ":keyspace:" + keyspace);
            return this;
        }
        
        /**
         * Add all resources.
         *
         * @return
         *      get all resources
         */
        public RoleDefinitionBuilder addResourceAllKeyspaces() {
            return addResourceKeyspaceForDatabase("*", "*");
        }
        
        /**
         * Add resources for the database
         * 
         * @param dbName
         *      db name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceAllKeyspacesForDatabase(String dbName) {
            return addResourceKeyspaceForDatabase(dbName, "*");
        }
        
        /**
         * Add table resources.
         * 
         * @param dbName
         *      database name
         * @param keyspace
         *      keyspace name
         * @param tableName
         *      table name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceTable(String dbName, String keyspace, String tableName) {
            this.resources.add(RSC_PREFIX + organizationId + ":db:" + dbName + ":keyspace:" + keyspace + ":table:" + tableName);
            return this;
        }
        
        /**
         * Add resources.
         *
         * @param dbName
         *      database name
         * @param keyspace
         *      keyspace name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceAllTablesKeyspaceForDatabase(String dbName, String keyspace) {
            return addResourceTable(dbName, keyspace, "*");
        }
        
        /**
         * Add resources.
         *
         * @param dbName
         *      database name
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceAllTablesForDatabase(String dbName) {
            return addResourceTable(dbName, "*", "*");
        }
        
        /**
         * Add resources.
         *
         * @return
         *      self reference
         */
        public RoleDefinitionBuilder addResourceAllTables() {
            return addResourceTable("*", "*", "*");
        }
        
        /**
         * Create the role definition.
         *
         * @return
         *      target role definition
         */
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
