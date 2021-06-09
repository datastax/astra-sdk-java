/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.utils.AstraRc;

/**
 * TEST Loading databases metadata in .astrarc 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T06_AstraRc_IntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T06_AstraRc_IntegrationTest]" + ANSI_RESET);
    }
    
    @Test
    @Order(1)
    public void should_create_astraRc_File() {
        System.out.println(ANSI_YELLOW + "\n#01 Create file " + ANSI_RESET);
        // Given
        new File(System.getProperty("user.home") + "/.astrarc").delete();
        Assert.assertFalse(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // When
        AstraRc.create(client.apiDevopsDatabases());
        // Then
        Assert.assertTrue(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // Then we should be able to load the file
        AstraRc.load();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Loaded");
    }
    
    @Test
    @Order(2)
    public void should_init_AstraClient_from_File() {
        System.out.println(ANSI_YELLOW + "\n#02 Use file " + ANSI_RESET);
        AstraClient astraClient = AstraClient.builder().build();
        Assertions.assertNotNull(astraClient);
        // Can query with CQL !!
        String dataCenterName = astraClient.cqlSession()
                .execute("select data_center from system.local")
                .one().getString("data_center");
        Assertions.assertNotNull(dataCenterName);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - cql sucess with dc " + dataCenterName);
    }
    
    
    @AfterAll
    public static void close() {
        new File(System.getProperty("user.home") + "/.astrarc").delete();
    }
    

}
