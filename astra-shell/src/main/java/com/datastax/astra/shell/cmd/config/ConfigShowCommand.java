package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Allowing both syntax:
 * 
 * astra config show default
 * astra show config default 
 */
@Command(name = "show", description = "Show details for a configuration.")
public class ConfigShowCommand extends AbstractConfigCommand implements Runnable {
    
    @Required
    @Arguments(
       title = "section", 
       description = "Section in configuration file to as as defulat.")
    protected String sectionName;
    
    /** {@inheritDoc} */
    public void run() {
        if (!getAstraRc().isSectionExists(sectionName)) {
            Out.error("Section '" + sectionName + "' has not been found in config.");
        } else {
            System.out.print(getAstraRc().renderSection(sectionName));
        }
     }

}