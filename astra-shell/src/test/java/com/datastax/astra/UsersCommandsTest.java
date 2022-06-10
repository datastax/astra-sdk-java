package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Commands relative to users.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class UsersCommandsTest extends AbstractAstraCliTest {

    @Test
    public void showUsers()  throws Exception {
        astraCli("show", "users");
    }
    
    @Test
    public void showUser()  throws Exception {
        astraCli("show", "user", "cedrick.lunven@datastax.com");
    }
    
}
