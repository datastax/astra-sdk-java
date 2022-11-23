package io.stargate.sdk.utils;

/**
 * Helper to log with colors.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AnsiUtils {
    
    /** Color. */
    public static final String ANSI_RESET           = "\u001B[0m";
    
    /** Color. */
    public static final String ANSI_BLACK           = "\u001b[30m";
    
    /** Color. */
    public static final String ANSI_RED             = "\u001b[31m";
    
    /** Color. */
    public static final String ANSI_GREEN           = "\u001B[32m";
    
    /** Color. */
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    /** Color. */
    public static final String ANSI_BLUE            = "\u001b[34m";
    
    /** Color. */
    public static final String ANSI_MAGENTA         = "\u001b[35m";
    
    /** Color. */
    public static final String ANSI_CYAN            = "\u001b[36m";
    
    /** Color. */
    public static final String ANSI_WHITE           = "\u001b[37m";
    
    /**
     * Hide constructor.
     */
    private AnsiUtils() {}
    
    /**
     * write black.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String black(String msg) {
        return ANSI_BLACK + msg + ANSI_RESET;
    }
    
    /**
     * write red.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String red(String msg) {
        return ANSI_RED + msg + ANSI_RESET;
    }
    
    /**
     * write green.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String green(String msg) {
        return ANSI_GREEN + msg + ANSI_RESET;
    }
    
    /**
     * write yellow.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String yellow(String msg) {
        return ANSI_YELLOW + msg + ANSI_RESET;
    }
    
    /**
     * write blue.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String blue(String msg) {
        return ANSI_BLUE + msg + ANSI_RESET;
    }
    
    /**
     * write magenta.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String magenta(String msg) {
        return ANSI_MAGENTA + msg + ANSI_RESET;
    }
    
    /**
     * write cyan.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String cyan(String msg) {
        return ANSI_CYAN + msg + ANSI_RESET;
    }
    
    /**
     * write white.
     * 
     * @param msg
     *      message
     * @return
     *      value in expected color.
     */
    public static String white(String msg) {
        return ANSI_WHITE + msg + ANSI_RESET;
    }
            

}
