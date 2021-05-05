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

package com.datastax.stargate.sdk.doc;

/**
 * Wrapper for an document retrieved from ASTRA caring a unique identifier.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <BEAN>
 *      target bean to store in ASTRA / STARGATE
 */
public class ApiDocument<BEAN> {
    
    /** Unique identifier. */
    private final String documentId;
    
    /** Marshalled Object. */
    private final BEAN document;
    
    /**
     * Constructor with Params
     */
    public ApiDocument(String docId, BEAN doc) {
        this.documentId = docId;
        this.document = doc;
    }

    /**
     * Getter accessor for attribute 'documentId'.
     *
     * @return
     *       current value of 'documentId'
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Getter accessor for attribute 'document'.
     *
     * @return
     *       current value of 'document'
     */
    public BEAN getDocument() {
        return document;
    }

}
