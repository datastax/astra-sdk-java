package com.datastax.astra.shell.cmd.config;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.shell.out.LoggerShell;
import com.datastax.astra.shell.out.ShellPrinter;
import com.github.rvesse.airline.annotations.Command;

/**
 * Setup the configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "setup", description = "Initialize configuration file")
public class ConfigSetup extends BaseConfigCommand implements Runnable {
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        // On setup you must have output
        verbose = true;
        System.out.print(ansi().eraseScreen().reset());
        ShellPrinter.banner();
        System.out.println();
        ShellPrinter.println("+-------------------------------+", Color.CYAN);
        ShellPrinter.println("+-           Setup.            -+", Color.CYAN);
        ShellPrinter.println("+-------------------------------+", Color.CYAN);
        System.out.println("\nWelcome to Astra Shell. We will guide you to start.");
        ShellPrinter.println("\n[Astra Setup]", Ansi.Color.CYAN);
        
        System.out.println("To use the cli you have to:");
        System.out.println("   • Create an Astra account on : https://astra.datastax.com");
        System.out.println("   • Create an authentication token following: https://dtsx.io/create-astra-token");
        
        ShellPrinter.println("\n[Cli Setup]", Ansi.Color.CYAN);
        System.out.println("You will be asked to enter your token, it will be saved locally.");  
        String token = null;
        try(Scanner scanner = new Scanner(System.in)) {
            boolean valid_token = false;
            while (!valid_token) {
                ShellPrinter.println("\n• Enter your token (AstraCS...) : ", Ansi.Color.MAGENTA);
                token = scanner.nextLine();
                if (!token.startsWith("AstraCS:")) {
                    LoggerShell.error("Your token should start with 'AstraCS:'");
                } else {
                    try {
                       ;
                        ConfigCreate ccc = new ConfigCreate();
                        ccc.token = token;
                        ccc.sectionName =  new OrganizationsClient(token).organization().getName();
                        ccc.run();
                        valid_token = true;
                        
                        ConfigGet configs      = new ConfigGet();
                        configs.astraRc         = this.astraRc;
                        configs.configFilename  = this.configFilename;
                        configs.run();
                        
                    } catch(Exception e) {
                        LoggerShell.error("Token provided is invalid. Please enter a valid token or quit with CTRL+C");
                    }
                }
            }
            ShellPrinter.println("\n[What's NEXT ?]", Ansi.Color.CYAN);
            System.out.println("You are all set. You can now:");
            System.out.println("   • Use any command, 'astra help' will get you the list");
            System.out.println("   • Try with 'astra db list'");
            System.out.println("   • Enter interactive mode using 'astra'");
            System.out.println("\nHappy Coding !");
            System.out.println("");
        }
    }

}
