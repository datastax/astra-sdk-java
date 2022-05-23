package com.datastax.astra.ansi;

/**
 * Foreground color in ANSI Terminal
 *
 * @author Cedrick Lunven (@clunven)
 */
public enum TextStyle {
    
    /** style. */
    CLEAR(0),
    
    /** style. */
    BOLD(1),
    
    /** style. */
    LIGHT(1),
    
    /** style. */
    DARK(2),
    
    /** style. */
    UNDERLINE(4),
    
    /** style. */
    REVERSE(7),
    
    /** style. */
    HIDDEN(8);
    
    /** Code color for foreGround. */
    private final int code;
    
    /**
     * Default Constructor.
     *
     * @param pcode
     */
    private TextStyle(int pcode) {
        this.code = pcode;
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
}
