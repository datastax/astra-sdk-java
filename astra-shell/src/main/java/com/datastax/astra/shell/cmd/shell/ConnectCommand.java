package com.datastax.astra.shell.cmd.shell;

import java.util.Optional;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Connection to another organization.
 * 
 * Should be replace by config load.
 *
 * connect --org mdddd
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "connect", description = "Connect to another Astra instance")
public class ConnectCommand extends BaseCommand<ConnectCommand>{
    
    @Required
    @Arguments(title = "configName", description = "Configura")
    public String configName;
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        if (!getAstraRc().isSectionExists(configName)) {
            Out.error("Config '" + configName + "' has not been found in configuration file.");
        } else {
            Optional<String> newToken = 
                    getAstraRc().getSectionKey(configName, AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            if (newToken.isPresent()) {
                ShellContext.getInstance().connect(newToken.get());
            } else {
                Out.error("Token not found for '" + configName + "'");
            }
        }
    }
}
