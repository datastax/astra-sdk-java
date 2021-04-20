package io.stargate.sdk.core;

import java.util.Collection;
import java.util.Map;

import io.stargate.sdk.utils.JsonUtils;

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
