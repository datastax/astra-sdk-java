package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

/**
 * Create a new section in configuration.
 * 
 * "astra config create"
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "create", 
         description = "Create a new section in configuration")
public class ConfigCreateCommand extends AbstractConfigCommand implements Runnable {
    
    /**
     * Section in configuration file to as as default.
     */
    @Arguments(
       title = "section", 
       description = "Section in configuration file to as as default.")
    protected String sectionName;
    
   
    /** Authentication token used if not provided in config. */
    @Option(name = { "-t", "--token" }, 
            title = "AuthToken",
            description = "Key to use authenticate each call.")
    protected String token;
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        if (token == null) {
            LoggerShell.error("Please Provide a token with option -t, --tokebn");
            ExitCode.INVALID_PARAMETER.exit();
        }
        if (!token.startsWith("AstraCS:")) {
            LoggerShell.error("Your token should start with 'AstraCS:'");
            ExitCode.INVALID_PARAMETER.exit();
        }
        try {
            Organization o = AstraClient
                    .builder()
                    .withToken(token)
                    .build()
                    .apiDevopsOrganizations()
                    .organization();
            LoggerShell.success("Valid Token, related organization is '" + o.getName() + "'");
            if (sectionName == null) {
                sectionName = o.getName();
            }
            getAstraRc().createSectionWithToken(sectionName, token);
            getAstraRc().save();
            LoggerShell.success("Configuration Saved.\n");
        } catch(IllegalArgumentException iexo) {
            LoggerShell.error("Your token seems invalid, it was not possible to connect to Astra."); 
            ExitCode.INVALID_PARAMETER.exit();
        }
    }
}
