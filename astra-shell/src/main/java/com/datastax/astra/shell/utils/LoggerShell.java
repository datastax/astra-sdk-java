package com.datastax.astra.shell.utils;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCliCommand;
import com.datastax.astra.shell.cmd.BaseShellCommand;

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
     * Log in the console only if verbose is enabled.
     *
     * @return
     *      if verbose
     */
    private static boolean isVerbose() {
        BaseShellCommand sh = ShellContext.getInstance().getCurrentShellCommand();
        return (ShellContext.getInstance().getStartCommand().isVerbose() || 
               (sh != null && sh.isVerbose()));
    }
    
    /**
     * If log provided the output will go to the logfile.
     * 
     * @param text
     *      text to log
     */
    private static void logToFile(String level, String text) {
        BaseCliCommand  cli = ShellContext.getInstance().getStartCommand();
        if (cli.getLogFileWriter() != null) {
            try {
                cli.getLogFileWriter().write(new Date().toString() 
                        + " - " 
                        + StringUtils.rightPad(level, 5) 
                        + " - " + text + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Writes in log file failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param cmd
     *      current command with option to format 
     * @param text
     *      text to show in success
     */
    public static void success(String text) {
        if (isVerbose()) {
            System.out.println(ansi().fg(GREEN).a("[ OK  ] - ").reset().a(text));
        }
        logToFile("INFO", text);
    }
    
    /**
     * Log error.
     *
     * @param text
     *       text to be displayed
     */
    public static void error(String text) {
        if (isVerbose()) {
            System.out.println(ansi().fg(RED).a("[ERROR] - ").reset().a(text));
        }
        logToFile("ERROR", text);
    }
    
    /**
     * Log warning.
     *
     * @param cmd
     *      current command with option to format 
     * @param text
     *       text to be displayed
     */
    public static void warning(String text) {
        if (isVerbose()) {
            System.out.println(ansi().fg(YELLOW).a("[WARN ] - ").reset().a(text));
        }
        logToFile("WARN", text);
    }
    
    /**
     * Syntax sugar for OK.
     * 
     * @param cmd
     *      current command with option to format 
     * @param text
     *      text to show in success
     */
    public static void trace(String text) {
        if (isVerbose()) {
            System.out.println(ansi().fg(YELLOW).a("[DEBUG ] - ").reset().a(text));
        }
        logToFile("DEBUG", text);
    }
    
    /**
     * Syntax sugar for OK.
     *
     * @param cmd
     *      current command with option to format 
     * @param text
     *      text to show in success
     */
    public static void info(String text) {
        if (isVerbose()) {
            System.out.println(ansi().fg(CYAN).a("[INFO ] - ").reset().a(text));
        }
        logToFile("INFO", text);
    }
    
}
