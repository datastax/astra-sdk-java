package com.datastax.astra.shell;

/**
 * Normalization of exit codes.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum ExitCode {
    
    /** code ok. */
    SUCCESS(0),
    
    /** code. */
    INVALID_PARAMETER(400),
    
    /** code. */
    NOT_FOUND(404),
    
    /** conflict. */
    CONFLICT(409),
    
    /** code. */
    PARSE_ERROR(-1),
    
    /** code. */
    CANNOT_CONNECT(408);
    
    /* Exit code. */
    private int code;
    
    /**
     * Constructor.
     *
     * @param code
     *      target code
     */
    private ExitCode(int code) {
        this.code = code;
    }

    /**
     * Getter accessor for attribute 'code'.
     *
     * @return
     *       current value of 'code'
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Exit the prgram.
     */
    public void exit() {
        System.exit(code);
    }
     
    

}
