package com.dtsx.astra.sdk.utils.observability;

/*-
 * #%L
 * Data API Java Client
 * --
 * Copyright (C) 2024 DataStax
 * --
 * Licensed under the Apache License, Version 2.0
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Helper to log with colors.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AnsiUtils {
    
    /** Color. */
    public static final String ANSI_RESET           = "\u001B[0m";

    /** Color. */
    public static final String ANSI_GREEN           = "\u001B[32m";
    
    /** Color. */
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    /** Color. */
    public static final String ANSI_MAGENTA         = "\u001b[35m";
    
    /** Color. */
    public static final String ANSI_CYAN            = "\u001b[36m";

    /**
     * Hide constructor.
     */
    private AnsiUtils() {}

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


}
