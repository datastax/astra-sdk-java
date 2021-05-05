/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.utils;

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
