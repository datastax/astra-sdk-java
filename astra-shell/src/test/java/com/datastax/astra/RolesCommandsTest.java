package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Testing CRUD for roles.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RolesCommandsTest extends AbstractAstraCliTest {

    @Test
    public void showRoles()  throws Exception {
        astraCli("show", "roles");
    }
    
    @Test
    public void showRole() throws Exception {
        astraCli("show", "role", "dde8a0e9-f4ae-4b42-b642-9f257436c8da");
    }
    
}
