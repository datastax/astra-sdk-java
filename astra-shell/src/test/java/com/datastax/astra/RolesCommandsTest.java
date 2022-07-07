package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Testing CRUD for roles.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RolesCommandsTest extends AbstractAstraCliTest {


    @Test
    public void interactive()  throws Exception {
        astraCli();
    }
    
    @Test
    public void listRoles()  throws Exception {
        astraCli("role", "list");
    }
    
    @Test
    public void showRole() throws Exception {
        astraCli("role", "show", "dde8a0e9-f4ae-4b42-b642-9f257436c8da");
    }
    
}
