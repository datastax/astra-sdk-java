package com.datastax.astra.sdk.organizations.domain;


/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public enum Permission {
    
    /**
     * 
     */
    org_read("org-read","Read Organization"),
    
    /**
     * 
     */
    org_write("org-write","Write Organization"),
    
    /**
     * 
     */
    org_audits_read("org-audits-read","Audit Reads"),
    
    /**
     * 
     */
    org_role_read("org-role-read","Read Role"),
    
    /**
     * 
     */
    org_role_write("org-role-write","Write Role"),
    
    /**
     * 
     */
    org_role_delete("org-role-delete","Delete Role"),
    
    /**
     * 
     */
    org_external_auth_read("org-external-auth-read",""),
    
    /**
     * 
     */
    org_external_auth_write("org-external-auth-write",""),
    
    /**
     * 
     */
    org_notification_write("org-notification-write",""),
    
    /**
     * 
     */
    org_token_read("org-token-read","Read Token"),
    
    /**
     * 
     */
    org_token_write("org-token-write","Write Token"),
    
    /**
     * 
     */
    org_billing_read("org-billing-read",""),
    
    /**
     * 
     */
    org_billing_write("org-billing-write",""),
    
    /**
     * 
     */
    org_user_read("org-user-read","Read User"),
    
    /**
     * 
     */
    org_user_write("org-user-write","Write User"),
    
    /**
     * 
     */
    org_db_creat("org-db-create","Create DB"),
    
    /**
     * 
     */
    org_db_passwordreset("org-db-passwordreset",""),
    
    /**
     * 
     */
    org_db_terminate("org-db-terminate","Terminate DB"),
    
    /**
     * 
     */
    org_db_suspend("org-db-suspend","Suspend DB"),
    
    /**
     * 
     */
    org_db_addpeering("org-db-addpeering","Add Peering"),
    
    /**
     * 
     */
    org_db_managemigratorproxy("org-db-managemigratorproxy",""),
    
    /**
     * 
     */
    org_db_expand("org-db-expand","Expand DB"),
    
    /**
     * 
     */
    org_db_view("org-db-view","View DB"),
    
    /**
     * 
     */
    db_all_keyspace_create("db-all-keyspace-create","Create Keyspace (All)"),
    
    /**
     * 
     */
    db_all_keyspace_describe("db-all-keyspace-describe","Describe Keyspace (All)"),
    
    /**
     * 
     */
    db_keyspace_grant("db-keyspace-grant","Grant Keyspace"),
    
    /**
     * 
     */
    db_keyspace_modify("db-keyspace-modify","Modify Keyspace"),
    
    /**
     * 
     */
    db_keyspace_describe("db-keyspace-describe","Describe Keyspace"),
    
    /**
     * 
     */
    db_keyspace_create("db-keyspace-create","Create Keyspace"),

    /**
     * 
     */
    db_keyspace_authorize("db-keyspace-authorize","Grant Keyspace"),
    
    /**
     * 
     */
    db_keyspace_alter("db-keyspace-alter","Alter Keyspace"),
    
    /**
     * 
     */
    db_keyspace_drop("db-keyspace-drop","Drop Keyspace"),
    
    /**
     * 
     */
    db_table_select("db-table-select","Select Table"),
    
    /**
     * 
     */
    db_table_grant("db-table-grant","Grant Table"),
    
    /**
     * 
     */
    db_table_modify("db-table-modify","Modify table"),
    
    /**
     * 
     */
    db_table_describe("db-table-describe","Describe Table"),
    
    /**
     * 
     */
    db_table_create("db-table-create","Create Table"),
    
    /**
     * 
     */
    db_table_authorize("db-table-authorize","Authorize Table"),
    
    /**
     * 
     */
    db_table_alter("db-table-alter","Alter Table"),
    
    /**
     * 
     */
    db_table_drop ("db-table-drop","Drop Table"),
    
    /**
     * 
     */
    db_graphql("db-graphql","Access Graphql"),
    
    /**
     * 
     */
    db_rest("db-rest","Access Rest"),
    
    /**
     * 
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
