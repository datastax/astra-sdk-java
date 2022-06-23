package com.datastax.astra.shell.utils;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.shell.ShellContext;
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
        System.out.print(" Version: ");
        String versionPackage = ShellPrinter.class
                .getPackage()
                .getImplementationVersion();
        if (versionPackage == null) {
            versionPackage = "Development";
        }
        System.out.println("\n");
    }
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
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
            LoggerShell.println(MAPPER
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
	        LoggerShell.print(ctx.getOrganization().getName(), Ansi.Color.GREEN);
	    }
	    if (ctx.getDatabase() != null) {
	        LoggerShell.print(" > ", Ansi.Color.GREEN);
            LoggerShell.print(ctx.getDatabase().getInfo().getName(), Ansi.Color.YELLOW);
            LoggerShell.print(" > ", Ansi.Color.GREEN);
            LoggerShell.print(ctx.getDatabaseRegion() + " ", Ansi.Color.YELLOW);
        }
	    LoggerShell.print("> ", Ansi.Color.GREEN);
	}
	
}
