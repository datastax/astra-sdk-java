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

package com.datastax.astra.sdk.utils;

import java.io.Serializable;

/**
 * Parser to read the CSV FiLE.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Token implements Serializable {
    
    /** Serial.*/
    private static final long serialVersionUID = -2071340043293340134L;

    /** Client identifier. */
    private final String clientId;
    
    /** Client secret. */
    private final String clientSecret;

    /** token. **/
    private final String token;
    
    /** role. */
    private final String role;
    
    /**
     * Constructor wil all fields.
     *
     * @param clientId
     * @param clientSecret
     * @param token
     * @param role
     */
    public Token(String clientId, String clientSecret, String token, String role) {
        super();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.token = token;
        this.role = role;
    }

    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Getter accessor for attribute 'clientSecret'.
     *
     * @return
     *       current value of 'clientSecret'
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'role'.
     *
     * @return
     *       current value of 'role'
     */
    public String getRole() {
        return role;
    }

}
