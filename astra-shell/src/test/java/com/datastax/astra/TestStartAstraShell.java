package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TestStartAstraShell {
    
    public static final String TOKEN = "CHANGE_ME";
    
    @Test
    public void cmdShowHelp() throws Exception {
        AstraShell.exec("--help");
    }
    
    @Test
    public void cmdShowVersion() throws Exception {
        AstraShell.exec("--version");
    }
    
    @Test
    public void startInteractive() throws Exception {
        AstraShell.exec("-t", TOKEN);
    }
    
    

}
