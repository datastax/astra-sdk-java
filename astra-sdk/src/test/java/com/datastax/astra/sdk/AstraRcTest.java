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

import org.junit.Ignore;

/**
 * Create Astrarc to execute test locally.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Ignore
public class AstraRcTest {
    
    /*
    @Test
    @DisplayName("Create .astraRC without clientId/clientSecret")
    public void should_create_astraRc_File() {
        // Given
        new File(System.getProperty("user.home") + "/.astrarc").delete();
        Assertions.assertFalse(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // When
        AstraRc.create(AstraClient.builder().build().getToken().get());
        // Then
        Assertions.assertTrue(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // Then we should be able to load the file
        AstraRc.load().print();
    }
    */

}
