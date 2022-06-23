package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Class to set a section as default in config file
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name="default", 
    description="Set a section as default")
public class ConfigDefaultCommand extends AbstractConfigCommand implements Runnable {
   
    /**
     * Section in configuration file to as as default.
     */
    @Required
    @Arguments(
       title = "section", 
       description = "Section in configuration file to as as defulat.")
    protected String sectionName;
    
    /** {@inheritDoc} */
    public void run() {
        if (!getAstraRc().isSectionExists(sectionName)) {
            LoggerShell.error("Section '" + sectionName + "' has not been found in config.");
        } else {
            getAstraRc().copySection(sectionName, AstraRc.ASTRARC_DEFAULT);
            getAstraRc().save();
            LoggerShell.success("Section has been copied");
        }
    }
}
