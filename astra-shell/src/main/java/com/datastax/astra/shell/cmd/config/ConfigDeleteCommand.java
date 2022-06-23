package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Delete a block in the command.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "delete", description = "Delete section in configuration")
public class ConfigDeleteCommand extends AbstractConfigCommand 
                                 implements Runnable {
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
            getAstraRc().deleteSection(sectionName);
            getAstraRc().save();
            LoggerShell.success("Section '" + sectionName + "' has been deleted.");
        }
     }

}
