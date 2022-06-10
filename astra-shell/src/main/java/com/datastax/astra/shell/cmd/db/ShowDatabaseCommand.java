package com.datastax.astra.shell.cmd.db;

import java.util.ArrayList;
import java.util.List;

import com.datastax.astra.shell.cmd.BaseCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Display information relative to a db.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = DatabaseCommandUtils.DB, 
    description = "Show details of a database")
public class ShowDatabaseCommand extends BaseCommand<ShowDatabaseCommand> {

    @Required
    @Arguments(title = "DbId", description = "Database name or identifier")
    public List<String> arguments = new ArrayList<>();
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        
    }

}
