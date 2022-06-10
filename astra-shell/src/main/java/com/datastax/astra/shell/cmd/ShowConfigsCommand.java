package com.datastax.astra.shell.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

@Command(name = "configs", description = "Show a list of configurations availables")
public class ShowConfigsCommand implements Runnable {
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        Map<String, Map<String, String>> sections = AstraRc.load().getSections();
        List<String> orgs = listOrganizations(sections);
        System.out.println("There are " + orgs.size() + " section(s) in your configuration file.");
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Configuration Sections");
        sht.getColumnSize().put("Organization Name", 40);
        for (String org : orgs) {
            Map <String, String> rf = new HashMap<>();
            rf.put("Organization Name", org);
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
