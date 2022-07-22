package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Tests commands relative to config.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class SetupCommandsTest extends AbstractAstraCliTest {
    
    @Test
    public void should_display_config()  throws Exception {
        astraCli("config", "list");
    }
    
    @Test
    public void should_asktoken()  throws Exception {
        astraCli("setup");
    }
    
    @Test
    public void should_set_default_org()  throws Exception {
        astraCli("config", "default", "celphys@gmail.com");
    }
    
    @Test
    public void should_delete_config()  throws Exception {
        astraCli("config", "delete", "saucisson");
    }
    
    @Test
    public void should_show_config()  throws Exception {
        astraCli("config", "show", "celphys@gmail.com");
    }
    
    @Test
    public void should_show_help()  throws Exception {
        astraCli("help", "config");
    }
    
    @Test
    public void should_list()  throws Exception {
        astraCli("config", "list");
    }
    
    @Test
    public void should_create()  throws Exception {
        astraCli("config", "create", "newSection", "-t", "AstraCS:TQPxCsTNLcAuPpuAcrCITtgq:5367eb28d1710199c6411a2ee20cb45d26104b8e32cd384c7e11c27ffa23d4a0");
    }

}
