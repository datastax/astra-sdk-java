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

package com.dtsx.astra.sdk.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

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
    
    /**
     * hasLength
     * 
     * @param str String
     * @return boolean
     */
    public static boolean hasLength(String str) {
        return (null != str && !"".equals(str));
    }
    
    /**
     * paramsProvided
     * 
     * @param lStr String
     * @return boolean
     */
    public static boolean hasAllLength(String... lStr) {
        if (null == lStr) return false;
        return Arrays.stream(lStr).allMatch(Utils::hasLength);
    }

    /**
     * Download File Content with retry logic.
     *
     * @param fileUrl
     *      current file URL
     * @return
     *      the file content
     */
    public static byte[] downloadFile(String fileUrl) {
        int maxRetries = 3;
        int retryDelayMs = 1000; // 1 second initial delay
        double backoffMultiplier = 2.0;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000); // 5 second connection timeout
                connection.setReadTimeout(30000);   // 30 second read timeout
                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    return out.toByteArray();
                }
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL format: " + fileUrl, e);
            } catch (java.net.SocketTimeoutException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Connection timeout while downloading file from " + fileUrl, e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            } catch (java.net.UnknownHostException e) {
                throw new IllegalArgumentException("Cannot resolve host for URL: " + fileUrl, e);
            } catch (java.net.ConnectException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Cannot connect to server at " + fileUrl, e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            } catch (IOException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Failed to download file from " + fileUrl + 
                            " after " + maxRetries + " attempts. Last error: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            }
        }
        throw new IllegalArgumentException("Failed to download file from " + fileUrl + " after " + maxRetries + " attempts");
    }

    /**
     * Download file to specified location with retry logic.
     *
     * @param urlStr String
     * @param file String
     */
    public static void downloadFile(String urlStr, String file) {
        int maxRetries = 3;
        int retryDelayMs = 1000; // 1 second initial delay
        double backoffMultiplier = 2.0;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            URL url;
            FileOutputStream fis = null;
            BufferedInputStream bis = null;
            try {
                url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000); // 5 second connection timeout
                urlConnection.setReadTimeout(30000);   // 30 second read timeout
                urlConnection.setRequestProperty("Accept", "bytes");
                bis = new BufferedInputStream(urlConnection.getInputStream());
                fis = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = bis.read(buffer, 0, 1024)) != -1) {
                    fis.write(buffer, 0, count);
                }
                return; // Success - exit the method
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL format: " + urlStr, e);
            } catch (java.net.SocketTimeoutException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Connection timeout while downloading file from " + urlStr, e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            } catch (java.net.UnknownHostException e) {
                throw new IllegalArgumentException("Cannot resolve host for URL: " + urlStr, e);
            } catch (java.net.ConnectException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Cannot connect to server at " + urlStr, e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            } catch (IOException e) {
                if (attempt == maxRetries) {
                    throw new IllegalArgumentException("Failed to download file from " + urlStr + 
                            " after " + maxRetries + " attempts. Last error: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(retryDelayMs * (long) Math.pow(backoffMultiplier, attempt - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException("Download interrupted", ie);
                }
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (bis != null) bis.close();
                } catch (IOException e) {
                    System.err.println("Warning: Error closing streams: " + e.getMessage());
                }
            }
        }
        throw new IllegalArgumentException("Failed to download file from " + urlStr + " after " + maxRetries + " attempts");
    }
    
    /**
     * Syntaxic sugar to read environment variables.
     *
     * @param key
     *      environment variable
     * @return
     *      if the value is there
     */
    public static Optional<String> readEnvVariable(String key) {
        if (Utils.hasLength(System.getProperty(key))) {
            return Optional.ofNullable(System.getProperty(key));
        } else if (Utils.hasLength(System.getenv(key))) {
            return Optional.ofNullable(System.getenv(key));
        }
        return Optional.empty();
    }
}
