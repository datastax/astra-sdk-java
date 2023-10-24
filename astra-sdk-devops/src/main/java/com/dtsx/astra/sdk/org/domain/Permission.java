package com.dtsx.astra.sdk.org.domain;


/**
 * List of permissions.
 */
public enum Permission {
    
    /**
     * Permission.
     */
    org_read("org-read","Read Organization"),
    
    /**
     * Permission.
     */
    org_write("org-write","Write Organization"),
    
    /**
     * Permission.
     */
    org_audits_read("org-audits-read","Audit Reads"),
    
    /**
     * Permission.
     */
    org_role_read("org-role-read","Read Role"),
    
    /**
     * Permission.
     */
    org_role_write("org-role-write","Write Role"),
    
    /**
     * Permission.
     */
    org_role_delete("org-role-delete","Delete Role"),
    
    /**
     * Permission.
     */
    org_external_auth_read("org-external-auth-read",""),
    
    /**
     * Permission.
     */
    org_external_auth_write("org-external-auth-write",""),
    
    /**
     * Permission.
     */
    org_notification_write("org-notification-write",""),
    
    /**
     * Permission.
     */
    org_token_read("org-token-read","Read Token"),
    
    /**
     * Permission.
     */
    org_token_write("org-token-write","Write Token"),
    
    /**
     * Permission.
     */
    org_billing_read("org-billing-read",""),
    
    /**
     * Permission.
     */
    org_billing_write("org-billing-write",""),
    
    /**
     * Permission.
     */
    org_user_read("org-user-read","Read User"),
    
    /**
     * Permission.
     */
    org_user_write("org-user-write","Write User"),
    
    /**
     * Permission.
     */
    org_db_creat("org-db-create","Create DB"),
    
    /**
     * Permission.
     */
    org_db_passwordreset("org-db-passwordreset",""),
    
    /**
     * Permission.
     */
    org_db_terminate("org-db-terminate","Terminate DB"),
    
    /**
     * Permission.
     */
    org_db_suspend("org-db-suspend","Suspend DB"),
    
    /**
     * Permission.
     */
    org_db_addpeering("org-db-addpeering","Add Peering"),
    
    /**
     * Permission.
     */
    org_db_managemigratorproxy("org-db-managemigratorproxy",""),
    
    /**
     * Permission.
     */
    org_db_expand("org-db-expand","Expand DB"),
    
    /**
     * Permission.
     */
    org_db_view("org-db-view","View DB"),
    
    /**
     * Permission.
     */
    db_all_keyspace_create("db-all-keyspace-create","Create Keyspace (All)"),
    
    /**
     * Permission.
     */
    db_all_keyspace_describe("db-all-keyspace-describe","Describe Keyspace (All)"),
    
    /**
     * Permission.
     */
    db_keyspace_grant("db-keyspace-grant","Grant Keyspace"),
    
    /**
     * Permission.
     */
    db_keyspace_modify("db-keyspace-modify","Modify Keyspace"),
    
    /**
     * Permission.
     */
    db_keyspace_describe("db-keyspace-describe","Describe Keyspace"),
    
    /**
     * Permission.
     */
    db_keyspace_create("db-keyspace-create","Create Keyspace"),

    /**
     * Permission.
     */
    db_keyspace_authorize("db-keyspace-authorize","Grant Keyspace"),
    
    /**
     * Permission.
     */
    db_keyspace_alter("db-keyspace-alter","Alter Keyspace"),
    
    /**
     * Permission.
     */
    db_keyspace_drop("db-keyspace-drop","Drop Keyspace"),
    
    /**
     * Permission.
     */
    db_table_select("db-table-select","Select Table"),
    
    /**
     * Permission.
     */
    db_table_grant("db-table-grant","Grant Table"),
    
    /**
     * Permission.
     */
    db_table_modify("db-table-modify","Modify table"),
    
    /**
     * Permission.
     */
    db_table_describe("db-table-describe","Describe Table"),
    
    /**
     * Permission.
     */
    db_table_create("db-table-create","Create Table"),
    
    /**
     * Permission.
     */
    db_table_authorize("db-table-authorize","Authorize Table"),
    
    /**
     * Permission.
     */
    db_table_alter("db-table-alter","Alter Table"),
    
    /**
     * Permission.
     */
    db_table_drop ("db-table-drop","Drop Table"),
    
    /**
     * Permission.
     */
    db_graphql("db-graphql","Access Graphql"),
    
    /**
     * Permission.
     */
    db_rest("db-rest","Access Rest"),
    
    /**
     * Permission.
     */
    db_cql("db-cql","Access CQL");
    
    /**
     * Code
     */
    private String code;
    
    /**
     * Description
     */
    private String description;
    
    /**
     * Permission.
     *
     * @param code
     *      code
     * @param description
     *      description
     */
    private Permission(String code, String description) {
        this.code        = code;
        this.description = description;
    }
    
    /**
     * Return the code.
     *
     * @return
     *      code value.
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Return the description.
     * 
     * @return
     *      description
     */
    public String getDescription() {
        return description;
    }

}
