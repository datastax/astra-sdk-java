package com.datastax.processor;

import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;

public class HelpUtils {

    public static void printMainHelp() {
        Out.println("usage: astra [ <command> ] [ <options> ]", TextColor.YELLOW);
        
        Out.println("\n Options:", TextColor.CYAN);
        System.out.println("   -i,--interactive \t\t Enter interactive mode (ignore command)");
        System.out.println("   -d,--debug \t\t\t Verbose Mode");
        System.out.println("   -h,--help \t\t\t Show this help");
        System.out.println("   -t,--token \t\t\t Authentication token");
        System.out.println("   -org,--organization <org> \t Organization as provided in ~.astrarc");
        System.out.println("   [no options] \t\t\t Load [default] organization from ~.astrarc");
        
        Out.println("\n Commands:", TextColor.CYAN);
        System.out.println("   connect \t\t\t Switch organization");
        System.out.println("   create-db \t\t\t Create a database");
        System.out.println("   create-role\t\t\t Create an role");
        System.out.println("   create-tenant\t\t\t Create a tenant");
        System.out.println("   create-token \t\t\t Create a token");
        System.out.println("   help \t\t\t\t Show help for a dedicated command");
        System.out.println("   show-dbs \t\t\t Display databases in an organization");
        System.out.println("   show-org \t\t\t Display information on current org");
        System.out.println("   show-orgs \t\t\t Display list of organization in your config");
        Out.println("\n astra help <command> for more help on a particular command", TextColor.CYAN);
    }
    
    public static void printCommandHelp() {
        Out.println("NAME:", TextColor.YELLOW);
        System.out.println("astra create-db - Create a database");
        Out.println("\nSYNOPSIS:", TextColor.YELLOW);
        System.out.println("In an Astra tenant (organization) you can have multiple databases. "
                + "Each has its unique identifier. A database also has a name but it does not need "
                + "to be unique. At creation you will have to choose a default region (where the database will live) and a default "
                + "default keyspace (to create tables). A database can have multiple regions and multiple keyspaces.");
        Out.println("\nOPTIONS:", TextColor.YELLOW);
        System.out.println("  -n,--name \t\t Database Name (required)");
        System.out.println("  -r,--region \t\t Default Region (required)");
        System.out.println("  -k,--keyspace \t\t Default keyspace (required)");
        System.out.println("  -d,--debug \t\t Verbose Mode");
        System.out.println("  -h,--help \t\t Show this help");
    }    
}
