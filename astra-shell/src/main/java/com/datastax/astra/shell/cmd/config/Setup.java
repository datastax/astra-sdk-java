package com.datastax.astra.shell.cmd.config;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.shell.cmd.show.ShowConfigsCommand;
import com.datastax.astra.shell.utils.LoggerShell;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.github.rvesse.airline.annotations.Command;

/**
 * Setup the configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
   name = "setup", 
   description = "Intialize configuration")
public class Setup extends BaseConfigCommand implements Runnable {
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        System.out.print(ansi().eraseScreen().reset());
        ShellPrinter.banner();
        System.out.println();
        LoggerShell.print("+-------------------------------+\n", Color.CYAN);
        LoggerShell.print("+-           Setup.            -+\n", Color.CYAN);
        LoggerShell.print("+-------------------------------+\n", Color.CYAN);
        System.out.println("\nWelcome to Astra Shell. We will guide you to start.");
        
        LoggerShell.println("\n[Astra Setup]\n", Ansi.Color.CYAN);
        System.out.println("To use the cli you have to:");
        System.out.println("   • Create an Astra account on : https://astra.datastax.com");
        System.out.println("   • Create an authentication token following: https://dtsx.io/create-astra-token");
        
        LoggerShell.println("\n[Cli Setup]\n", Ansi.Color.CYAN);
        System.out.println("You will be asked to enter your token, it will be saved locally.");  
        String token = null;
        try(Scanner scanner = new Scanner(System.in)) {
            boolean valid_token = false;
            while (!valid_token) {
                LoggerShell.print("\n• Enter your token (AstraCS...) : ", Ansi.Color.MAGENTA);
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
                        
                        ShowConfigsCommand configs = new ShowConfigsCommand();
                        configs.astraRc         = this.astraRc;
                        configs.configFilename  = this.configFilename;
                        configs.run();
                        
                    } catch(Exception e) {
                        LoggerShell.error("Token provided is invalid. Please enter a valid token or quit with CTRL+C");
                    }
                }
            }
            
            LoggerShell.println("\n[What's NEXT ?]\n", Ansi.Color.CYAN);
            System.out.println("You are all set you can now:");
            System.out.println("   • Use any command, 'astra help' will get you the list");
            System.out.println("   • Try with 'astra db list'");
            System.out.println("   • Enter interactive mode using 'astra'");
            System.out.println("\nHappy Coding !");
            System.out.println("");
        }
    }
    
    

}
