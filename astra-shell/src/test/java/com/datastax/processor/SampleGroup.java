package com.datastax.processor;

import java.io.IOException;

import com.datastax.astra.shell.cmd.ConnectCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.db.ShowOrganizationsCommand;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.help.cli.CliCommandGroupUsageGenerator;
import com.github.rvesse.airline.model.CommandGroupMetadata;

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
public class SampleGroup {
    
    public static void main(String[] args) {
        com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<>(SampleGroup.class);
        cli.parse(args).run();
    }
    
    public static void generateHelp() throws IOException {
        com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<Runnable>(SampleGroup.class);
        
        CommandGroupUsageGenerator<Runnable> helpGenerator = new CliCommandGroupUsageGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
