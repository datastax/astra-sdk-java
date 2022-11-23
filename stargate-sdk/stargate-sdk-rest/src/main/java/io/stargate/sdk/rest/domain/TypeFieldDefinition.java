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

package io.stargate.sdk.rest.domain;

import java.io.Serializable;

/**
 * Work with the column.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TypeFieldDefinition implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 5953054505785338100L;

    /** Unique identifier in the class. */
    protected String name;
    
    /** Type Definition if static. */
    protected String typeDefinition;
    
    /**
     * Default Constructor
     */
    public TypeFieldDefinition() {}
    
    /**
     * Full constructor.
     * 
     * @param name
     *      column identifier
     * @param type
     *      type as a string
     */
     public TypeFieldDefinition(String name, String type) {
        this.name = name;
        this.typeDefinition = type;
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
     * Getter accessor for attribute 'typeDefinition'.
     *
     * @return
     *       current value of 'typeDefinition'
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * Setter accessor for attribute 'typeDefinition'.
     * @param typeDefinition
     * 		new value for 'typeDefinition '
     */
    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
    
}
