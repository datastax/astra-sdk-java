package com.datastax.astra.shell.cmd.repl;

import java.util.Scanner;

import com.datastax.astra.shell.AstraCli;
import com.datastax.astra.shell.AstraShell;
import com.datastax.astra.shell.ExitCode;
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
    name = ".", 
    description = "If no command, entering interactive mode.")
public class ReplCommand extends BaseCommand<ReplCommand> implements Runnable {
   
    /** {@inheritDoc} */
    public void execute() {
        
        // astra -h, --help
        if (help.showHelpIfRequested()) {
            AstraCli.exec("help");
            System.exit(ExitCode.SUCCESS.getCode());
        }
            
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
