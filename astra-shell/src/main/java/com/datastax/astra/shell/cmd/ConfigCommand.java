package com.datastax.astra.shell.cmd;

import java.util.Scanner;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.utils.AstraRcParser;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.CommandLineUtils;
import com.github.rvesse.airline.annotations.Command;

/**
 * Setup the configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "config", description = "Init configuration file ~/.astrarc")
public class ConfigCommand implements Runnable {

    /** {@inheritDoc} */
    @Override
    public void run() {
        Out.println("INFORMATION", TextColor.CYAN);
        System.out.println("To use astra cli a Token is required.");
        System.out.println("Here is the procedure to create one : https://awesome-astra.github.io/docs/pages/astra/create-token/");
        System.out.println("Save your token now, it will be used when needed (stored in ~/.astrarc)");
        System.out.println("");
        
        String token = null;
        try(Scanner scanner = new Scanner(System.in)) {
            boolean valid_token = false;
            while (!valid_token) {
                Out.print("ENTER A TOKEN ? ", TextColor.CYAN);
                token = scanner.nextLine();
                if (!token.startsWith("AstraCS:")) {
                    Out.error("Your token should start with 'AstraCS:'");
                } else {
                    try {
                        Organization o = AstraClient
                                .builder()
                                .withToken(token)
                                .build()
                                .apiDevopsOrganizations()
                                .organization();
                        valid_token = true;
                        Out.success("Valid Token, Organization is '" + o.getName() + "'");
                        AstraRcParser.save(o.getName(), AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
                        Out.success("Configuration Saved");
                    } catch(IllegalArgumentException iexo) {
                        Out.error("Your token seems invalid, it was not possible to connect to Astra."); 
                    }
                }
            }
           
            new ShowConfigCommand().run();
            System.out.println("");
            
            if (CommandLineUtils.askForConfirmation(scanner, 
                    "Do you want it to be your default organization")) {
                    AstraRcParser.save(AstraRcParser.ASTRARC_DEFAULT, 
                            AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
            }
            
            System.out.println("");
            new ShowConfigCommand().run();
            
            System.out.println("\nTo change default organization: astra default-org <orgName>");
        }
    }
    
    

}
