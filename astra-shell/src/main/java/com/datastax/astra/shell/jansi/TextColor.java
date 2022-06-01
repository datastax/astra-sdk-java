package com.datastax.astra.shell.jansi;

/**
 * Foreground color in ANSI Terminal
 *
 * @author Cedrick Lunven (@clunven)
 */
public enum TextColor {
    
    /** color. */
    RESET(0),
    
    /** color. */
    BLACK   (30),
    
    /** color. */
    RED     (31),
    
    /** color. */
    GREEN   (32),
    
    /** color. */
    YELLOW  (33),
    
    /** color. */
    BLUE    (34),
    
    /** color. */
    MAGENTA (35),
    
    /** color. */
    CYAN    (36),
    
    /** color. */
    WHITE   (37);
    
    /** Code color for foreGround. */
    private final int code;
    
    /**
     * Default Constructor.
     *
     * @param pcode
     */
    private TextColor(int pcode) {
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
