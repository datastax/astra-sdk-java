package com.dtsx.astra.sdk.cassio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusteredRecord {

    /** Partition id. */
    String partitionId;

    /** Row identifier. */
    UUID rowId;

    /** Text body. */
    String body;

}