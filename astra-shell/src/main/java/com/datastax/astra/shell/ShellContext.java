package com.datastax.astra.shell;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.shell.utils.LoggerShell;

/**
 * Hold the context of CLI to know where we are.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellContext {

    /**
     * Singleton Pattern, private intance.
     */
    private static ShellContext _instance;
    
    /**
     * Default Constructor for Shell.
     */
    private ShellContext() {}
    
    /**
     * Singleton Pattern.
     *
     * @return
     *      current instance of context
     */
    public static synchronized ShellContext getInstance() {
        if (_instance == null) {
            _instance = new ShellContext();
        }
        return _instance;
    }
    
    /** Current token in use */
    private String token;
    
    /** Organization informations (prompt). */
    private Organization organization;
    
    /** Work on a db. */
    private Database database;
    
    /** Database informations. */
    private String databaseRegion;
    
    /** Hold a reference for the Api Devops. */
    private DatabasesClient apiDevopsDatabases;
    
    /** Hold a reference for the Api Devops. */
    private OrganizationsClient apiDevopsOrganizations;
    
    /** Hold a reference for the Api Devops. */
    private StreamingClient apiDevopsStreaming;
    
    /**
     * Based on a token will initialize the connection.
     * 
     * @param token
     *      token loaded from param
     */
    public void connect(String token) {

        // Persist Token
        this.token = token;
        
        // Initializing Http Clients
        apiDevopsOrganizations  = new OrganizationsClient(token);
        apiDevopsDatabases      = new DatabasesClient(token);  
        apiDevopsStreaming      = new StreamingClient(token);
        
        // Use client to get Org infos
        this.organization = apiDevopsOrganizations.organization();
        
        // Validation of token
        if (null == getOrganization().getName()) {
            LoggerShell.error("Cannot connect to Astra: Invalid token.");
            INVALID_PARAMETER.exit();
        }
        
    }
    
    /**
     * Setter accessor for attribute 'database'.
     * @param database
     *      new value for 'database '
     */
    public void useDatabase(Database database) {
        this.database = database;
        // Initialize to default region
        this.databaseRegion = this.database.getInfo().getRegion();
    }
    
    /**
     * Setter accessor for attribute 'databaseRegion'.
     * @param databaseRegion
     *      new value for 'databaseRegion '
     */
    public void useRegion(String databaseRegion) {
        this.databaseRegion = databaseRegion;
    }
    
    /**
     * Reference if the context has been initialized.
     * 
     * @return
     *      if context is initialized
     */
    public boolean isInitialized() {
        return getToken() != null;
    }
   
    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'organization'.
     *
     * @return
     *       current value of 'organization'
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Getter accessor for attribute 'database'.
     *
     * @return
     *       current value of 'database'
     */
    public Database getDatabase() {
        return database;
    }
    
    /**
     * Getter accessor for attribute 'databaseRegion'.
     *
     * @return
     *       current value of 'databaseRegion'
     */
    public String getDatabaseRegion() {
        return databaseRegion;
    }
    
    /**
     * Drop focus on current database.
     */
    public void exitDatabase() {
        this.database = null;
        this.databaseRegion = null;
    }

    /**
     * Getter accessor for attribute 'apiDevopsDatabases'.
     *
     * @return
     *       current value of 'apiDevopsDatabases'
     */
    public DatabasesClient getApiDevopsDatabases() {
        return apiDevopsDatabases;
    }

    /**
     * Setter accessor for attribute 'apiDevopsDatabases'.
     * @param apiDevopsDatabases
     * 		new value for 'apiDevopsDatabases '
     */
    public void setApiDevopsDatabases(DatabasesClient apiDevopsDatabases) {
        this.apiDevopsDatabases = apiDevopsDatabases;
    }

    /**
     * Getter accessor for attribute 'apiDevopsStreaming'.
     *
     * @return
     *       current value of 'apiDevopsStreaming'
     */
    public StreamingClient getApiDevopsStreaming() {
        return apiDevopsStreaming;
    }

    /**
     * Setter accessor for attribute 'apiDevopsStreaming'.
     * @param apiDevopsStreaming
     * 		new value for 'apiDevopsStreaming '
     */
    public void setApiDevopsStreaming(StreamingClient apiDevopsStreaming) {
        this.apiDevopsStreaming = apiDevopsStreaming;
    }

    /**
     * Setter accessor for attribute 'apiDevopsOrganizations'.
     * @param apiDevopsOrganizations
     * 		new value for 'apiDevopsOrganizations '
     */
    public void setApiDevopsOrganizations(OrganizationsClient apiDevopsOrganizations) {
        this.apiDevopsOrganizations = apiDevopsOrganizations;
    }

    /**
     * Getter accessor for attribute 'apiDevopsOrganizations'.
     *
     * @return
     *       current value of 'apiDevopsOrganizations'
     */
    public OrganizationsClient getApiDevopsOrganizations() {
        return apiDevopsOrganizations;
    }
    
}
