package com.datastax.astra.shell.cmd;

import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import javax.inject.Inject;

import org.apache.pulsar.shade.org.apache.commons.lang.StringUtils;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
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

        // Load configuration from file
        AstraRc config = AstraRc.load();
        
        // Token is provided, it will be used 
        if (!StringUtils.isEmpty(token)) {
            astraToken = token;
        } else {
            String lookupOrg = DEFAULT_ORG;
            if (!StringUtils.isEmpty(organization)) {
                lookupOrg = organization;
            }
           
            if(!config.getSections().containsKey(lookupOrg)) {
                Out.error("Organization '" + lookupOrg + "' is not in the configuration file\n");
                Out.print("Available Organizations:");
                Out.print(config.getSections().keySet().toString(), TextColor.CYAN);
                INVALID_PARAMETER.exit();
            } else {
                // Org found, loading token
                astraToken = config.getSections()
                        .get(lookupOrg)
                        .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            }
        }
        // If default is not in the file, set current org as default
        if(!config.getSections().containsKey(DEFAULT_ORG)) {
            AstraRc.save(DEFAULT_ORG, 
                    AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, 
                    astraToken);
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
