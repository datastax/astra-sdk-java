package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.HelpCustomCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.org.ConnectCommand;
import com.datastax.astra.shell.cmd.org.ShowOrganizationsCommand;
import com.datastax.astra.shell.cmd.repl.EmptyCommand;
import com.datastax.astra.shell.cmd.repl.ExitCommand;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;

/**
 * Shell in an interactive CLI.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Cli(
  name        = "$prompt:", 
  description = "Interactive Shell for DataStax Astra",
  defaultCommand = 
    EmptyCommand.class, 
  commands       = { 
    ConnectCommand.class,
    EmptyCommand.class,
    ShowDatabasesCommand.class,
    ShowOrganizationsCommand.class,
    HelpCustomCommand.class,
    ExitCommand.class
})
public class AstraShell {
    
    public static void main(String[] args) {
        com.github.rvesse.airline.Cli<Runnable> cli = 
                new com.github.rvesse.airline.Cli<>(AstraShell.class);
        try {
            // Parsing
            Runnable cmd = cli.parse(args);
            
            // Interogation failed
            cmd.run();
            
        } catch(ParseArgumentsUnexpectedException ex) {
            Out.println("Invalid command", TextColor.RED);
            ex.printStackTrace();
            
        } catch(Exception e) {
            Out.println("An error occured during exection " + e.getMessage(), TextColor.RED);
            e.printStackTrace();
            
        }
    }

}
