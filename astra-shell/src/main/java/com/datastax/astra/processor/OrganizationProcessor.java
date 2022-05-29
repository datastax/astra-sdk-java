package com.datastax.astra.processor;

import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.cmd.ShellCommand;
import com.datastax.astra.cmd.ShellContext;

/**
 * Processor for commands relative to organizations.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class OrganizationProcessor {
    
    /**
     * Hide constructor.
     */
    private OrganizationProcessor() {}
    
    /**
     * Parsing of organization commands.
     * 
     * @param oc
     *      command parsed
     * @param args
     *      argument
     */
    public static void execute(ShellContext ctx, ShellCommand cmd, String[] args) {
       
            /* Parsing command line
            OrganizationCommands oCmd = OrganizationCommands.valueOf(cmd.getCmd());
            
            // Validate options (if any)
            CommandLine cli = null;
            try {
                cli = new DefaultParser().parse(oCmd.getOptions(), cmd.getArgs());
                
                // Validate Arguments (if any)
                if (!oCmd.getArgs().getArguments().isEmpty()) {
                    int requiredParams = 0;
                    for (Argument arg : oCmd.getArgs().getArguments()) {
                        if (arg.isRequired()) requiredParams++;
                    }
                    if (cli.getArgList().size() != requiredParams) {
                        throw new ParseException("Invalid number of arguments for " + oCmd.getCmd());
                    }
                }       
                
            } catch (ParseException e) {
                Out.print(e.getMessage(), TextColor.RED);
                //oCmd.printHelp();
            }
            
            switch (oCmd) {
                case q:
                case exit:
                case quit:
                    Out.print("\n Bye.", TextColor.YELLOW);
                    //AstraShell.exit(SUCCESS);
                break;
                case conf:
                    ShellPrinter.printConfig();
                break;
                case info:
                    printConfig(ctx);
                break;
                case org:
                    changeOrg(ctx, cmd);
                break;
                
        } catch(IllegalArgumentException iaex) {
            Out.print("Unknown command '" + cmd.getCmd() + "'.\n", TextColor.RED);
            
            System.out.print("Valid commands are ");
            Out.print(
            Arrays.stream(OrganizationCommands.values())
                  .map(OrganizationCommands::name)
                  .collect(Collectors.toList()).toString(), TextColor.CYAN);
            Out.print("\nUse");
            Out.print("help <cmd>", TextColor.CYAN);
            System.out.println("to get information on a command: help ls");
            
            
        }
    }
    
    public static void changeOrg(ShellContext ctx, ShellCommand cmd) {
        String newOrg = cmd.getArgs()[0];
        // Is the new org a valid one ?
        Map<String, Map<String,String>> sections =  AstraRc.load().getSections();
        if (!sections.keySet().contains(newOrg)) {
            Out.error("Organization " + newOrg + " is not in the configuration");
            Out.error("Please add it or pick one in " + sections.keySet());
        } else if (newOrg.equalsIgnoreCase(ctx.getOrganization().getName())) {
           
        } else {
            //ctx = new ShellContext(sections.get(newOrg).get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN));
          
        }*/
    }
    
    /**
     * Print information relative to the current context.
     *
     * @param ctx
     *         current context
     */
    public static void printConfig(ShellContext ctx) {
        Out.print("\n[Organization Information]\n", TextColor.MAGENTA);
        System.out.print(" + Organization Name    : ");
        Out.print(ctx.getOrganization().getName(), TextColor.CYAN);
        System.out.print("\n + Organization ID      : ");
        Out.print(ctx.getOrganization().getId(), TextColor.CYAN);
        System.out.print("\n + Authentication Token : ");
        Out.print(ctx.getToken(), TextColor.CYAN);
        System.out.println("\n");
    }
    
}
