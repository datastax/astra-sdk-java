package com.datastax.astra.shell.utils;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;

import com.datastax.astra.shell.output.CsvOutput;
import com.datastax.astra.shell.output.JsonOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Work with terminal.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class LoggerShell {
	
	/**
	 * Hide default  constructor.
	 */
	private LoggerShell() {}
	
	/**
	 * Json Object Mapper. 
	 */
	public static final ObjectMapper OM = new ObjectMapper();
	
    /**
     * Output.
     *
     * @param text
     *      text to display
     * @param color
     *      colot
     */
    public static void print(String text, Ansi.Color color) {
        System.out.print(ansi().fg(color).a(text).reset());
    }
    
    /**
     * Show text in the console.
     * 
     * @param text
     *      content of the message
     * @param size
     *      text size
     * @param color
     *      text color
     */
    public static void print(String text, Ansi.Color color, int size) {
        print(StringUtils.rightPad(text, size), color);
    }
    
    /**
     * Output.
     *
     * @param text
     *      text to display
     * @param color
     *      colot
     */
    public static void println(String text, Ansi.Color color) {
        System.out.println(ansi().fg(color).a(text).reset());
    }
    
    /**
     * Print property in the shell.
     * 
     * @param name
     *      property name
     * @param value
     *      property value
     */
    public static void printProperty(String name, String value ) {
        print("+ " + name + " = ", Ansi.Color.CYAN);
        System.out.println(value);
    }
    
    /**
     * Log error.
     *
     * @param text
     *       text to be displayed
     */
    public static void error(String text) {
        System.out.println(ansi().fg(RED).a("[ERROR] - ").reset().a(text));
    }
    
    /**
     * Log warning.
     *
     * @param text
     *       text to be displayed
     */
    public static void warning(String text) {
        System.out.println(ansi().fg(YELLOW).a("[WARN ] - ").reset().a(text));
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param text
     *      text to show in success
     */
    public static void success(String text) {
        System.out.println(ansi().fg(GREEN).a("[ OK  ] - ").reset().a(text));
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param text
     *      text to show in success
     */
    public static void trace(String text) {
        System.out.println(ansi().fg(BLUE).a("[TRACE] - ").reset().a(text));
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param text
     *      text to show in success
     */
    public static void info(String text) {
        System.out.println(ansi()
                .fg(CYAN)
                .a("[INFO ] - ").reset().a(text));
    }
    
    /**
     * Log as JSON in the console.
     *
     * @param json
     *      json in the console
     */
    public static void json(JsonOutput json) {
        if (json != null) {
            try {
                String myJson = OM
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(json);
                System.out.println(myJson);
            } catch (JsonProcessingException e) {
                error("Cannot create JSON :" + e.getMessage());
            }
        }
    }
    
    /**
     * Log as CSV in the output.
     *
     * @param csv
     *      create CSV for the output
     */
    public static void csv(CsvOutput csv) {
        if (csv != null) {
            System.out.println(csv.toString());
        }
    }
    
}
