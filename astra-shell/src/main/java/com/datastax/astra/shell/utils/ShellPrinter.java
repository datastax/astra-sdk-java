package com.datastax.astra.shell.utils;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.BackgroundColor;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
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
        System.out.println("");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print("  █████╗ ███████╗████████╗██████╗  █████╗   ");
        Out.setup(TextColor.MAGENTA, null, null);
        System.out.println("  ███████╗██╗  ██╗███████╗██╗     ██╗     ");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██╔══██╗██╔════╝╚══██╔══╝██╔══██╗██╔══██╗  ");
        Out.setup(TextColor.BLUE, null, null);
        System.out.println("  ██╔════╝██║  ██║██╔════╝██║     ██║  ");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ███████║███████╗   ██║   ██████╔╝███████║  ");
        Out.setup(TextColor.GREEN, null, null);
        System.out.println("  ███████╗███████║█████╗  ██║     ██║   ");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██╔══██║╚════██║   ██║   ██╔══██╗██╔══██║  ");
        Out.setup(TextColor.YELLOW, null, null);
        System.out.println("  ╚════██║██╔══██║██╔══╝  ██║     ██║");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██║  ██║███████║   ██║   ██║  ██║██║  ██║  ");
        Out.setup(TextColor.RED, null, null);
        System.out.println("  ███████║██║  ██║███████╗███████╗███████╗");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ╚═╝  ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ");
        Out.setup(TextColor.RED, null, null);
        System.out.println("  ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝");
        System.out.println("");
        Out.setup(TextColor.RESET, null, null);
        System.out.print(" Version: ");
        String versionPackage = ShellPrinter.class
                .getPackage()
                .getImplementationVersion();
        if (versionPackage == null) {
            versionPackage = "Development";
        }
        Out.print(versionPackage, TextColor.GREEN);
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
    public static final void printObjectAsJson(Object obj, TextColor color) {
        try {
            Out.println(MAPPER
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(obj), color);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize object as JSON", e);
        }
    }
    
	/**
	 * Print the Datastax Devs logo in console
	 */
	public static final void printDatastaxDevs() {
	    Out.setup(TextColor.BLUE, BackgroundColor.WHITE, null);
        System.out.println("    ___      _        __ _                 ___                ");
        System.out.println("   /   \\__ _| |_ __ _/ _\\ |_ __ ___  __   /   \\_____   _____  ");
        System.out.println("  / /\\ / _` | __/ _` \\ \\| __/ _` \\ \\/ /  / /\\ / _ \\ \\ / / __\\ ");
        System.out.println(" / /_// (_| | || (_| |\\ \\ || (_| |>  <  / /_//  __/\\ V /\\__ \\ ");
        System.out.println("/___,' \\__,_|\\__\\__,_\\__/\\__\\__,_/_/\\_\\/___,' \\___| \\_/ |___/ ");
        System.out.println("                                                              ");
        Out.setup(TextColor.RESET, null, null);
        
	}
 	
	/**
	 * Will print Promt based on the current state.
	 * 
	 * [;32;mcedrick.lunven@datastax.com[;0;m[;36;m>mtg[;0;m[;35;m>eu-central-1[;0;m[;32;m>[;0;m
	 */
	public static void prompt() {
	    System.out.println("");
	    ShellContext ctx = ShellContext.getInstance();
	    if (ctx.getOrganization() != null) {
	        Out.print(ctx.getOrganization().getName(), TextColor.GREEN);
	    }
	    if (ctx.getDatabase() != null) {
	        Out.print(" > ", TextColor.GREEN);
            Out.print(ctx.getDatabase().getInfo().getName(), TextColor.YELLOW);
            Out.print(" > ", TextColor.GREEN);
            Out.print(ctx.getDatabaseRegion() + " ", TextColor.YELLOW);
        }
	    Out.print("> ", TextColor.GREEN);
	}
	
}
