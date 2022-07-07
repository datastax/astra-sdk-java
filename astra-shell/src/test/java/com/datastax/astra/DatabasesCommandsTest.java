package com.datastax.astra;

import org.junit.jupiter.api.Test;

/**
 * Testing CRUD for databases.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabasesCommandsTest extends AbstractAstraCliTest {
    
    @Test
    public void showDbs()  throws Exception {
        astraCli("db", "list");
    }
    
    @Test
    public void createDb()  throws Exception {
        astraCli("db", "create", "test", "-r", "eu-central-1", "-ks", "ks1");
    }

    @Test
    public void deleteDb()  throws Exception {
        astraCli("db", "delete","sky_session1");
    }
    
    
}
