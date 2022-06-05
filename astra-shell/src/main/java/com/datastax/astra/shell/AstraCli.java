package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.ConfigCommand;
import com.datastax.astra.shell.cmd.HelpCustomCommand;
import com.datastax.astra.shell.cmd.SetDefaultOrgCommand;
import com.datastax.astra.shell.cmd.ShowConfigCommand;
import com.datastax.astra.shell.cmd.ShowRoleCommand;
import com.datastax.astra.shell.cmd.ShowRolesCommand;
import com.datastax.astra.shell.cmd.ShowUserCommand;
import com.datastax.astra.shell.cmd.ShowUsersCommands;
import com.datastax.astra.shell.cmd.db.CreateDatabaseCommand;
import com.datastax.astra.shell.cmd.db.DeleteDatabaseCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand.ShowDatabasesCommandBis;
import com.datastax.astra.shell.cmd.shell.ShellCommand;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;

/**
 * Main class for the program. Will route commands to proper class 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Cli(
  name = "astra", 
  description    = "CLI for DataStax Astra™ including an interactive mode",
  defaultCommand = ShellCommand.class, // no command => interactive
  commands       = { 
    ShellCommand.class,
    HelpCustomCommand.class,
    ConfigCommand.class,
    SetDefaultOrgCommand.class
  },
  groups = {
     @Group(
      name = "show", description = "Display an entity or a group of entities",
      commands = { 
        ShowDatabasesCommand.class, ShowDatabasesCommandBis.class, 
        ShowRoleCommand.class, ShowRolesCommand.class, 
        ShowUserCommand.class, ShowUsersCommands.class,
        ShowConfigCommand.class
      }
     ),
     @Group(
      name = "create",
      description = "Create an entity",
      commands = { 
         CreateDatabaseCommand.class
      }
     ),
     @Group(
      name = "delete",
      description = "Delete an entity",
      commands = { 
          DeleteDatabaseCommand.class
      }
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
        
        try {
            
            // Command Line Interface
            new com.github.rvesse.airline.Cli<Runnable>(AstraCli.class)
                .parse(args)  // Find the processor for the command 
                .run();       // Run the command
            
        } catch(ParseArgumentsUnexpectedException ex) {
            Out.println("Invalid command: " + ex.getMessage(), TextColor.RED);
        } catch(Exception e) {
            Out.println("Execution error:" + e.getMessage(), TextColor.RED);
        }
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