package com.dstx.astra.sdk.utils;

public class Assert {
    
    public static void hasLength(String s, String name) {
        if (s == null || "".equals(s)) {
            throw new IllegalArgumentException("Parameter '" + name + "' should be null nor empty");
        }
    }
    
    public static void notNull(Object o, String name) {
        if (o == null) {
            throw new IllegalArgumentException("Parameter '" + name + "' should be null nor empty");
        }
    }
    
    public static void isTrue(Boolean b, String msg) {
        if (!b) {
            throw new IllegalArgumentException(msg);
        }
    }

}
