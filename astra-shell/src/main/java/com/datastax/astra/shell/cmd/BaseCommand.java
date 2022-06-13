package com.datastax.astra.shell.cmd;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import org.apache.pulsar.shade.org.apache.commons.lang.StringUtils;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Option;

/**
 * Base command.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class BaseCommand<CHILD extends BaseCommand<?>> implements Runnable {
    
    /**
     * Default section name. 
     */
    public static final String DEFAULT_CONFIG_SECTION = "default";
    
    /** Each command can have a verbose mode. */
    @Option(name = { "-v", "--verbose" }, 
            description = "Enables verbose mode")
    protected boolean verbose = false;
    
    /** Authentication token used if not provided in config. */
    @Option(name = { "-t", "--token" }, 
            title = "AuthToken",
            description = "Key to use authenticate each call.")
    protected String token;
    
    /**
     * File on disk to reuse configuration.
     */
    @Option(name = { "-cf","--config-file" }, 
            title = "config_file",
            description= "Configuration file (default = ~/.astrarc)")
    protected String configFilename;
    
    /**
     * Section in configuration file with context
     */
    @Option(name = { "-conf, --config" }, 
            title = "config_section",
            description= "Section in configuration file to load context (default = default)")
    protected String configSectionName;
    
    /**
     * Configuration as loaded from file.
     */
    protected AstraRc astraRc;
    
    /**
     * Getter for confifguration AstraRC.
     *
     * @return
     *      configuration in AstraRc
     */
    protected AstraRc getAstraRc() {
        if (astraRc == null) {
            if (configFilename != null) {
                astraRc = new AstraRc(configFilename);
            } else {
                astraRc = new AstraRc();
            }
        }
        return astraRc;
    }
    
    
    /** {@inheritDoc} */
    public void run() {
       ShellContext ctx = ShellContext.getInstance();
       if (!ctx.isInitialized()) { 
           ctx.connect(getAstraToken());
       }
       execute();
    }
    
    /**
     * Implementation Specialization per command (Pattern Strategy)
     */
    public abstract void execute();
    
    /**
     * Read value for Astra Token.
     * 
     * @return
     *      current token to use
     */
    protected String getAstraToken() {
        String astraToken = null;
        
        // Token (-t, --token) is explicitely provided
        if (!StringUtils.isEmpty(token)) {
            astraToken = token;
        } else {
            String lookupSection = DEFAULT_CONFIG_SECTION;
            
            // --conf is provided lookup for token in config file
            if (!StringUtils.isEmpty(configSectionName)) {
                lookupSection = configSectionName;
            }
            
            if(!getAstraRc().isSectionExists(lookupSection)) {
                Out.error("Section '" + lookupSection + "' not found in config file.");
                INVALID_PARAMETER.exit();
            }
            
            astraToken = getAstraRc()
                        .getSection(lookupSection)
                        .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            if (astraToken == null) {
                Out.error("Key '" + AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN + 
                          "' not found in config section '" + lookupSection + "'");
                    INVALID_PARAMETER.exit();
            }
        }
        
        if (astraToken == null) {
            Out.warning("There is no token option (-t) and configuration file is invalid.");
            Out.info("To setup the cli: astra setup");
            Out.info("To list commands: astra help");
            ExitCode.INVALID_PARAMETER.exit();
        }
        
        return astraToken;
    }
    
    /**
     * Syntaxic sugar to get Astra Client.
     * 
     * @return
     *      astra client.
     */
    protected AstraClient getAstraClient() {
        return ShellContext.getInstance().getAstraClient();
    }
    
    /**
     * Syntax sugar api devops.
     *
     * @return
     *      api devops org
     */
    protected OrganizationsClient getApiDevopsOrg() {
        return getAstraClient().apiDevopsOrganizations();
    }
    
    /**
     * Syntaxi sugar api devops.
     *
     * @return
     *      api devops db
     */
    protected DatabasesClient getApiDevopsDb() {
        return getAstraClient().apiDevopsDatabases();
    }
    
    

}
