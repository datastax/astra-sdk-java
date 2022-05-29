package com.datastax.astra.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Arguments of a command line.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Arguments implements Serializable {
    
    /** Serial.*/
    private static final long serialVersionUID = 1L;
    
    /** the command argument list. */
    private List<Argument> arguments = new ArrayList<>();

    /**
     * Default Constructor.
     */
    public Arguments() {
    }
    
    /**
     * Add an argument in the command.
     *
     * @param arg
     *      argument of the command
     * @return
     *      the arguments lists
     */
    public Arguments addArgument(Argument arg) {
        arguments.add(arg);
        return this;
    }

    /**
     * Getter accessor for attribute 'arguments'.
     *
     * @return
     *       current value of 'arguments'
     */
    public List<Argument> getArguments() {
        return arguments;
    }
  
}
