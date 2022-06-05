package com.datastax.astra.shell.cmd.shell;

import java.util.Scanner;

import com.datastax.astra.shell.AstraShell;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.CommandLineUtils;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.github.rvesse.airline.annotations.Command;

/**
 * The is a COMMAND from the CLI when no command name is provided
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = "shell", 
    description = "Enter interactive mode")
public class ShellCommand extends BaseCommand<ShellCommand> implements Runnable {
    
    /** {@inheritDoc} */
    public void execute() {
        
        // Show Banner
        ShellPrinter.banner();
        
        // Interactive mode
        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                ShellPrinter.prompt();
                String readline = scanner.nextLine();
                if (null!= readline) {
                    AstraShell.main(CommandLineUtils.parseCommand(readline.trim()));
                }
            }
        }
    }

}
