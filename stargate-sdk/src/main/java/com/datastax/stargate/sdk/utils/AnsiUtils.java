package com.datastax.stargate.sdk.utils;

public class AnsiUtils {
    
    public static final String ANSI_RESET           = "\u001B[0m";
    public static final String ANSI_BLACK           = "\u001b[30m";
    public static final String ANSI_RED             = "\u001b[31m";
    public static final String ANSI_GREEN           = "\u001B[32m";
    public static final String ANSI_YELLOW          = "\u001B[33m";
    public static final String ANSI_BLUE            = "\u001b[34m";
    public static final String ANSI_MAGENTA         = "\u001b[35m";
    public static final String ANSI_CYAN            = "\u001b[36m";
    public static final String ANSI_WHITE           = "\u001b[37m";

    
    /**
     * Hide constructor.
     */
    private AnsiUtils() {}
    
    
    /**
     * black
     * @param msg
     * @return
     */
    public static String black(String msg) {
        return ANSI_BLACK + msg + ANSI_RESET;
    }
    
    public static String red(String msg) {
        return ANSI_RED + msg + ANSI_RESET;
    }
    
    public static String green(String msg) {
        return ANSI_GREEN + msg + ANSI_RESET;
    }
    
    public static String yellow(String msg) {
        return ANSI_YELLOW + msg + ANSI_RESET;
    }
    
    public static String blue(String msg) {
        return ANSI_BLUE + msg + ANSI_RESET;
    }
    
    public static String magenta(String msg) {
        return ANSI_MAGENTA + msg + ANSI_RESET;
    }
    
    public static String cyan(String msg) {
        return ANSI_CYAN + msg + ANSI_RESET;
    }
    
    public static String white(String msg) {
        return ANSI_WHITE + msg + ANSI_RESET;
    }
            

}
