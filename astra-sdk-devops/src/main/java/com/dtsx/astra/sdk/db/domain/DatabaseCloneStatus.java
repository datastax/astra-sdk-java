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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent a database clone status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseCloneStatus implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = 8242671294103939313L;

    /** unique identifier for the operation. */
    private String operationId;

    /** source database identifier. */
    private String sourceDbId;

    /** target database identifier. */
    private String targetDbId;

    /** current phase of the clone operation. */
    private String phase;

    /** status of the clone operation. */
    private String status;

    /** message with additional details. */
    private String message;

    /** snapshot identifier. */
    private String snapshotId;

    /** timestamp when the clone was created. */
    private String createdAt;

    /** source region for the clone operation. */
    private String sourceRegion;

    /** target region for the clone operation. */
    private String targetRegion;

    /**
     * Default constructor.
     */
    public DatabaseCloneStatus() {}

    /**
     * Getter accessor for attribute 'operationId'.
     *
     * @return
     *       current value of 'operationId'
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * Setter accessor for attribute 'operationId'.
     * @param operationId
     * 		new value for 'operationId '
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * Getter accessor for attribute 'sourceDbId'.
     *
     * @return
     *       current value of 'sourceDbId'
     */
    public String getSourceDbId() {
        return sourceDbId;
    }

    /**
     * Setter accessor for attribute 'sourceDbId'.
     * @param sourceDbId
     * 		new value for 'sourceDbId '
     */
    public void setSourceDbId(String sourceDbId) {
        this.sourceDbId = sourceDbId;
    }

    /**
     * Getter accessor for attribute 'targetDbId'.
     *
     * @return
     *       current value of 'targetDbId'
     */
    public String getTargetDbId() {
        return targetDbId;
    }

    /**
     * Setter accessor for attribute 'targetDbId'.
     * @param targetDbId
     * 		new value for 'targetDbId '
     */
    public void setTargetDbId(String targetDbId) {
        this.targetDbId = targetDbId;
    }

    /**
     * Getter accessor for attribute 'phase'.
     *
     * @return
     *       current value of 'phase'
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Setter accessor for attribute 'phase'.
     * @param phase
     * 		new value for 'phase '
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * Getter accessor for attribute 'status'.
     *
     * @return
     *       current value of 'status'
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter accessor for attribute 'status'.
     * @param status
     * 		new value for 'status '
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter accessor for attribute 'message'.
     *
     * @return
     *       current value of 'message'
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter accessor for attribute 'message'.
     * @param message
     * 		new value for 'message '
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter accessor for attribute 'snapshotId'.
     *
     * @return
     *       current value of 'snapshotId'
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * Setter accessor for attribute 'snapshotId'.
     * @param snapshotId
     * 		new value for 'snapshotId '
     */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * Getter accessor for attribute 'createdAt'.
     *
     * @return
     *       current value of 'createdAt'
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter accessor for attribute 'createdAt'.
     * @param createdAt
     * 		new value for 'createdAt '
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Getter accessor for attribute 'sourceRegion'.
     *
     * @return
     *       current value of 'sourceRegion'
     */
    public String getSourceRegion() {
        return sourceRegion;
    }

    /**
     * Setter accessor for attribute 'sourceRegion'.
     * @param sourceRegion
     * 		new value for 'sourceRegion '
     */
    public void setSourceRegion(String sourceRegion) {
        this.sourceRegion = sourceRegion;
    }

    /**
     * Getter accessor for attribute 'targetRegion'.
     *
     * @return
     *       current value of 'targetRegion'
     */
    public String getTargetRegion() {
        return targetRegion;
    }

    /**
     * Setter accessor for attribute 'targetRegion'.
     * @param targetRegion
     * 		new value for 'targetRegion '
     */
    public void setTargetRegion(String targetRegion) {
        this.targetRegion = targetRegion;
    }

}
