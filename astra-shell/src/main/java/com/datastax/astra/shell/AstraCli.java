package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.HelpCommand;
import com.datastax.astra.shell.cmd.SetDefaultOrgCommand;
import com.datastax.astra.shell.cmd.SetupCommand;
import com.datastax.astra.shell.cmd.ShowConfigsCommand;
import com.datastax.astra.shell.cmd.ShowRoleCommand;
import com.datastax.astra.shell.cmd.ShowRolesCommand;
import com.datastax.astra.shell.cmd.ShowUserCommand;
import com.datastax.astra.shell.cmd.ShowUsersCommands;
import com.datastax.astra.shell.cmd.db.CreateDatabaseCommand;
import com.datastax.astra.shell.cmd.db.CreateDatabaseCommand.CreateDatabaseCommandAlias1;
import com.datastax.astra.shell.cmd.db.DeleteDatabaseCommand;
import com.datastax.astra.shell.cmd.db.DeleteDatabaseCommand.DeleteDatabaseCommandAlias1;
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
    SetupCommand.class,
    HelpCommand.class,
    ShellCommand.class,
    SetDefaultOrgCommand.class
  },
  groups = {
     @Group(
      name = "show", description = "Display an entity or a group of entities",
      commands = {
        // List Db in the organization (dbs | databases)
        ShowDatabasesCommand.class, 
        ShowDatabasesCommandBis.class, 
        // List Roles in the current organization/tenant
        ShowRolesCommand.class, 
        // Display details of a role (permissions)
        ShowRoleCommand.class, 
        // Display details of a user (roles, permissions, metadata)
        ShowUserCommand.class,
        // List Users in the current organization/tenant
        ShowUsersCommands.class,
        // Show current configuration
        ShowConfigsCommand.class
      }
     ),
     @Group(
      name = "create",
      description = "Create an entity",
      commands = { 
         CreateDatabaseCommand.class,
         CreateDatabaseCommandAlias1.class,
         SetupCommand.class
      }
     ),
     @Group(
      name = "delete",
      description = "Delete existing entities",
      commands = { 
          DeleteDatabaseCommand.class,
          DeleteDatabaseCommandAlias1.class,
      }
     )
  })
public class AstraCli {
    
    /**
     * Main Program.
     *
     * @param args
     *           start options for the shell
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