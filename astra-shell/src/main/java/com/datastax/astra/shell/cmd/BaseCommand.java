package com.datastax.astra.shell.cmd;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.output.CsvOutput;
import com.datastax.astra.shell.output.JsonOutput;
import com.datastax.astra.shell.output.OutputFormat;
import com.datastax.astra.shell.utils.LoggerShell;
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
    
    // --- Flags ---
    
    /** 
     * Each command can have a verbose mode. 
     **/
    @Option(name = { "--debug" }, description = "Enter Debug mode")
    protected boolean debug = false;
    
    /**
     * No log but provide output as a JSON
     */
    @Option(name = { "-f", "--format" }, 
            title = "FORMAT",
            description = "Output format, valid values are: human,json,csv")
    protected OutputFormat format = OutputFormat.human;
    
    /**
     * Exit program with error.
     *
     * @param code
     *      error code
     * @param msg
     *      error message
     */
    public void outputError(ExitCode code, String msg) {
        switch(format) {
            case json:
                LoggerShell.json(new JsonOutput(code, code.name() + ": " + msg));
            break;
            case csv:
                LoggerShell.csv(new CsvOutput(code,  code.name() + ": " + msg));
            break;
            case human:
            default:
                LoggerShell.error( code.name() + ": " + msg);
            break;
        }
    }
    
    /**
     * Exit program with error.
     *
     * @param msg
     *      return message
     */
    public void outputData(String label, String data) {
        switch(format) {
            case json:
                LoggerShell.json(new JsonOutput(ExitCode.SUCCESS, label, data));
            break;
            case csv:
                Map<String, String> m = new HashMap<>();
                m.put(label, data);
                LoggerShell.csv(new CsvOutput(Arrays.asList(label), Arrays.asList(m)));
            break;
            case human:
            default:
               System.out.println(data);
            break;
        }
    }
    
    /**
     * Exit program with error.
     *
     * @param msg
     *      return message
     */
    public void outputSuccess(String msg) {
        switch(format) {
            case json:
                LoggerShell.json(new JsonOutput(ExitCode.SUCCESS, msg));
            break;
            case csv:
                LoggerShell.csv(new CsvOutput(ExitCode.SUCCESS, msg));
            break;
            case human:
            default:
                LoggerShell.success(msg);
            break;
        }
    }

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
    public boolean isDebug() {
        return debug;
    }
    

}
