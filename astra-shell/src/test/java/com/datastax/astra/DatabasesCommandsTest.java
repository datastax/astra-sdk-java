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
        astraCli("show", "dbs");
    }

    @Test
    public void createDb()  throws Exception {
        astraCli("create", "db", "-n","shell_tests", "-r", "us-east1", "-ks", "ks1");
    }
    
}
