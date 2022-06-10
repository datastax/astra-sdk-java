package com.datastax.astra.shell.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name="default-org", 
    description="Edit default organization in configuration")
public class SetDefaultOrgCommand implements Runnable {
    
    @Required
    @Arguments(title = "Organization Name", description = "The organization name to connect to")
    public List<String> arguments = new ArrayList<>();
   
    /** {@inheritDoc} */
    public void run() {
        if (arguments.size() != 1) {
            Out.print("Invalid arguments, please use 'default-org <orgName>'", TextColor.RED);
        } else {
            String orgname = arguments.get(0);
            Map<String, Map<String, String > > sections = AstraRc.load().getSections();
            Map<String, String> section = sections.get(orgname);
            if (section == null) {
                Out.error("Organization name not found.");
            } else {
                String token = section.get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
                AstraRc.save(AstraRc.ASTRARC_DEFAULT, 
                        AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
                
                Out.success("Configuration has been updated\n");
                new ShowConfigsCommand().run();
            }
        }
    }

}
