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

package com.datastax.astra.sdk.utils;

import static com.datastax.astra.sdk.AstraClient.ASTRA_DB_APPLICATION_TOKEN;
import static com.datastax.astra.sdk.AstraClient.ASTRA_DB_ID;
import static com.datastax.astra.sdk.AstraClient.ASTRA_DB_KEYSPACE;
import static com.datastax.astra.sdk.AstraClient.ASTRA_DB_REGION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter.Include;

/**
 * Utility class to load/save .astrarc file. This file is used to store
 * Astra configuration.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraRc {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraRc.class);
   
    /** Default filename we are looking for. */
    public static final String ASTRARC_FILENAME  = ".astrarc";
    public static final String ASTRARC_DEFAULT   = "default";
    
    /** Environment variable coding user home. */
    public static final String ENV_USER_HOME      = "user.home";
    public static final String ENV_LINE_SEPERATOR = "line.separator";
    
    /** Sections in the file. [sectionName] key/Value. */
    private final Map <String, Map<String, String>> sections;
    
    /**
     * Load from ~/.astrarc
     */
    public AstraRc() {
        this.sections = AstraRc.load().getSections();
    }
    
    /**
     * Load from specified file
     * 
     * @param fileName String
     */
    public AstraRc(String fileName) {
        this.sections = AstraRc.load(fileName).getSections();
    }
    
    /**
     * Load from a set of keys (sections / Key / Value)
     * 
     * @param s Map
     */
    public AstraRc(Map <String, Map<String, String>> s) {
        this.sections = s;
    }
    
    /**
     * Getter accessor for attribute 'sections'.
     *
     * @return
     *       current value of 'sections'
     */
    public Map<String, Map<String, String>> getSections() {
        return sections;
    }
    
    /**
     * Display output in the console
     */
    public void print() {
        System.out.println(generateFileContent(getSections()));
    }
    
    /**
     * Helper to react a key in the file based on section name and key
     * 
     * @param sectionName String
     * @param key String
     * @return String
     */
    public String read(String sectionName, String key) {
        return (!sections.containsKey(sectionName)) ? 
                null :sections.get(sectionName).get(key);
    }
    
    // -- Static operations --
    
    /**
     * Check if file ~/.astrac is present in the filesystem
     * 
     * @return File
     */
    public static boolean exists() {
        return new File(System.getProperty(ENV_USER_HOME) 
                + File.separator 
                + ASTRARC_FILENAME).exists();
    }
    
    /**
     * Generate astrarc based on values in DB using devops API.
     *
     * @param devopsClient ApiDevopsClient
     */
    public static void create(DatabasesClient devopsClient) {
        save(extractDatabasesInfos(devopsClient));
    }
  
    /**
     * Update only one key.
     * 
     * @param section String
     * @param key String
     * @param value String
     */
    public static void save(String section, String key, String value) {
        Map <String, Map<String, String>> astraRc = new HashMap<>();
        Map<String, String> val = new HashMap<>();
        val.put(key, value);
        astraRc.put(section, val);
        save(astraRc);
    }
    
    /**
     * Create the file from a list of key, merging with existing
     *
     * @param astraRc
     *      update .astrarc file. 
     */
    public static void save(Map <String, Map<String, String>> astraRc) {
        LOGGER.info("Updating .astrarc file");
        // This map is empty if file does not exist
        Map <String, Map<String, String>> targetAstraRc = astraRc;
        
        if (exists()) {
            targetAstraRc = load().getSections();
            
            // Merge if needed, append otherwize
            for (String dbName : astraRc.keySet()) {
                if (targetAstraRc.containsKey(dbName)) {
                    // overriding keys (merge)
                    targetAstraRc.get(dbName).putAll(astraRc.get(dbName));
                } else {
                    // Append
                    targetAstraRc.put(dbName, astraRc.get(dbName));
                }
                LOGGER.info("+ updating [" + dbName + "]");
            }
        }
            
        // Generate expected file
        File outFile = new File(System.getProperty(ENV_USER_HOME) 
                + File.separator 
                + ASTRARC_FILENAME);
        FileWriter out = null;
        try {
            out = new FileWriter(outFile);
            out.write(generateFileContent(targetAstraRc));
            LOGGER.info("File {} has been successfully updated.", outFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save astrarc file", e);
        } finally {
            if (null != out) {
                try { out.close(); } catch (IOException e) {}
            }
        }
    }
    
    /*
     * Loading ~/.astrarc (if present).
     * 
     * @return AstraRc
     */ 
    public static AstraRc load() {
        return load(System.getProperty(ENV_USER_HOME) + File.separator + ASTRARC_FILENAME);
    }
    
    /**
     * Loading ~/.astrarc (if present).
     * Key = block name (dbname of default), then key/value
     * 
     * @param fileName String
     * @return AstraRc
     */
    public static AstraRc load(String fileName) {
        Map <String, Map<String, String>> sections = new HashMap<>();
        File current = new File(fileName);
        try(Scanner scanner = new Scanner(current)) {
            if (current.exists()) {
                String sectionName = "";
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("[")) {
                        // Starting a new section
                        sectionName = line.replaceAll("\\[", "") 
                                          .replaceAll("\\]", "").trim();
                        sections.put(sectionName, new HashMap<>());
                    } else if (!line.startsWith("#") && !"".equals(line)) {
                        int off = line.indexOf("=");
                        if (off < 0) {
                            throw new IllegalArgumentException("Cannot parse file " +
                                    fileName + ", line '" 
                                    + line + "' invalid format expecting key=value");
                        }
                        String key = line.substring(0,off);
                        String val = line.substring(off+1);
                        sections.get(sectionName).put(key, val);
                    }
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot read configuration file", e);
        }
        return new AstraRc(sections);
    }
    
    /**
     * Prepare file content
     * 
     * @param astraRc Map
     */
    private static String generateFileContent(Map <String, Map<String, String>> astraRc) {
        StringBuilder sb = new StringBuilder();
        if (astraRc.containsKey(ASTRARC_DEFAULT)) {
            sb.append("[" + ASTRARC_DEFAULT + "]");
            sb.append(System.getProperty(ENV_LINE_SEPERATOR));
            for (Entry<String,String> line : astraRc.get(ASTRARC_DEFAULT).entrySet()) {
                sb.append(line.getKey() + "=" + line.getValue());
                sb.append(System.getProperty(ENV_LINE_SEPERATOR));
            }
        }
        
        for (String dbName : astraRc.keySet()) {
            if (!ASTRARC_DEFAULT.equals(dbName)) {
                sb.append(System.getProperty(ENV_LINE_SEPERATOR));
                sb.append("[" + dbName + "]");
                sb.append(System.getProperty(ENV_LINE_SEPERATOR));
                for (Entry<String,String> line : astraRc.get(dbName).entrySet()) {
                    sb.append(line.getKey() + "=" + line.getValue());
                    sb.append(System.getProperty(ENV_LINE_SEPERATOR));
                }
            }
        }
        
        return sb.toString();
    }
   
    /**
     * Generate expecting key in the file
     * 
     * @param devopsClient ApiDevopsClient
     */
    private static Map <String, Map<String, String>> extractDatabasesInfos(DatabasesClient devopsClient) {
        // Look for 'all' (limit 100), non terminated DB
        List<Database> dbs = devopsClient.searchDatabases(DatabaseFilter.builder()
                .limit(100)
                .provider(CloudProviderType.ALL)
                .include(Include.NON_TERMINATED)
                .build()).collect(Collectors.toList());
        
        // [default]
        Map <String, Map<String, String>> result = new HashMap<>();
        result.put(ASTRARC_DEFAULT, new HashMap<>());
        result.get(ASTRARC_DEFAULT).put(ASTRA_DB_APPLICATION_TOKEN, devopsClient.getBearerAuthToken());
        if (dbs.size() > 0) {
            result.get(ASTRARC_DEFAULT).putAll(dbKeys(dbs.get(0), devopsClient.getBearerAuthToken()));
        }
        // Loop on each database
        dbs.stream().forEach(db -> result.put(db.getInfo().getName(), dbKeys(db, devopsClient.getBearerAuthToken())));
        return result;
    }
    
    /**
     * dbKeys
     * 
     * @param db Database
     * @param token String
     * @return Map
     */
    private static Map<String, String> dbKeys(Database db, String token) {
        Map<String, String> dbKeys = new HashMap<>();
        dbKeys.put(ASTRA_DB_ID, db.getId() );
        dbKeys.put(ASTRA_DB_REGION, db.getInfo().getRegion());
        dbKeys.put(ASTRA_DB_KEYSPACE, db.getInfo().getKeyspace());
        dbKeys.put(ASTRA_DB_APPLICATION_TOKEN, token);
        return dbKeys;
    }

}
