package com.datastax.astra;

import com.datastax.astra.shell.AstraCli;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;

/**
 * Super class for tests.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class AbstractAstraCliTest {

    /**
     * Helper to execute a command
     * 
     * @param args
     *      args as providede in the command line
     */
    protected void astraCli(String... args) {
        System.out.println("----- TESTED COMMAND ------");
        Out.print(" astra ", TextColor.GREEN);
        for (String string : args) Out.print(string + " ", TextColor.GREEN);
        System.out.println();
        System.out.println("---------------------------");
        System.out.println();
        AstraCli.main(args);
    }
    
}
