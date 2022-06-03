package com.datastax.astra.shell.cmd.db;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

@Command(name = "orgs", description = "Display the list organizations registered in config file")
public class ShowOrganizationsCommand implements Runnable {

    /** {@inheritDoc} */
    @Override
    public void run() {
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Organization Name");
        sht.getColumnTitlesNames().add("Token");
        sht.getColumnSize().put("Organization Name", 20);
        sht.getColumnSize().put("Token", 37);
        AstraRc arc = AstraRc.load();
        for (String org : arc.getSections().keySet()) {
            Map <String, String> rf = new HashMap<>();
            rf.put("Organization Name", org);
            rf.put("Token",arc.getSections().get(org).get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN));
            sht.getCellValues().add(rf);
        }
        sht.show();
    }

}
