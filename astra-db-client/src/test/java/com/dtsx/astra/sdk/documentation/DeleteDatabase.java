package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;

public class DeleteDatabase {
    public static void main(String[] args) {
        AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");

        // Delete from its Name
        client.deleteDatabase("<replace_with_db_name>");
    }
}
