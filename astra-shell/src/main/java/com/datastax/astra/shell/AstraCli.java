package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.HelpCustomCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.org.ShowOrganizationsCommand;
import com.datastax.astra.shell.cmd.repl.ReplCommand;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;

/**
 * Main class for the program. Will route commands to proper class 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Cli(
  name = "astra", 
  description    = "CLI for DataStax Astra™ including an interactive mode",
  defaultCommand = ReplCommand.class, // no command provided => REPL
  commands       = { 
    ReplCommand.class,
    HelpCustomCommand.class
  },
  
  groups = {
        @Group(
            name = "show",
            description = "Listing details of an entity or entity list",
            commands = { ShowOrganizationsCommand.class, ShowDatabasesCommand.class }
        )
  })
public class AstraCli {
    
    /**
     * Main Program.
     *
     * @param args
     *           start options for the shell
     * @throws Exception
     *           error during parsing or interpreting command
     */
    public static void main(String[] args) {
        
        // Show Banner
        ShellPrinter.banner();
        
        // Command Line Interface
        new com.github.rvesse.airline.Cli<Runnable>(AstraCli.class)
            .parse(args)  // Find the processor for the command 
            .run();       // Run the command
    }
    
    /**
     * Run the Program with varArgs.
     * 
     * @param args
     *      arguments
     */
    public static void exec(String ...args) {
        main(args);
    }
    
}