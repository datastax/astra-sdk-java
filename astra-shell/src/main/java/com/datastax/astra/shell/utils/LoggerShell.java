package com.datastax.astra.shell.utils;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;

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
        System.out.println(ansi().fg(color).a("text").reset());
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
        System.out.println(ansi().fg(YELLOW).a("[ OK  ] - ").reset().a(text));
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
    
}
