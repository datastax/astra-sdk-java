package com.datastax.astra.cmd;

import static com.datastax.astra.ExitCode.INVALID_PARAMETER;

import java.util.Optional;

import com.datastax.astra.ansi.Out;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.utils.AstraRc;
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
     * Devops Api Organization.
     * 
     * @return
     *      orgnization
     */
    public static OrganizationsClient apiDevopsOrganizations() {
        return getInstance().getAstraClient().apiDevopsOrganizations();
    }
    
    /** Organization level context */
    private String token;
    
    /** Retrive organization informations. */
    private Organization organization;
    
    /** Database level context */
    private String databaseId;
    private String databaseRegion;
    private String databaseName;
    private String databaseKeyspace;
    
    /** Pulsar level context. *.
    
    /** Main Client. */
    private AstraClient astraClient;
    
    /**
     * Init context.
     *
     * @param token
     *      current token
     */
    public void init(String token) {
        this.token = token;
        this.astraClient = AstraClient
                .builder()
                .withToken(token)
                .build();
        this.organization = astraClient
                .apiDevopsOrganizations()
                .organization();
        
    }
    
    /**
     * Validate context with token.
     */
    public void validate() {
        if (null == getOrganization().getName()) {
            Out.error("Cannot connect to Astra: Invalid token.");
            INVALID_PARAMETER.exit();
        }
    }
    
    /**
     * Update ~/.astrarc.
     */
    public void saveConfiguration() {
        AstraRc.save(getOrganization().getName(), 
                AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
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
     * Setter accessor for attribute 'token'.
     * @param token
     * 		new value for 'token '
     */
    public void setToken(String token) {
        this.token = token;
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
     * Setter accessor for attribute 'databaseId'.
     * @param databaseId
     * 		new value for 'databaseId '
     */
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
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
     * Setter accessor for attribute 'databaseRegion'.
     * @param databaseRegion
     * 		new value for 'databaseRegion '
     */
    public void setDatabaseRegion(String databaseRegion) {
        this.databaseRegion = databaseRegion;
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
     * Setter accessor for attribute 'databaseName'.
     * @param databaseName
     * 		new value for 'databaseName '
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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
     * Setter accessor for attribute 'databaseKeyspace'.
     * @param databaseKeyspace
     * 		new value for 'databaseKeyspace '
     */
    public void setDatabaseKeyspace(String databaseKeyspace) {
        this.databaseKeyspace = databaseKeyspace;
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
     * Setter accessor for attribute 'astreClient'.
     *
     * @param astreClient
     * 		new value for 'astraClient '
     */
    public void setAstraClient(AstraClient astreClient) {
        this.astraClient = astreClient;
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
