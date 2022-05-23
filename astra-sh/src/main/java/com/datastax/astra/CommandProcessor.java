package com.datastax.astra;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.cmd.Argument;
import com.datastax.astra.cmd.Arguments;

/**
 * Generic commands model independent of scope.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public interface CommandProcessor {
    
    /**
     * Show the short documentation message.
     * 
     * @return
     *      documentation line.
     */
    String getDocumentation();
    
    /**
     * If validation success proceed with the command.
     *  
     * @param ctx
     *      current context for the shell
     * @param commandLine
     *       current command line
     */
    void process(String commandLine);
     
    /**
     * List available arguments.
     *
     * @return
     *      options.
     */
    default Arguments getArgs() {
        return new Arguments();
    }
    
    /**
     * List availables options.
     *
     * @return
     *      options.
     */
    default Options getOptions() {
        return new Options();
    }
    
    /**
     * Default command and description (nor arguments)
     * 
     * @param keyword
     *      current keyword (we can have multiple per command)
     */
    default void printHelpShort(String keyword) {
        Out.print(StringUtils.rightPad(keyword, 12), TextColor.CYAN);
        Out.println(getDocumentation());
    }
    
    /**
     * Display help for the command.
     */
    default void printHelp(String cmd) {
        System.out.println("\n\nusage: " + cmd + " [arguments] [<option(s)>] ");
        if (!getArgs().getArguments().isEmpty()) {
            System.out.println("\nArguments:");
            for (Argument arg : getArgs().getArguments()) {
                Out.print(" " + StringUtils.rightPad(arg.getName(), 10), 
                        TextColor.CYAN);
                Out.print(StringUtils.rightPad(arg.getFixedValues().toString(), 30), 
                        TextColor.CYAN);
                System.out.println(" " + StringUtils.rightPad(arg.getDescription(), 30));
            }
        }
       
        if (!getOptions().getOptions().isEmpty()) {
            System.out.println("\nOptions:");
            for (Option opt : getOptions().getOptions()) {
                Out.print(StringUtils.rightPad("-" + opt.getOpt() + ",--" + opt.getLongOpt(), 20), 
                        TextColor.YELLOW);
                System.out.println(StringUtils.rightPad(opt.getDescription(), 30));
            }
        }
        System.out.println(getDocumentation());
    }

}
