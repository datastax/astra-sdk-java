package com.datastax.astra.processor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.datastax.astra.CommandProcessor;
import com.datastax.astra.CommandTypes;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;

/**
 * Exit the program. 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class HelpProcessor implements CommandProcessor {
   
    /** {@inheritDoc} */
    @Override
    public String getDocumentation() {
        return "Command list or details with 'help <cmd>'";
    }

    /** {@inheritDoc} */
    @Override
    public void process(String commandLine) {
        try {
            CommandLine cli = new DefaultParser().parse(getOptions(), commandLine.split(" "));
            if (cli.getArgList().size() == 1) {
                Out.println("\n[Command List]", TextColor.MAGENTA);
                for(CommandTypes ct : CommandTypes.values()) {
                    ct.getProcessor().printHelpShort(ct.name());
                }
                
            } else if (cli.getArgList().size() == 2) {
                try {
                    CommandTypes
                        .valueOf(cli.getArgList().get(0))
                        .getProcessor()
                        .printHelp(cli.getArgList().get(0));
                } catch(IllegalArgumentException iaex) {
                    Out.error("Command not found '" + cli.getArgList().get(0) + "', no help available.");
                }
                
            } else {
                Out.error("Invalid help command. Please use 'help' or 'help <cmd>' ");
            }

        } catch (ParseException e) {
            Out.error("Cannot show help " + e.getMessage());
        }
        
    }

}
