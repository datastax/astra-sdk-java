/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtsx.astra.sdk.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Response wrapper for database snapshots list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseSnapshotsResponse implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /** list of snapshots. */
    private List<DatabaseSnapshot> snapshots;

    /**
     * Default constructor.
     */
    public DatabaseSnapshotsResponse() {}

    /**
     * Getter accessor for attribute 'snapshots'.
     *
     * @return
     *       current value of 'snapshots'
     */
    public List<DatabaseSnapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * Setter accessor for attribute 'snapshots'.
     * @param snapshots
     * 		new value for 'snapshots '
     */
    public void setSnapshots(List<DatabaseSnapshot> snapshots) {
        this.snapshots = snapshots;
    }
    
}
