package com.datastax.astra.shell.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Scanner;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.utils.AstraRcParser;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Command;

/**
 * Setup the configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "config", description = "Ask for token and persist config in ~/.astrarc")
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
                    Out.error("Your token should start with 'AstraCS:'\n");
                } else {
                    try {
                        Organization o = AstraClient
                                .builder()
                                .withToken(token)
                                .build()
                                .apiDevopsOrganizations()
                                .organization();
                        valid_token = true;
                        Out.success("Valid Token, Organization '" + o.getName() + "'");
                        AstraRcParser.save(o.getName(), AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
                        Out.success("Configuration Saved'\n");
                    } catch(IllegalArgumentException iexo) {
                        Out.error("Your token seems invalid, it was not possible to connect to Astra.\n"); 
                    }
                }
            }
           
            showConfiguration();
            System.out.println("");
            
            if (askForConfirmation(scanner, "Do you want it to be your default organization")) {
                    AstraRcParser.save(AstraRcParser.ASTRARC_DEFAULT, 
                            AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
            }
            
            System.out.println("");
            showConfiguration();
            
            System.out.println("\nTo change default organization use: astra set-default-org <orgName>");
        }
    }
    
    /**
     * This behaviour will happen again and again.
     * 
     * @param message
     *      question asked
     * @return
     *      response of user
     */
    private boolean askForConfirmation(Scanner scanner, String message) {
        String response = null;
        while (!"y".equalsIgnoreCase(response) && !"n".equalsIgnoreCase(response)) {
                 Out.print(message + " (y/n) ? ", TextColor.CYAN);
                 response = scanner.nextLine();
        }
        return "y".equalsIgnoreCase(response);
    }
    
    /**
     * Display configuration in the shell
     */
    private void showConfiguration() {
        Map<String, Map<String, String>> sections = AstraRcParser.load().getSections();
        List<String> orgs = listOrganizations(sections);
        System.out.println("There are now " + orgs.size() + " organization(s) in your configuration.");
        int idx = 1;
        for (String org : orgs) {
            System.out.println(idx + ": " + org);
            idx++;
        }
    }
    
    /**
     * Build List as expected on screen.
     *
     * @param sections
     *      section in AstraRc.
     * @return
     *      organization list
     */
    private List<String> listOrganizations(Map<String, Map<String, String>> sections) {
        List<String> returnedList = new ArrayList<>();
        Optional<String> defaultOrg = findDefaultOrganizationName(sections);
        for (Entry<String, Map<String, String>> section : sections.entrySet()) {
            if (AstraRcParser.ASTRARC_DEFAULT.equalsIgnoreCase(section.getKey()) &&  defaultOrg.isPresent()) {
                returnedList.add(AstraRcParser.ASTRARC_DEFAULT + " (" + defaultOrg.get() + ")");
            } else {
                returnedList.add(section.getKey());
            }
        }
        return returnedList;
    }
    
    /**
     * Find the default org name in the configuration file.
     * 
     * @param sections
     *      list of sections
     * @return
     */
    private Optional<String> findDefaultOrganizationName(Map<String, Map<String, String>> sections) {
        String defaultOrgName = null;
        if (sections.containsKey(AstraRcParser.ASTRARC_DEFAULT)) {
            String defaultToken = sections
                    .get(AstraRcParser.ASTRARC_DEFAULT)
                    .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            if (defaultToken !=null) {
                for (String sectionName : sections.keySet()) {
                    if (!sectionName.equals(AstraRcParser.ASTRARC_DEFAULT)) {
                       if (defaultToken.equalsIgnoreCase(
                               sections.get(sectionName).get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN))) {
                           defaultOrgName = sectionName;
                       }
                    }
                }
            }
        }
        return Optional.ofNullable(defaultOrgName);
    }

}
