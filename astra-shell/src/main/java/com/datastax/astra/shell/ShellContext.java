package com.datastax.astra.shell;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import java.util.Optional;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.oss.driver.api.core.CqlSession;

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
    
    /**
     * Access Devops Api Organization.
     * 
     * @return
     *      api organization.
     */
    public static OrganizationsClient getApiDevopsOrganizations() {
        return getInstance().getAstraClient().apiDevopsOrganizations();
    }
    
    /** Current token in use */
    private String token;
    
    /** Organization informations (prompt). */
    private Organization organization;
    
    /** Work on a db. */
    private Database database;
    
    /** Database informations. */
    private String databaseRegion;
    
    /** Main Client. */
    private AstraClient astraClient;
    
    /**
     * Based on a token will initialize the connection.
     * 
     * @param token
     *      token loaded from param
     */
    public void connect(String token) {

        // Persist Token
        this.token = token;
        
        // Initialize client
        this.astraClient = AstraClient
                .builder()
                .withToken(token)
                .build();
        
        // Use client to get Org infos
        this.organization = astraClient
                .apiDevopsOrganizations()
                .organization();
        
        // Validation of token
        if (null == getOrganization().getName()) {
            Out.error("Cannot connect to Astra: Invalid token.");
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
     * Syntaxi sugar.
     * 
     * @return
     *      cql session
     */
    public Optional<CqlSession> getCqlSession() {
        return getAstraClient().getStargateClient().cqlSession();
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
     * Getter accessor for attribute 'astreClient'.
     *
     * @return
     *       current value of 'astraClient'
     */
    public AstraClient getAstraClient() {
        return astraClient;
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
    
}
