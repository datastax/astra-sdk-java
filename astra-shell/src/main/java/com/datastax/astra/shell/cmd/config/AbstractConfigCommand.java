package com.datastax.astra.shell.cmd.config;

import com.datastax.astra.sdk.utils.AstraRc;
import com.github.rvesse.airline.annotations.Option;

/**
 * Shared properties with multiple config command (avoiding duplication).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class AbstractConfigCommand implements Runnable {
    
    /** 
     * Overriding default config file 
     **/
    @Option(
      name = { "-f", "--configFile" }, 
      title = "fileName", 
      description = "Custom configuration file")
    protected String configFileName;
    
    /**
     * Configuration loaded
     */
    protected AstraRc astraRc;
    
    /**
     * Getter for confifguration AstraRC.
     *
     * @return
     *      configuration in AstraRc
     */
    protected AstraRc getAstraRc() {
        if (astraRc == null) {
            if (configFileName != null) {
                astraRc = new AstraRc(configFileName);
            } else {
                astraRc = new AstraRc();
            }
        }
        return astraRc;
    }

}
