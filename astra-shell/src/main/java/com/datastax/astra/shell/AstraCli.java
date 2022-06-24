package com.datastax.astra.shell;

import org.fusesource.jansi.AnsiConsole;

import com.datastax.astra.shell.cmd.HelpCommand;
import com.datastax.astra.shell.cmd.config.ConfigCreateCommand;
import com.datastax.astra.shell.cmd.config.ConfigDefaultCommand;
import com.datastax.astra.shell.cmd.config.ConfigDeleteCommand;
import com.datastax.astra.shell.cmd.config.ConfigListCommand;
import com.datastax.astra.shell.cmd.config.ConfigShowCommand;
import com.datastax.astra.shell.cmd.config.SetupCommand;
import com.datastax.astra.shell.cmd.db.CreateDatabaseCommand;
import com.datastax.astra.shell.cmd.db.DatabaseCommandUtils;
import com.datastax.astra.shell.cmd.db.CreateDatabaseCommand.CreateDatabaseCommandAlias1;
import com.datastax.astra.shell.cmd.db.DeleteDatabaseCommand;
import com.datastax.astra.shell.cmd.db.DeleteDatabaseCommand.DeleteDatabaseCommandAlias1;
import com.datastax.astra.shell.cmd.db.ShowDatabaseCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand.ShowDatabasesCommandBis;
import com.datastax.astra.shell.cmd.iam.ShowRoleCommand;
import com.datastax.astra.shell.cmd.iam.ShowRolesCommand;
import com.datastax.astra.shell.cmd.iam.ShowUserCommand;
import com.datastax.astra.shell.cmd.iam.ShowUsersCommands;
import com.datastax.astra.shell.cmd.shell.ShellCommand;
import com.datastax.astra.shell.cmd.show.ShowConfigCommand;
import com.datastax.astra.shell.cmd.show.ShowConfigsCommand;
import com.datastax.astra.shell.utils.LoggerShell;
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
    ShellCommand.class
  },
  groups = {
     @Group(
      name = "show", description = "Display entity details or list entities",
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
        ShowConfigsCommand.class,
        ShowConfigCommand.class
      }
     ),
     @Group(
       name = "list", description = "Display list of entities",
       commands = {
         // List Db in the organization (dbs | databases)
         ShowDatabaseCommand.class,
         ShowDatabasesCommand.class, 
         ShowDatabasesCommandBis.class, 
         ShowRolesCommand.class, 
         ShowUsersCommands.class,
         ShowConfigsCommand.class,
         ShowConfigCommand.class
       }
     ),
     @Group(
      name = "config",
      description = "Edit configuration file",
      commands = { 
        ConfigCreateCommand.class,
        ConfigDefaultCommand.class,
        ConfigDeleteCommand.class,
        ConfigShowCommand.class,
        ConfigListCommand.class
      }
     ),
     @Group(
      name = "create",
      description = "Create entities (db, tenant, user, role...)",
      commands = { 
         CreateDatabaseCommand.class,
         CreateDatabaseCommandAlias1.class
      }
     ),
     @Group(
      name = "delete",
      description = "Delete existing entities (db, tenant, user, role...)",
      commands = { 
          DeleteDatabaseCommand.class,
          DeleteDatabaseCommandAlias1.class,
      }
     ),
     // Noun then verb
     @Group(
       name = DatabaseCommandUtils.DB,
       description = "Commands at Database level",
       commands = { 
               CreateDatabaseCommand.class,
               CreateDatabaseCommandAlias1.class
       }
     ),
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
            
            // Enable Colored outputs
            AnsiConsole.systemInstall();
            
            // Command Line Interface
            new com.github.rvesse.airline.Cli<Runnable>(AstraCli.class)
                .parse(args)  // Find the processor for the command 
                .run();       // Run the command
            
        } catch(ParseArgumentsUnexpectedException ex) {
            LoggerShell.error("Invalid command: " + ex.getMessage());
            ex.printStackTrace();
        } catch(Exception e) {
            LoggerShell.error("Execution error:" + e.getMessage());
            e.printStackTrace();
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