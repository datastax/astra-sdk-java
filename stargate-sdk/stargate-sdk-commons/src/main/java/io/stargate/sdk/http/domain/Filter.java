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

package io.stargate.sdk.http.domain;

import io.stargate.sdk.utils.JsonUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Encoding Filter.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Filter {

    /** Column name in the table. */
    private final String fieldName;

    /** Condition of our filter. */
    private final FilterCondition condition;

    /** Value, could be multiple value. */
    private final Object fieldValue;

    /**
     * Full fledge constructor
     * @param fieldName
     *      field to filter on 
     * @param cond
     *      condition for the filter
     * @param val
     *      value for the filter
     */
    public Filter(String fieldName, FilterCondition cond, Object val) {
        this.fieldName = fieldName;
        this.condition = cond;
        this.fieldValue = val;
    }

    /** {@inheritDoc} */
    public String toString() {
        // Serializing as JSON and no need for Espacing here yet
        StringBuilder wherePiece = new StringBuilder("\"");
        wherePiece.append(fieldName);
        wherePiece.append("\": {\"");
        wherePiece.append(condition.getOperator());
        wherePiece.append("\":");
        if (fieldValue instanceof Collection) {
            wherePiece.append(JsonUtils.collectionAsJson((Collection<?>) fieldValue));
        } else if (fieldValue instanceof Map) {
            wherePiece.append(JsonUtils.mapAsJson((Map<?, ?>) fieldValue));
        } else {
            wherePiece.append(JsonUtils.valueAsJson(fieldValue));
        }
        wherePiece.append("}");
        return wherePiece.toString();
    }

}
