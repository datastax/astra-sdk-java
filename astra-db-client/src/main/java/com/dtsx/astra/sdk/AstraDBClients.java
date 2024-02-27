package com.dtsx.astra.sdk;

public class AstraDBClients {

    public static AstraDBAdmin create(String token) {
        return new AstraDBAdmin(token);
    }


}
