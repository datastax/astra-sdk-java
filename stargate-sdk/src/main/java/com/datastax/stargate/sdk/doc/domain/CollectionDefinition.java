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

package com.datastax.stargate.sdk.doc.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a column when working with Document API
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class CollectionDefinition implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 8579135728885849205L;

    /** unique identifier of the the collection. */
    private String name;
    
    /** status to use upgrade. */
    private boolean upgradeAvailable;
    
    /** upgrade capability like SAI_INDEX_UPGRADE. */
    private String upgradeType;
    
    /**
     * Default constructor.
     */
    public CollectionDefinition() {
        super();
    }

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter accessor for attribute 'upgradeAvailable'.
     *
     * @return
     *       current value of 'upgradeAvailable'
     */
    public boolean isUpgradeAvailable() {
        return upgradeAvailable;
    }

    /**
     * Setter accessor for attribute 'upgradeAvailable'.
     * @param upgradeAvailable
     * 		new value for 'upgradeAvailable '
     */
    public void setUpgradeAvailable(boolean upgradeAvailable) {
        this.upgradeAvailable = upgradeAvailable;
    }

    /**
     * Getter accessor for attribute 'upgradeType'.
     *
     * @return
     *       current value of 'upgradeType'
     */
    public String getUpgradeType() {
        return upgradeType;
    }

    /**
     * Setter accessor for attribute 'upgradeType'.
     * @param upgradeType
     * 		new value for 'upgradeType '
     */
    public void setUpgradeType(String upgradeType) {
        this.upgradeType = upgradeType;
    }
    
    
}
