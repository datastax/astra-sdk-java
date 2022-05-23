package com.datastax.astra.processor;

import com.datastax.astra.CommandProcessor;
import com.datastax.astra.ExitCode;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;

/**
 * Exit the program. 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ExitProcessor implements CommandProcessor {
  
    /** {@inheritDoc} */
    @Override
    public String getDocumentation() {
        return "Exit the program";
    }

    /** {@inheritDoc} */
    @Override
    public void process(String commandLine) {
        Out.print("Bye\n", TextColor.CYAN);
        System.exit(ExitCode.SUCCESS.getCode());      
    }

}
