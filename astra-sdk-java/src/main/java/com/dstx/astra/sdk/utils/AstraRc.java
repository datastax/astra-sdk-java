package com.dstx.astra.sdk.utils;

import static com.dstx.astra.sdk.AstraClient.ASTRA_CLIENT_ID;
import static com.dstx.astra.sdk.AstraClient.ASTRA_CLIENT_NAME;
import static com.dstx.astra.sdk.AstraClient.ASTRA_CLIENT_SECRET;
import static com.dstx.astra.sdk.AstraClient.ASTRA_DB_ID;
import static com.dstx.astra.sdk.AstraClient.ASTRA_DB_PASSWORD;
import static com.dstx.astra.sdk.AstraClient.ASTRA_DB_REGION;
import static com.dstx.astra.sdk.AstraClient.ASTRA_DB_USERNAME;

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

import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.devops.AstraDatabaseInfos;
import com.dstx.astra.sdk.devops.CloudProvider;
import com.dstx.astra.sdk.devops.DatabaseFilter;
import com.dstx.astra.sdk.devops.DatabaseFilter.Include;

/**
 * Utility class to load/save interact with .astrarc file.
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
    
    /** Sections in the file. */
    private final Map <String, Map<String, String>> sections;
    
    public AstraRc() {
        this.sections = AstraRc.load().getSections();
    }
    
    public AstraRc(String fileName) {
        this.sections = AstraRc.load(fileName).getSections();
    }
    
    /** Hid epublic constructor. */
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
    
    public String read(String sectionName, String key) {
        return (!sections.containsKey(sectionName)) ? 
                null :sections.get(sectionName).get(key);
    }
    
    /**
     * If file present a merge is done.
     */
    public static boolean exists() {
        return new File(System.getProperty(ENV_USER_HOME) 
                + File.separator 
                + ASTRARC_FILENAME).exists();
    }
    
    /**
     * Generate astrarc based on values in DB using devops API.
     *
     * @param devopsClient
     */
    public static void upsert(ApiDevopsClient devopsClient) {
        upsert(extractDatabasesInfos(devopsClient));
    }
  
    /**
     * Update only one key.
     */
    public static void upsert(String section, String key, String value) {
        Map <String, Map<String, String>> astraRc = new HashMap<>();
        Map<String, String> val = new HashMap<>();
        val.put(key, value);
        astraRc.put(section, val);
        upsert(astraRc);
    }
    
    /**
     * Create the file from a list of key, merging with existing
     *
     * @param astraRc
     *      update .astrarc file. 
     * @throws IOException 
     */
    public static void upsert(Map <String, Map<String, String>> astraRc) {
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
     */ 
    public static AstraRc load() {
        return load(System.getProperty(ENV_USER_HOME) + File.separator + ASTRARC_FILENAME);
    }
    
    /**
     * Loading ~/.astrarc (if present).
     * Key = block name (dbname of default), then key/value
     * 
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
     */
    private static Map <String, Map<String, String>> extractDatabasesInfos(ApiDevopsClient devopsClient) {
        // Look for db
        List<AstraDatabaseInfos> dbs = devopsClient.findDatabases(DatabaseFilter.builder()
                .limit(20)
                .provider(CloudProvider.ALL)
                .include(Include.NON_TERMINATED)
                .build()).collect(Collectors.toList());
        
        // [default]
        Map <String, Map<String, String>> result = new HashMap<>();
        result.put(ASTRARC_DEFAULT, new HashMap<>());
        result.get(ASTRARC_DEFAULT).put(ASTRA_CLIENT_ID, devopsClient.getClientId());
        result.get(ASTRARC_DEFAULT).put(ASTRA_CLIENT_NAME, devopsClient.getClientName());
        result.get(ASTRARC_DEFAULT).put(ASTRA_CLIENT_SECRET, devopsClient.getClientSecret());
        if (dbs.size() > 0) {
            result.get(ASTRARC_DEFAULT).putAll(dbKeys(dbs.get(0)));
        }
        
        // Loop on each database
        dbs.stream().forEach(db -> result.put(db.getInfo().getName(), dbKeys(db)));
        
        // If password value already exist in the file we do not want to override with 
        // <ENTER_YOUR_PASSWORD>. It should be provided only if not exists.
        if (exists()) {
            load().getSections().entrySet().stream()
                  // Make sure we update an existing section
                  .filter(e -> result.containsKey(e.getKey()))
                  // Make sure existing file does have the key
                  .filter(e -> e.getValue().containsKey(ASTRA_DB_PASSWORD))
                  // Remove key to avoid overriding
                  .forEach(e -> result.get(e.getKey()).remove(ASTRA_DB_PASSWORD));
        }
        
        return result;
    }
    
    private static Map<String, String> dbKeys(AstraDatabaseInfos db) {
        Map<String, String> dbKeys = new HashMap<>();
        dbKeys.put(ASTRA_DB_ID, db.getId() );
        dbKeys.put(ASTRA_DB_REGION, db.getInfo().getRegion());
        dbKeys.put(ASTRA_DB_USERNAME, db.getInfo().getUser());
        dbKeys.put(ASTRA_DB_PASSWORD, "<ENTER_YOUR_PASSWORD>");
        return dbKeys;
    }

}
