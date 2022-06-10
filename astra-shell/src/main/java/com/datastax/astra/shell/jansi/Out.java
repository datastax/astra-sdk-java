package com.datastax.astra.shell.jansi;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.AnsiConsole;

/**
 * Work with terminal.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class Out {
	
    /** Current OS Value. */
    private static String OS = System.getProperty("os.name").toLowerCase();
   
    /** Every Ansi escape code begins with this PREFIX. */
    private static String PREFIX = "\033[";
    
    /** Every attribute is separated by this SEPARATOR. */
    private static String SEPARATOR = ";";
    
    /** Every Ansi escape code end with this POSTFIX. */
    private static String POSTFIX = "m";
    
	/**
	 * Hide default  constructor.
	 */
	private Out() {}
	
    /**
     * Change everything.
     *
     * @param color
     *      text color
     * @param backgroundColor
     *      background color
     * @param attribute
     *      text style
     */
    public static void setup(
            TextColor color, 
            BackgroundColor backgroundColor, 
            TextStyle attribute) {
        StringBuilder sb = new StringBuilder(PREFIX);
        if (attribute != null) {
            sb.append(attribute.getCode());
        }
        sb.append(SEPARATOR);
        if (color != null) {
            sb.append(color.getCode());
        }
        sb.append(SEPARATOR);
        if (backgroundColor != null) {
            sb.append(backgroundColor.getCode());
        }
        sb.append(POSTFIX);
        print(sb.toString());
    }
    
    public static final String string(String text, TextColor color) {
        StringBuilder sb = new StringBuilder(PREFIX);
        sb.append(SEPARATOR);
        sb.append(color.getCode());
        sb.append(SEPARATOR);
        sb.append(POSTFIX);
        sb.append(text);
        sb.append(PREFIX);
        sb.append(TextColor.RESET.getCode());
        sb.append(SEPARATOR);
        sb.append(POSTFIX);
        return sb.toString();
    }
    
    /**
     * Change text color
     *
     * @param color
     *      blue color
     */
    public static void color(TextColor color) {
        setup(color, null, null);
    }
    
    /**
     * Change only background.
     * @param back
     *      background color
     */
    public static void background(BackgroundColor back) {
        setup(null, back, null);
    }
    
    /**
     * Change text style
     *
     * @param txt
     *      text style
     */
    public static void style(TextStyle txt) {
        setup(null, null, txt);
    }
    
    /**
     * Print text ton console.
     *
     * @param text
     *      current text to be displauyed
     */
    public static void println(String text) {
        print(text + System.lineSeparator());
    }
    
    /**
     * Print text ton console.
     *
     * @param text
     *      current text to be displauyed
     */
    public static void print(String text) {
        if (OS.contains("win")) {
            AnsiConsole.sysOut().print(text);
            AnsiConsole.sysOut().flush();
        } else {
            System.out.print(text);
            System.out.flush();
        }
    }
    
    /**
     * Output.
     *
     * @param text
     *      text to display
     * @param color
     *      colot
     */
    public static void print(String text, TextColor color) {
        color(color);
        print(text);
        reset();
    }
    
    /**
     * Output.
     *
     * @param text
     *      text to display
     * @param color
     *      colot
     */
    public static void println(String text, TextColor color) {
        print(text + System.lineSeparator(), color);
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
        print("+ " + name + " = ", TextColor.CYAN);
        println(value);
    }
    
   
    /**
     * Show text in the console.
     * 
     * @param text
     *      content of the message
     * @param bold
     *      text weight
     * @param size
     *      text size
     * @param color
     *      text color
     */
    public static void print(String text,  boolean bold, Optional<Integer> size, TextColor color) {
        TextStyle att = bold ? TextStyle.BOLD : TextStyle.CLEAR;
        setup(color, null, att);
        if (size.isPresent()) {
            text = StringUtils.rightPad(text, size.get());
        }
        print(text);
    }
    
	/**
     * Change text color to white.
     */
    public static void reset() {
        color(TextColor.RESET);
    }
    
    /**
     * Log error.
     *
     * @param text
     *       text to be displayed
     */
    public static void error(String text) {
        print("[ERROR] - ", TextColor.RED);
        System.out.println(text);
    }
    
    /**
     * Log warning.
     *
     * @param text
     *       text to be displayed
     */
    public static void warning(String text) {
        print("[WARN ] - ", TextColor.YELLOW);
        System.out.println(text);
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param text
     *      text to show in success
     */
    public static void success(String text) {
        print("[ OK ] - ", TextColor.GREEN);
        System.out.println(text);
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param text
     *      text to show in success
     */
    public static void info(String text) {
        print("[INFO ] - ", TextColor.CYAN);
        System.out.println(text);
    }
    
}
