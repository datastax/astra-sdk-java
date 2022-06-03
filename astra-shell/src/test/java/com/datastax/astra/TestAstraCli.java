package com.datastax.astra;

import org.junit.jupiter.api.Test;

import com.datastax.astra.shell.AstraCli;

/**
 * Test Stateless CLI.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TestAstraCli {
    
    private void astraCli(String... args) {
        System.out.println("---------------------------");
        System.out.print(" Command: astra ");
        for (String string : args) System.out.print(string + " ");
        System.out.println();
        System.out.println("---------------------------");
        AstraCli.main(args);
    }
    
    @Test
    public void showHelp1() throws Exception {
        astraCli("help");
    }
    
    @Test
    public void showHelp2() throws Exception {
        astraCli("-h");
    }
    
    @Test
    public void showDatabasesCli() throws Exception {
        astraCli( "show-dbs", "-org", "cedrick.lunven@datastax.com");
    }
    
    @Test
    public void startInteractive() throws Exception {
        astraCli();
    }
    
    
    
}
