package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.cassio.MetadataVectorTableTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractAstraVectorTest {

    @SuppressWarnings("unchecked")
    protected LinkedHashMap<String, List<?>> loadQuotes(String filePath) throws IOException {
        File inputFile = new File(MetadataVectorTableTest.class.getClassLoader().getResource(filePath).getFile());
        LinkedHashMap<String, Object> sampleQuotes = new ObjectMapper().readValue(inputFile, LinkedHashMap.class);
        System.out.println("Quotes by Author:");
        ((LinkedHashMap<?,?>) sampleQuotes.get("quotes")).forEach((k,v) ->
                System.out.println("   " + k + " (" + ((ArrayList<?>)v).size() + ") "));
        log.info("Sample Quotes");
        ((LinkedHashMap<?, ?>) sampleQuotes.get("quotes"))
                .entrySet().stream().limit(2)
                .forEach(e -> {
                    System.out.println("   " + e.getKey() + " : ");
                    Map<String, Object> entry = (Map<String,Object>) ((ArrayList<?>)e.getValue()).get(0);
                    System.out.println("      " + ((String) entry.get("body")).substring(0, 50) + "... (tags: " + entry.get("tags") + ")");
                    entry = (Map<String,Object>) ((ArrayList<?>)e.getValue()).get(1);
                    System.out.println("      " + ((String) entry.get("body")).substring(0, 50) + "... (tags: " + entry.get("tags") + ")");
                });
        return  ((LinkedHashMap<String, List<?>>) sampleQuotes.get("quotes"));
    }

}
