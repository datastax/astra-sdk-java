package com.datastax.astra.shell.cmd.show;

import com.datastax.astra.shell.cmd.config.ConfigListCommand;
import com.github.rvesse.airline.annotations.Command;

@Command(name = "configs", description = "Show a list of configurations availables")
public class ShowConfigsCommand extends ConfigListCommand {}
