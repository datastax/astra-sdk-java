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

package com.dtsx.astra.sdk.db.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent a database snapshot.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseSnapshot implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 8242671294103939312L;

    /** unique identifier for the snapshot. */
    private String id;
    
    /** snapshot time. */
    private String time;

    /**
     * Default constructor.
     */
    public DatabaseSnapshot() {}

    /**
     * Getter accessor for attribute 'id'.
     *
     * @return
     *       current value of 'id'
     */
    public String getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'id'.
     * @param id
     * 		new value for 'id '
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'time'.
     *
     * @return
     *       current value of 'time'
     */
    public String getTime() {
        return time;
    }

    /**
     * Setter accessor for attribute 'time'.
     * @param time
     * 		new value for 'time '
     */
    public void setTime(String time) {
        this.time = time;
    }

}
