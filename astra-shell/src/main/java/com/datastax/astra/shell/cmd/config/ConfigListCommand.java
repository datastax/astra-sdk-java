package com.datastax.astra.shell.cmd.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Show the list of available configurations.
 * 
 * astra list XXX
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
@Command(name = "list", description = "Show the list of available configurations.")
public class ConfigListCommand extends AbstractConfigCommand {
    
    /**
     * Title of the table.
     */
    private static final String COLUMN_TITLE = "Configuration Sections";
   
    /**
     * Constructor to TODO
     */
    public ConfigListCommand() {
        super();
    }
    
    /** {@inheritDoc} */
    public void run() {
        Map<String, Map<String, String>> sections = getAstraRc().getSections();
        List<String> orgs = listOrganizations(sections);
        System.out.println("There are " + orgs.size() + " section(s) in your configuration file.");
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(Ansi.Color.YELLOW);
        sht.setCellColor(Ansi.Color.WHITE);
        sht.setTableColor(Ansi.Color.CYAN);
        sht.getColumnTitlesNames().add(COLUMN_TITLE);
        sht.getColumnSize().put(COLUMN_TITLE, 40);
        for (String org : orgs) {
            Map <String, String> rf = new HashMap<>();
            rf.put(COLUMN_TITLE, org);
            sht.getCellValues().add(rf);
        }
        sht.show();
    }
    
    /**
     * Find the default org name in the configuration file.
     * 
     * @param sections
     *      list of sections
     * @return
     */
    private static Optional<String> findDefaultOrganizationName(Map<String, Map<String, String>> sections) {
        String defaultOrgName = null;
        if (sections.containsKey(AstraRc.ASTRARC_DEFAULT)) {
            String defaultToken = sections
                    .get(AstraRc.ASTRARC_DEFAULT)
                    .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            if (defaultToken !=null) {
                for (String sectionName : sections.keySet()) {
                    if (!sectionName.equals(AstraRc.ASTRARC_DEFAULT)) {
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
    
    /**
     * Build List as expected on screen.
     *
     * @param sections
     *      section in AstraRc.
     * @return
     *      organization list
     */
    private static List<String> listOrganizations(Map<String, Map<String, String>> sections) {
        List<String> returnedList = new ArrayList<>();
        Optional<String> defaultOrg = findDefaultOrganizationName(sections);
        for (Entry<String, Map<String, String>> section : sections.entrySet()) {
            if (AstraRc.ASTRARC_DEFAULT.equalsIgnoreCase(section.getKey()) &&  defaultOrg.isPresent()) {
                returnedList.add(AstraRc.ASTRARC_DEFAULT + " (" + defaultOrg.get() + ")");
            } else {
                returnedList.add(section.getKey());
            }
        }
        return returnedList;
    }
    
}
