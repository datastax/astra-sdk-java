package cpm.dstx.astrarc;

import java.util.stream.Collectors;

import org.datastax.astra.ApiSupportTest;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.utils.AstraRc;


public class AstraRcLoaderTest extends ApiSupportTest {
    
    @Test
    public void generateAstraRc() {
        AstraRc.upsert(new ApiDevopsClient(clientName, clientId, clientSecret));
        AstraRc.upsert("default", AstraClient.ASTRA_DB_PASSWORD, "astraPassword1");
        AstraRc.upsert("freetier", AstraClient.ASTRA_DB_PASSWORD, "astraPassword1");
    }
    
    @Test
    public void useClientWithRc() {
        System.out.println(AstraClient.builder().build()
                   .apiDocument().namespaceNames()
                   .collect(Collectors.toList()));
    }
    
    @Test
    public void load() {
        System.out.println(AstraRc.exists());
        //AstraRc rc  = new AstraRc();
        //System.out.println(rc.read("default", "ASTRA_CLIENT_SECRET"));
        
        //AstraRc rc1 = AstraRc.load();
        //System.out.println(rc1.read("default", "ASTRA_CLIENT_SECRET"));
        
        //AstraRc rc2 = new AstraRc("src/test/resources/astrarc-sample-1.txt");
        //System.out.println(rc2.read("default", "ASTRA_CLIENT_SECRET"));
    }

}
