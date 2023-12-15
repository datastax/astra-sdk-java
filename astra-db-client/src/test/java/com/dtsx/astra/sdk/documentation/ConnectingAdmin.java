package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBAdmin;

import java.util.UUID;

public class ConnectingAdmin {
    public static void main(String[] args) {
        // Default Initialization
        AstraDBAdmin client = new AstraDBAdmin("<token>");

        /*
         * You can omit the token if you defined the environment variable
         * `ASTRA_DB_APPLICATION_TOKEN` or you if are using the Astra CLI.
         */
        AstraDBAdmin defaultClient=new AstraDBAdmin();
    }
}
