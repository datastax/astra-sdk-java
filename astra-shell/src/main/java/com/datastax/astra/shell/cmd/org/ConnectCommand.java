package com.datastax.astra.shell.cmd.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRcParser;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Connection to another organization.
 * 
 * 
 * connect --org mdddd
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "connect", description = "Connect to another Astra instance")
public class ConnectCommand implements Runnable {

    @Inject
    protected HelpOption<ConnectCommand> help;
    
    @Required
    @Arguments(title = "Organization Name", description = "The organization name to connect to")
    public List<String> arguments = new ArrayList<>();
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        if (arguments.size() > 1) {
            help.showHelp();
            Out.print("Invalid arguments, please use 'connect <orgName>'", TextColor.RED);
        } else {
            String orgname = arguments.get(0);
            Map<String, Map<String, String > > sections = AstraRcParser.load().getSections();
            Map<String, String> section = sections.get(orgname);
            if (section == null) {
                Out.print("Organization name not found. Valid are " + sections.keySet(), TextColor.RED);
            } else {
                String token = section.get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
                if (token ==null) {
                    Out.print("Token not found for '" + orgname + "'");
                } else {
                    ShellContext.getInstance().connect(token);
                }
            }
        }
    }

}
