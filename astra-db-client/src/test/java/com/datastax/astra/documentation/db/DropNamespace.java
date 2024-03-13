package com.datastax.astra.documentation.db;

import com.datastax.astra.db.AstraDBDatabase;

public class DropNamespace {

    public static void main(String[] args) {
        // Default initialization
        AstraDBDatabase db = new AstraDBDatabase("API_ENDPOINT", "TOKEN");

        // Drop a Namespace
        db.dropNamespace("<namespace_name>");
    }
}
