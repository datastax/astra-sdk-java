package cpm.dstx.astrarc;

import java.util.stream.Collectors;

import org.datastax.astra.ApiSupportTest;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.utils.AstraRc;


public class AstraRcLoaderTest extends ApiSupportTest {
    
    @Test
    public void should_work_with_astraRc() {
        
        AstraClient astraClient = AstraClient.builder().build();
        
        System.out.println("=================================");
        System.out.println(astraClient
                .apiDocument().namespaceNames()
                .collect(Collectors.toList()));
    }
    @Test
    public void should_setup_astraRc() {
        
        /*
         * 1. Create AstraRc based on devops API. You need to provide clientName
         * clientId and clientSecret for this call, later they will be save in the
         * file.
         */
        System.out.println("[TEST] Create AstraRC file");
        AstraRc.create(new ApiDevopsClient(clientName, clientId, clientSecret));
        
        /*
         * 2. Update password key explicitely (cannot be retrieved from API)
         */
        System.out.println("[TEST] Update Password");
        AstraRc.save("default", AstraClient.ASTRA_DB_PASSWORD, "astraPassword1");
        
        /**
         * 3. Congratulation you are all set..
         * As .astrarc file exist AstraClient setup will be done from the file
         */
        System.out.println("[TEST] Load from AstraRC");
        AstraClient astraClient = AstraClient.builder()
                .clientId(clientId)
                .clientName(clientName)
                .clientSecret(clientSecret)
                .build();
        
        /* 
         * Note: You can load your own file
         *  AstraClient astraClient = AstraClient.builder()
         *       .astraRc(AstraRc.load("file"), "section")
         *       .build();
         */
        System.out.println(astraClient
                .apiDocument().namespaceNames()
                .collect(Collectors.toList()));
    }
    
    @Test
    public void load() {
        //System.out.println(AstraRc.exists());
        
        AstraRc.load().print();
        //AstraRc rc  = new AstraRc();
        //System.out.println(rc.read("default", "ASTRA_CLIENT_SECRET"));
        
        //AstraRc rc1 = AstraRc.load();
        //System.out.println(rc1.read("default", "ASTRA_CLIENT_SECRET"));
        
        //AstraRc rc2 = new AstraRc("src/test/resources/astrarc-sample-1.txt");
        //System.out.println(rc2.read("default", "ASTRA_CLIENT_SECRET"));
    }

}
