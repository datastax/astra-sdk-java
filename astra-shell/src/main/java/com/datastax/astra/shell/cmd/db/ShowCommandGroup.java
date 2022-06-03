package com.datastax.astra.shell.cmd.db;

import com.datastax.astra.shell.cmd.ConnectCommand;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.help.Help;

@Cli(
        name = "cli", 
        description = "A simple CLI with several commands available in groups",
        groups = {
            @Group(
                name = "basic",
                description = "Basic commands",
                commands = { ConnectCommand.class , Help.class}
            ),
            @Group(
                name = "show",
                description = "Commands that demonstrate option inheritance",
                commands = { ShowOrganizationsCommand.class, ShowDatabasesCommand.class }
            )
        },
        commands = { Help.class }
)
public class ShowCommandGroup {

}
