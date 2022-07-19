package com.datastax.astra.shell.utils;

import static org.fusesource.jansi.Ansi.ansi;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.output.CsvOutput;
import com.datastax.astra.shell.output.JsonOutput;
import com.datastax.astra.shell.output.OutputFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Render all component for the FF4J commands.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellPrinter {
    
	/** Default constructor. */
	private ShellPrinter() {}
	
	/** Start Banner. */
    public static void banner() {
        System.out.println();
        System.out.print("  █████╗ ███████╗████████╗██████╗  █████╗   ");
        System.out.println("  ███████╗██╗  ██╗███████╗██╗     ██╗     ");
        System.out.print(" ██╔══██╗██╔════╝╚══██╔══╝██╔══██╗██╔══██╗  ");
        System.out.println("  ██╔════╝██║  ██║██╔════╝██║     ██║  ");
        System.out.print(" ███████║███████╗   ██║   ██████╔╝███████║  ");
        System.out.println("  ███████╗███████║█████╗  ██║     ██║   ");
        System.out.print(" ██╔══██║╚════██║   ██║   ██╔══██╗██╔══██║  ");
        System.out.println("  ╚════██║██╔══██║██╔══╝  ██║     ██║");
        System.out.print(" ██║  ██║███████║   ██║   ██║  ██║██║  ██║  ");
        System.out.println("  ███████║██║  ██║███████╗███████╗███████╗");
        System.out.print(" ╚═╝  ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ");
        System.out.println("  ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝");
        System.out.println("");
        System.out.print(" Version: " + version() + "\n");
    }
    
    /**
     * Show version.
     *
     * @return
     *      return version
     */
    public static String version() {
        String versionPackage = ShellPrinter.class
                .getPackage()
                .getImplementationVersion();
        if (versionPackage == null) {
            versionPackage = "Development";
        }
        return versionPackage;
    }
    
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
     * Log as JSON in the console.
     *
     * @param json
     *      json in the console
     */
    public static void printJson(JsonOutput json) {
        if (json != null) {
            try {
                String myJson = OM
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(json);
                System.out.println(myJson);
            } catch (JsonProcessingException e) {
                LoggerShell.error("Cannot create JSON :" + e.getMessage());
            }
        }
    }
    
    /**
     * Log as CSV in the output.
     *
     * @param csv
     *      create CSV for the output
     */
    public static void printCsv(CsvOutput csv) {
        if (csv != null) {
            System.out.println(csv.toString());
        }
    }
    
    /**
     * Show the table in console.
     * 
     * @param sht
     *      table
     * @param fmt
     *      format
     */
    public static void printShellTable(ShellTable sht, OutputFormat fmt) {
        switch(fmt) {
            case json:
                sht.showJson("db list");
            break;
            case csv: 
                sht.showCsv(); 
            break;
            case human:
            default:
                sht.show();
            break;
        }
    }
    
    /**
     * Show object as Json in console.
     *
     * @param obj
     *      object
     * @param color
     *      color
     */
    public static final void printObjectAsJson(Object obj, Ansi.Color color) {
        try {
            println(OM
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(obj), color);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize object as JSON", e);
        }
    }
    
	/**
	 * Will print Promt based on the current state.
	 */
	public static void prompt() {
	    System.out.println("");
	    ShellContext ctx = ShellContext.getInstance();
	    if (ctx.getOrganization() != null) {
	        print(ctx.getOrganization().getName(), Ansi.Color.GREEN);
	    }
	    if (ctx.getDatabase() != null) {
	        print(" > ", Ansi.Color.GREEN);
            print(ctx.getDatabase().getInfo().getName(), Ansi.Color.YELLOW);
            print(" > ", Ansi.Color.GREEN);
            print(ctx.getDatabaseRegion() + " ", Ansi.Color.YELLOW);
        }
	    print("> ", Ansi.Color.GREEN);
	}
	
}
