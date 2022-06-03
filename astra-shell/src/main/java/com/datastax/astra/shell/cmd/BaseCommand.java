package com.datastax.astra.shell.cmd;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import javax.inject.Inject;

import org.apache.pulsar.shade.org.apache.commons.lang.StringUtils;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRcParser;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.MutuallyExclusiveWith;

/**
 * Base command.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class BaseCommand<CHILD extends BaseCommand<?>> implements Runnable {
    
    /** Each command can have a verbose mode. */
    @Option(name = { "-v", "--verbose" }, 
            description = "Enables verbose mode")
    protected boolean verbose = false;
    
    @Option(name = { "-t", "--token" }, 
            title = "Authentication Token",
            description = "Key to use authenticate each call.")
    @MutuallyExclusiveWith(tag = "authentication")
    protected String token;
    
    @Option(name = { "-org", "--organization" }, 
            title = "Organization Name",
            description= "Organization name as provided for section in ~.astrarc")
    @MutuallyExclusiveWith(tag = "authentication")
    protected String organization;
    
    @Inject
    protected HelpOption<CHILD> help;
    
    /** If no Organization provided we will look in ~.astrarc. */
    public static final String DEFAULT_ORG = "default";
    
    /**
     * Read value for Astra Token.
     * 
     * @return
     *      current token to use
     */
    protected String getAstraToken() {
        String astraToken = null;

        // Load configuration (create if needed)
        AstraRcParser config = AstraRcParser.load();
        
        // Token (-t, --token) is explicitely provided
        if (!StringUtils.isEmpty(token)) {
            astraToken = token;
        } else {
            String lookupOrg = DEFAULT_ORG;
            
            // -org is provided lookup for token in config file
            if (!StringUtils.isEmpty(organization)) {
                lookupOrg = organization;
            }
           
            // Organization name is not in config file => error
            if(!config.getSections().containsKey(lookupOrg)) {
                Out.error("Organization '" + lookupOrg + "' not found in config file.");
                
                INVALID_PARAMETER.exit();
            } else {
                // Org found, loading token
                astraToken = config.getSections()
                        .get(lookupOrg)
                        .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            }
        }
        
        return astraToken;
    }
    
    /** {@inheritDoc} */
    public void run() {
       // Connect to Astra first
       ShellContext ctx = ShellContext.getInstance();
       if (!ctx.initialized()) ctx.connect(getAstraToken());
       
       // Execute custom code
       execute();
    }
    
    /**
     * Implementation of the command.
     * 
     * @return
     *      return the command
     */
    public abstract void execute();

}
