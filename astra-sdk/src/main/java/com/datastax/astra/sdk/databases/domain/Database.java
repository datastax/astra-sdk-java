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

package com.datastax.astra.sdk.databases.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Database {
    
    private String id;
    private String orgId;
    private String ownerId;
    
    private String creationTime;
    private String terminationTime;
    private String lastUsageTime;
    
    private DatabaseInfo       info;
    private DatabaseStatusType status;
    private DatabaseStatusType observedStatus;
    private DatabaseStorage    storage;
    private DatabaseCost       cost;
    private DatabaseMetrics    metrics;
    
    private Set<String> availableActions;
    private String studioUrl;
    private String grafanaUrl;
    private String cqlshUrl;
    private String graphqlUrl;
    private String dataEndpointUrl;

    /**
     * Getter accessor for attribute 'id'.
     *
     * @return
     *       current value of 'id'
     */
    public String getId() {
        return id;
    }

    /**
     * Setter accessor for attribute 'id'.
     * @param id
     * 		new value for 'id '
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter accessor for attribute 'orgId'.
     *
     * @return
     *       current value of 'orgId'
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Setter accessor for attribute 'orgId'.
     * @param orgId
     * 		new value for 'orgId '
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Getter accessor for attribute 'ownerId'.
     *
     * @return
     *       current value of 'ownerId'
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Setter accessor for attribute 'ownerId'.
     * @param ownerId
     * 		new value for 'ownerId '
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Getter accessor for attribute 'info'.
     *
     * @return
     *       current value of 'info'
     */
    public DatabaseInfo getInfo() {
        return info;
    }

    /**
     * Setter accessor for attribute 'info'.
     * @param info
     * 		new value for 'info '
     */
    public void setInfo(DatabaseInfo info) {
        this.info = info;
    }

    /**
     * Getter accessor for attribute 'creationTime'.
     *
     * @return
     *       current value of 'creationTime'
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Setter accessor for attribute 'creationTime'.
     * @param creationTime
     * 		new value for 'creationTime '
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Getter accessor for attribute 'terminationTime'.
     *
     * @return
     *       current value of 'terminationTime'
     */
    public String getTerminationTime() {
        return terminationTime;
    }

    /**
     * Setter accessor for attribute 'terminationTime'.
     * @param terminationTime
     * 		new value for 'terminationTime '
     */
    public void setTerminationTime(String terminationTime) {
        this.terminationTime = terminationTime;
    }

    /**
     * Getter accessor for attribute 'cost'.
     *
     * @return
     *       current value of 'cost'
     */
    public DatabaseCost getCost() {
        return cost;
    }

    /**
     * Setter accessor for attribute 'cost'.
     * @param cost
     * 		new value for 'cost '
     */
    public void setCost(DatabaseCost cost) {
        this.cost = cost;
    }

    /**
     * Getter accessor for attribute 'studioUrl'.
     *
     * @return
     *       current value of 'studioUrl'
     */
    public String getStudioUrl() {
        return studioUrl;
    }

    /**
     * Setter accessor for attribute 'studioUrl'.
     * @param studioUrl
     * 		new value for 'studioUrl '
     */
    public void setStudioUrl(String studioUrl) {
        this.studioUrl = studioUrl;
    }

    /**
     * Getter accessor for attribute 'grafanaUrl'.
     *
     * @return
     *       current value of 'grafanaUrl'
     */
    public String getGrafanaUrl() {
        return grafanaUrl;
    }

    /**
     * Setter accessor for attribute 'grafanaUrl'.
     * @param grafanaUrl
     * 		new value for 'grafanaUrl '
     */
    public void setGrafanaUrl(String grafanaUrl) {
        this.grafanaUrl = grafanaUrl;
    }

    /**
     * Getter accessor for attribute 'cqlshUrl'.
     *
     * @return
     *       current value of 'cqlshUrl'
     */
    public String getCqlshUrl() {
        return cqlshUrl;
    }

    /**
     * Setter accessor for attribute 'cqlshUrl'.
     * @param cqlshUrl
     * 		new value for 'cqlshUrl '
     */
    public void setCqlshUrl(String cqlshUrl) {
        this.cqlshUrl = cqlshUrl;
    }

    /**
     * Getter accessor for attribute 'graphqlUrl'.
     *
     * @return
     *       current value of 'graphqlUrl'
     */
    public String getGraphqlUrl() {
        return graphqlUrl;
    }

    /**
     * Setter accessor for attribute 'graphqlUrl'.
     * @param graphqlUrl
     * 		new value for 'graphqlUrl '
     */
    public void setGraphqlUrl(String graphqlUrl) {
        this.graphqlUrl = graphqlUrl;
    }

    /**
     * Getter accessor for attribute 'dataEndpointUrl'.
     *
     * @return
     *       current value of 'dataEndpointUrl'
     */
    public String getDataEndpointUrl() {
        return dataEndpointUrl;
    }

    /**
     * Setter accessor for attribute 'dataEndpointUrl'.
     * @param dataEndpointUrl
     * 		new value for 'dataEndpointUrl '
     */
    public void setDataEndpointUrl(String dataEndpointUrl) {
        this.dataEndpointUrl = dataEndpointUrl;
    }

    /**
     * Getter accessor for attribute 'metrics'.
     *
     * @return
     *       current value of 'metrics'
     */
    public DatabaseMetrics getMetrics() {
        return metrics;
    }

    /**
     * Setter accessor for attribute 'metrics'.
     * @param metrics
     * 		new value for 'metrics '
     */
    public void setMetrics(DatabaseMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Setter accessor for attribute 'status'.
     * @param status
     * 		new value for 'status '
     */
    public void setStatus(DatabaseStatusType status) {
        this.status = status;
    }

    /**
     * Setter accessor for attribute 'storage'.
     * @param storage
     * 		new value for 'storage '
     */
    public void setStorage(DatabaseStorage storage) {
        this.storage = storage;
    }

    /**
     * Setter accessor for attribute 'availableActions'.
     * @param availableActions
     * 		new value for 'availableActions '
     */
    public void setAvailableActions(Set<String> availableActions) {
        this.availableActions = availableActions;
    }

    /**
     * Getter accessor for attribute 'status'.
     *
     * @return
     *       current value of 'status'
     */
    public DatabaseStatusType getStatus() {
        return status;
    }

    /**
     * Getter accessor for attribute 'storage'.
     *
     * @return
     *       current value of 'storage'
     */
    public DatabaseStorage getStorage() {
        return storage;
    }

    /**
     * Getter accessor for attribute 'availableActions'.
     *
     * @return
     *       current value of 'availableActions'
     */
    public Set<String> getAvailableActions() {
        return availableActions;
    }

    /**
     * Getter accessor for attribute 'lastUsageTime'.
     *
     * @return
     *       current value of 'lastUsageTime'
     */
    public String getLastUsageTime() {
        return lastUsageTime;
    }

    /**
     * Setter accessor for attribute 'lastUsageTime'.
     * @param lastUsageTime
     * 		new value for 'lastUsageTime '
     */
    public void setLastUsageTime(String lastUsageTime) {
        this.lastUsageTime = lastUsageTime;
    }

    /**
     * Getter accessor for attribute 'observedStatus'.
     *
     * @return
     *       current value of 'observedStatus'
     */
    public DatabaseStatusType getObservedStatus() {
        return observedStatus;
    }

    /**
     * Setter accessor for attribute 'observedStatus'.
     * @param observedStatus
     * 		new value for 'observedStatus '
     */
    public void setObservedStatus(DatabaseStatusType observedStatus) {
        this.observedStatus = observedStatus;
    }
       

}
