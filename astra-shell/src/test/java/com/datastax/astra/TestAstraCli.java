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
        System.out.print(" astra ");
        for (String string : args) System.out.print(string + " ");
        System.out.println();
        System.out.println("---------------------------");
        System.out.println();
        AstraCli.main(args);
    }
    
    // --  Help
    
    @Test
    public void showHelp1() throws Exception {
        astraCli("help");
    }
    
    @Test
    public void showHelp2() throws Exception {
        astraCli("help", "show");
    }
    
  
    // -- Config
    
    @Test
    public void config()  throws Exception {
        astraCli("config");
    }
    
    @Test
    public void setDefault()  throws Exception {
        astraCli("default-org", "cedrick.lunven@datastax.com");
    }
    
    @Test
    public void showConfig()  throws Exception {
        astraCli("show", "config");
    }
    
    @Test
    public void startInteractive() throws Exception {
        astraCli();
    }
    
    // -- shows
    
    @Test
    public void showDbs()  throws Exception {
        astraCli("show", "dbs");
    }
    
    @Test
    public void showRoles()  throws Exception {
        astraCli("show", "roles");
    }
    
    @Test
    public void showRole() throws Exception {
        astraCli("show", "role", "dde8a0e9-f4ae-4b42-b642-9f257436c8da");
    }
    
    @Test
    public void showUsers()  throws Exception {
        astraCli("show", "users");
    }
    
    @Test
    public void showUser()  throws Exception {
        astraCli("show", "user", "cedrick.lunven@datastax.com");
    }
    
    @Test
    public void createDb()  throws Exception {
        astraCli("create", "db", "-n","shell_tests", "-r", "us-east1", "-ks", "ks1");
    }
    
}
