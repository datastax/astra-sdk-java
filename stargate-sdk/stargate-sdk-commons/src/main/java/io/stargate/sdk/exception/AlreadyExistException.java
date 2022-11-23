package io.stargate.sdk.exception;

/**
 * Specialization when creating an entity which should be unique.
 */
public class AlreadyExistException extends IllegalArgumentException {

    /**
     * Default error.
     *
     * @param msg
     *      error message
     */
    public AlreadyExistException(String msg) {
        super(msg);
    }

}
