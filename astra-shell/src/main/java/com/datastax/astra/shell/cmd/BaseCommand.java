package com.datastax.astra.shell.cmd;

import com.datastax.astra.shell.output.OutputFormat;
import com.github.rvesse.airline.annotations.Option;

/**
 * Options, parameters and treatments that you want to apply on all commands.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class BaseCommand implements Runnable {
    
    /** Command constants. */
    public static final String CREATE     = "create";
    
    /** Command constants. */
    public static final String DELETE     = "delete";
    
    /** Command constants. */
    public static final String SHOW       = "show";
    
    /** Command constants. */
    public static final String LIST       = "list";
    
    /** Command constants. */
    public static final String USE       = "use";
    
    // --- Flags ---
    
    /** 
     * Each command can have a verbose mode. 
     **/
    @Option(name = { "--verbose" }, description = "Enter Debug mode")
    protected boolean verbose = false;
    
    /** 
     * Each command can have a verbose mode. 
     **/
    @Option(name = { "--no-color" }, description = "Remove all colors in output")
    protected boolean noColor = false;
    
    /**
     * No log but provide output as a JSON
     */
    @Option(name = { "-f", "--format" }, 
            title = "FORMAT",
            description = "Output format, valid values are: human,json,csv")
    protected OutputFormat format = OutputFormat.human;
    
    /**
     * Getter accessor for attribute 'format'.
     *
     * @return
     *       current value of 'format'
     */
    public OutputFormat getFormat() {
        return format;
    }

    /**
     * Getter accessor for attribute 'debug'.
     *
     * @return
     *       current value of 'debug'
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Getter accessor for attribute 'noColor'.
     *
     * @return
     *       current value of 'noColor'
     */
    public boolean isNoColor() {
        return noColor;
    }
    

}
