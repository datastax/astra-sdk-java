package com.datastax.astra;

import static com.datastax.astra.ExitCode.INVALID_PARAMETER;
import static com.datastax.astra.ExitCode.SUCCESS;
import static com.datastax.astra.cmd.ShellPrinter.printBanner;
import static com.datastax.astra.cmd.ShellPrinter.printPrompt;

import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.cmd.ShellContext;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;

/**
 * Astra Shell.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraShell {
    
    /**
     * Main Program.
     *
     * @param args
     *           start options for the shell
     * @throws Exception
     *           error during parsing or interpreting command
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = null;
        try {
            
            printBanner();

            /**
             * Parsing and initializaion
             */
            String token = parseStartCommand(args);
            ShellContext ctx = ShellContext.getInstance();
            ctx.init(token);
            ctx.validate();
            ctx.saveConfiguration();
            
            /**
             * Main Command Loop 
             */
            scanner = new Scanner(System.in);
            while(true) {
                printPrompt(ctx);
                String readline = scanner.nextLine();
                parseUserCommand(readline).ifPresent(cmd -> 
                    cmd.getProcessor().process(readline));
            }
            
        } finally {
            if (scanner != null ) scanner.close();
        }
    }
    
    /**
     * Parse user input to get command asked.
     * 
     * @param readline
     *      user command
     * @return
     *      command keyword
     */
    private static Optional<CommandTypes> parseUserCommand(String readline) {
        CommandTypes command = null;
        String keyword = null;
        try {
            if (null!= readline && readline.length() > 0) {
                if (readline.endsWith(";")) {
                    readline = readline.substring(0, readline.length()-1);
                }
                // Get command, no options validation
                String[] readLineChunks = readline.split(" ");
                keyword = readLineChunks[0];
                // Using interrogation is common to get help
                if ("?".equals(keyword)) {
                    keyword = CommandTypes.help.name();
                }
                command = CommandTypes.valueOf(keyword); 
            }
         } catch(IllegalArgumentException iaex) {
             Out.error("Unkown command '" + keyword + "': use 'help' to show command list");
         }
        return Optional.ofNullable(command);
    }
    
    /**
     * Parsing input options.
     * 
     * @param args
     *      start options of the program
     * @return
     *      token if present
     */
    private static String parseStartCommand(String[] args) {
        Option optionDebug = Option
                .builder("d").longOpt("debug")
                .desc("Enable verbose messages for debugging.")
                .build();
        Option optionHelp = Option
                .builder("h").longOpt("help")
                .desc("Display help information")
                .build();
        Option optionVersion = Option
                .builder("v").longOpt("version")
                .desc("Display version")
                .build();
        Option optionOrg = Option
                .builder("org").longOpt("organization")
                .argName("orgName")
                .hasArg()
                .desc("The organization name. You must connect with token first for the org to be saved in config")
                .build();
        Option optionToken = Option
                .builder("t").longOpt("token")
                .argName("astraToken")
                .hasArg().desc("The astra token (AstraCS:..)")
                .build();
        Options startOptions = new Options()
                .addOption(optionHelp)
                .addOption(optionToken)
                .addOption(optionOrg)
                .addOption(optionDebug)
                .addOption(optionVersion);        
        
        String token = null;
        try {
            
            // Parsing with intput options
            CommandLine cli = new DefaultParser().parse(startOptions, args);
            
            // --v or --version = Show version number and quit
            if (cli.hasOption(optionVersion)) {
                SUCCESS.exit();
            }
    
            // --help or -h = Show help and quit
            if (cli.hasOption(optionHelp)) {
                new HelpFormatter().printHelp("astra", startOptions);
                SUCCESS.exit();
            }
    
            // -org or --organization, connect to specified organization (if exist)
            if (cli.hasOption(optionOrg)) {
                String orgName = cli.getOptionValue(optionOrg);
                AstraRc config = AstraRc.load();
                if(!config.getSections().containsKey(orgName)) {
                    Out.error("Organization '" + orgName + "' is not in the configuration file\n");
                    Out.print("Available Organizations:");
                    Out.print(config.getSections().keySet().toString(), TextColor.CYAN);
                    INVALID_PARAMETER.exit();
                } else {
                    // Org found, loading token
                    token = config.getSections().get(orgName).get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
                }
            }
            
            // -t or --token, conect with the token
            if (cli.hasOption(optionToken)) {
                token = cli.getOptionValue(optionToken);
            }
        
        } catch (ParseException e) {
            Out.error(e.getMessage());
            INVALID_PARAMETER.exit();
        }
        return token;
    }
    
}
