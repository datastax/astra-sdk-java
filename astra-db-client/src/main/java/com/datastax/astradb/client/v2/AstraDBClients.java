package com.datastax.astradb.client.v2;

import com.datastax.astradb.client.AstraDBAdmin;

/**
 * High level class to instanciate clients for AstraDB
 */
public class AstraDBClients {

    public static AstraDBAdmin create(String token) {
        return new AstraDBAdmin(token);
    }


}
