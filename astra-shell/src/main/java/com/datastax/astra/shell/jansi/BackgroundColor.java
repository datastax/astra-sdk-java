package com.datastax.astra.shell.jansi;

/**
 * Foreground color in ANSI Terminal
 *
 * @author Cedrick Lunven (@clunven)
 */
public enum BackgroundColor {
    
    /** color. */
    BLACK   (40),
    
    /** color. */
    RED     (41),
    
    /** color. */
    GREEN   (42),
    
    /** color. */
    YELLOW  (43),
    
    /** color. */
    BLUE    (44),
    
    /** color. */
    MAGENTA (45),
    
    /** color. */
    CYAN    (46),
    
    /** color. */
    WHITE   (47);
    
    /** Code color for foreGround. */
    private final int code;
    
    /**
     * Default Constructor.
     *
     * @param pcode
     */
    private BackgroundColor(int pcode) {
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
