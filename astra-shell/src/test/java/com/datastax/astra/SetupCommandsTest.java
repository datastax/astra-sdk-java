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
        astraCli("show", "config");
    }
    
    @Test
    public void should_asktoken()  throws Exception {
        astraCli("setup");
    }
    
    @Test
    public void should_set_default_org()  throws Exception {
        astraCli("default-org", "cedrick.lunven@datastax.com");
    }

}
