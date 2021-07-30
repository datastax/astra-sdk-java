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

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * TEST Loading databases metadata in .astrarc 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T00_AstraRc_IntegrationTest extends AbstractAstraIntegrationTest {
    
//    @Test
//    @Order(1)
//    public void should_create_astraRc_File() {
//        // You need to have en var ASTRA_DB_APPLICATION_TOKEN
//        
//        printYellow("Create file ");
//        // Given
//        new File(System.getProperty("user.home") + "/.astrarc").delete();
//        Assert.assertFalse(new File(System.getProperty("user.home") + "/.astrarc").exists());
//        printOK("File Deleted if exist");
//        // When
//        AstraRc.create(client.apiDevopsDatabases());
//        printOK("File Created");
//        // Then
//        Assert.assertTrue(new File(System.getProperty("user.home") + "/.astrarc").exists());
//        // Then we should be able to load the file
//        AstraRc.load().print();
//        printOK("Loaded ");
//    }

}
