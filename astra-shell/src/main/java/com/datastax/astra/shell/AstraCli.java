package com.datastax.astra.shell;

import org.fusesource.jansi.AnsiConsole;

import com.datastax.astra.shell.cmd.HelpCommand;
import com.datastax.astra.shell.cmd.config.ConfigCreate;
import com.datastax.astra.shell.cmd.config.ConfigDefault;
import com.datastax.astra.shell.cmd.config.ConfigDelete;
import com.datastax.astra.shell.cmd.config.ConfigList;
import com.datastax.astra.shell.cmd.config.ConfigShow;
import com.datastax.astra.shell.cmd.config.Setup;
import com.datastax.astra.shell.cmd.db.Db;
import com.datastax.astra.shell.cmd.db.DbCreateCli;
import com.datastax.astra.shell.cmd.db.DbDelete;
import com.datastax.astra.shell.cmd.db.DbListCli;
import com.datastax.astra.shell.cmd.shell.ShellCommand;
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
    Setup.class,
    HelpCommand.class,
    ShellCommand.class
  },
  groups = {
          @Group(name = Db.DB, description = "Commands acting of database", commands = { 
                  DbCreateCli.class,
                  DbListCli.class
          }),
          @Group(name = "config", description = "Edit configuration file", commands = { 
                  ConfigCreate.class,
                  ConfigDefault.class,
                  ConfigDelete.class,
                  ConfigShow.class,
                  ConfigList.class
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
            //ex.printStackTrace();
        } catch(Exception e) {
            LoggerShell.error("Execution error:" + e.getMessage());
            //e.printStackTrace();
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