package com.datastax.astra.processor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.datastax.astra.CommandProcessor;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.cmd.Argument.ArgumentBuilder;
import com.datastax.astra.processor.ShowProcessor.ShownItems;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.cmd.Arguments;
import com.datastax.astra.cmd.ShellContext;

/**
 * Change organization.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ConnectProcessor  implements CommandProcessor {

    /**
     * Error message
     */
    private static String ERROR_MESSAGE = "Invalid syntax in command 'connect': ";
    
    /** {@inheritDoc} */
    @Override
    public String getDocumentation() {
        return "Change organization providing org id or name.";
    }
    
    /** {@inheritDoc} */
    @Override
    public Arguments getArgs() { 
        return new Arguments().addArgument(new ArgumentBuilder()
                .name("Organization identifier")
                .description(getDocumentation())
                .build());
    }

    /** {@inheritDoc} */
    @Override
    public void process(String commandLine) {
        CommandLine cli = null;
        try {
            cli = new DefaultParser().parse(getOptions(), commandLine.split(" "));
            
            if (cli.getArgList().size() < 2) {
                Out.error(ERROR_MESSAGE + " an argument is expected");
                this.printHelp(commandLine);
            }
            
            Map<String, String > section = AstraRc.load().getSections().get(cli.getArgs()[1]);
            if (section == null) {
                Out.error("Organization '" + cli.getArgs()[1] + "' has not been found.\n");
                Out.println("Please pick a name from this list:");
                Out.println(AstraRc.load().getSections().keySet().toString(), TextColor.CYAN);
            } else {
                String token = section.get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
                ShellContext ctx = ShellContext.getInstance();
                ctx.init(token);
                ctx.validate();
                ctx.saveConfiguration();
            }
            
        } catch (ParseException e) {
            Out.error(ERROR_MESSAGE + e.getMessage());
        } catch (IllegalArgumentException e) {
            Out.error(ERROR_MESSAGE + "'" + cli.getArgs()[1] + "' is not a valid argument.");
            Out.print("\nPlease use ");
            Out.println(Arrays.stream(ShownItems.values())
                    .map(ShownItems::name)
                    .collect(Collectors.toList()).toString(), TextColor.CYAN);
        }
    }

}
