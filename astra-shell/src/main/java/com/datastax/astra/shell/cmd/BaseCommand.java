package com.datastax.astra.shell.cmd;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import java.io.File;

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
    @Option(name = { "-cn, --config-name" }, 
            title = "config_section",
            description= "Section in configuration file to load context (default = default)")
    protected String configSectionName;
    
    /**
     * Configuration as loaded from file.
     */
    protected AstraRc config;
    
    /**
     * Retrieve working configuration file.
     *
     * @return
     *      configuration file
     */
    protected File getConfigurationFile() {
        if (configFilename != null) {
            return new File(configFilename);
        }
        return AstraRc.getDefaultConfigFile();
    }
    
    /**
     * Load configuration from file.
     *
     * @return
     *      configuration
     */
    public AstraRc getConfig() {
       if (config == null) {
           // Load configuration (create if needed)
           if (configFilename != null) {
               File fileConfig = new File(configFilename);
               if (!fileConfig.exists() || !fileConfig.canRead()) {
                   Out.error("Cannot read configuration file " + configFilename);
                   ExitCode.INVALID_PARAMETER.exit();
               }
               config = AstraRc.load(new File(configFilename));
            } else {
                AstraRc.createIfNotExists();
                config = AstraRc.load();
            }
       }
       return config;
        
    }
    
    /** {@inheritDoc} */
    public void run() {
        
        // If no config present, ask for configuation
        if (config.getSections().isEmpty()) {
            // ask to create a token
        }
        
        String astraToken = getAstraToken();
        if (null == astraToken) {
            System.out.println("");
            Out.warning("There is no token option (-t) and configuration file is empty.");
            Out.info("To setup the cli: astra config");
            Out.info("To list commands: astra help");
            ExitCode.INVALID_PARAMETER.exit();
        }
        
       ShellContext ctx = ShellContext.getInstance();
       if (!ctx.isInitialized()) ctx.connect(astraToken);
       
       // Execute custom code
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
            
            // -org is provided lookup for token in config file
            if (!StringUtils.isEmpty(configSectionName)) {
                lookupSection = configSectionName;
                if(!config.getSections().containsKey(lookupSection)) {
                    Out.error("Section '" + lookupSection + "' not found in config file.");
                    INVALID_PARAMETER.exit();
                }
            }
            
            // Organization name is not in config file => error
            if(config.getSections().containsKey(lookupSection)) {
                astraToken = config.getSections()
                        .get(lookupSection)
                        .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            }
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
