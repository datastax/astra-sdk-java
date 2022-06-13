package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name="default", 
    description="Set a section as default")
public class ConfigDefaultCommand extends AbstractConfigCommand implements Runnable {
   
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
            getAstraRc().copySection(sectionName, AstraRc.ASTRARC_DEFAULT);
            getAstraRc().save();
            Out.success("Section has been copied");
        }
    }
}
