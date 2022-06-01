package com.datastax.astra.shell.cmd.db;

import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;

public class CreateDatabaseCommand {

    @Required
    @Option(name = { "-n", "--name" }, 
            title = "Database name", 
            arity = 1, 
            description = "A required option")
    private String databaseNamw;
}
