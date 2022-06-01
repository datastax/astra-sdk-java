package com.datastax.astra.shell;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import java.util.Optional;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.utils.AstraRc;
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
    
    public static OrganizationsClient apiDevopsOrganizations() {
        return getInstance().getAstraClient().apiDevopsOrganizations();
    }
    
    /** Current token in use */
    private String token;
    
    /** Organization informations (prompt). */
    private Organization organization;
    
    /** Database informations. */
    private String databaseId;
    
    /** Database informations. */
    private String databaseRegion;
    
    /** Database informations. */
    private String databaseName;
    
    /** Database informations. */
    private String databaseKeyspace;
    
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
        
        // Update ~/.astrarc
        AstraRc.save(
            getOrganization().getName(), 
            AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, 
            token);
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
     * Reference if the context has been initialized.
     * 
     * @return
     *      if context is initialized
     */
    public boolean initialized() {
        return getToken() != null;
    }

    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
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
     * Getter accessor for attribute 'databaseName'.
     *
     * @return
     *       current value of 'databaseName'
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Getter accessor for attribute 'databaseKeyspace'.
     *
     * @return
     *       current value of 'databaseKeyspace'
     */
    public String getDatabaseKeyspace() {
        return databaseKeyspace;
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
     * Setter accessor for attribute 'organization'.
     * @param organization
     * 		new value for 'organization '
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
   
    
}
