package com.datastax.astra.shell.cmd.show;

import com.datastax.astra.shell.cmd.config.ConfigShowCommand;
import com.github.rvesse.airline.annotations.Command;

/**
 * Show details of a configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "config", description = "Show details for a configuration.")
public class ShowConfigCommand extends ConfigShowCommand {}
