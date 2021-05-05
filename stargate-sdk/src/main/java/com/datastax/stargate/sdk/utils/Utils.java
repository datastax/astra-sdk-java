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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Utilities
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Utils {
    
    /**
     * Private constructor
     */
    private Utils() {}
    
    
    public static boolean hasLength(String str) {
        return (null != str && !"".equals(str));
    }
    
    public static boolean paramsProvided(String... lStr) {
        if (null == lStr) return false;
        return Arrays.stream(lStr).allMatch(Utils::hasLength);
    }
    
    /**
     * Download file
     */
    public static void downloadFile(String urlStr, String file) {
        URL url;
        FileOutputStream    fis = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "bytes");
            bis = new BufferedInputStream(urlConnection.getInputStream());
            fis = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count=0;
            while((count = bis.read(buffer,0,1024)) != -1) {
                fis.write(buffer, 0, count);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Cannot read URL, invalid syntax",e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot download file",e);
        } finally {
            try {
                if (null != fis) fis.close();
                if (null!= bis)  bis.close();
            } catch (IOException e) {}
        }
    }

}
