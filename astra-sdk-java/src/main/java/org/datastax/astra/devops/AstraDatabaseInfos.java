package org.datastax.astra.devops;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class AstraDatabaseInfos {
    
    String id;
    String orgId;
    String ownerId;
    Info   info;
    String creationTime;
    
    String terminationTime;
    String status;
    Storage storage;
    Cost cost;
    Set<String> availableActions;
    String studioUrl;
    String grafanaUrl;
    String cqlshUrl;
    String graphqlUrl;
    String dataEndpointUrl;
    
    
    public static  class Storage {
        int nodeCount;
        int replicationFactor;
        int totalStorage;
        /**
         * Getter accessor for attribute 'nodeCount'.
         *
         * @return
         *       current value of 'nodeCount'
         */
        public int getNodeCount() {
            return nodeCount;
        }
        /**
         * Setter accessor for attribute 'nodeCount'.
         * @param nodeCount
         * 		new value for 'nodeCount '
         */
        public void setNodeCount(int nodeCount) {
            this.nodeCount = nodeCount;
        }
        /**
         * Getter accessor for attribute 'replicationFactor'.
         *
         * @return
         *       current value of 'replicationFactor'
         */
        public int getReplicationFactor() {
            return replicationFactor;
        }
        /**
         * Setter accessor for attribute 'replicationFactor'.
         * @param replicationFactor
         * 		new value for 'replicationFactor '
         */
        public void setReplicationFactor(int replicationFactor) {
            this.replicationFactor = replicationFactor;
        }
        /**
         * Getter accessor for attribute 'totalStorage'.
         *
         * @return
         *       current value of 'totalStorage'
         */
        public int getTotalStorage() {
            return totalStorage;
        }
        /**
         * Setter accessor for attribute 'totalStorage'.
         * @param totalStorage
         * 		new value for 'totalStorage '
         */
        public void setTotalStorage(int totalStorage) {
            this.totalStorage = totalStorage;
        }
    }
    
    public static class Cost {
        double costPerMinCents         = 0;
        double costPerHourCents        = 0;
        double costPerDayCents         = 0;
        double costPerMonthCents       = 0;
        double costPerMinMRCents       = 0;
        double costPerHourMRCents      = 0;
        double costPerDayMRCents       = 0;
        double costPerMonthMRCents     = 0;
        double costPerMinParkedCents   = 0;
        double costPerHourParkedCents  = 0;
        double costPerDayParkedCents   = 0;
        double costPerMonthParkedCents = 0;
        double costPerNetworkGbCents   = 0;
        /**
         * Getter accessor for attribute 'costPerMinCents'.
         *
         * @return
         *       current value of 'costPerMinCents'
         */
        public double getCostPerMinCents() {
            return costPerMinCents;
        }
        /**
         * Setter accessor for attribute 'costPerMinCents'.
         * @param costPerMinCents
         * 		new value for 'costPerMinCents '
         */
        public void setCostPerMinCents(double costPerMinCents) {
            this.costPerMinCents = costPerMinCents;
        }
        /**
         * Getter accessor for attribute 'costPerHourCents'.
         *
         * @return
         *       current value of 'costPerHourCents'
         */
        public double getCostPerHourCents() {
            return costPerHourCents;
        }
        /**
         * Setter accessor for attribute 'costPerHourCents'.
         * @param costPerHourCents
         * 		new value for 'costPerHourCents '
         */
        public void setCostPerHourCents(double costPerHourCents) {
            this.costPerHourCents = costPerHourCents;
        }
        /**
         * Getter accessor for attribute 'costPerDayCents'.
         *
         * @return
         *       current value of 'costPerDayCents'
         */
        public double getCostPerDayCents() {
            return costPerDayCents;
        }
        /**
         * Setter accessor for attribute 'costPerDayCents'.
         * @param costPerDayCents
         * 		new value for 'costPerDayCents '
         */
        public void setCostPerDayCents(double costPerDayCents) {
            this.costPerDayCents = costPerDayCents;
        }
        /**
         * Getter accessor for attribute 'costPerMonthCents'.
         *
         * @return
         *       current value of 'costPerMonthCents'
         */
        public double getCostPerMonthCents() {
            return costPerMonthCents;
        }
        /**
         * Setter accessor for attribute 'costPerMonthCents'.
         * @param costPerMonthCents
         * 		new value for 'costPerMonthCents '
         */
        public void setCostPerMonthCents(double costPerMonthCents) {
            this.costPerMonthCents = costPerMonthCents;
        }
        /**
         * Getter accessor for attribute 'costPerMinMRCents'.
         *
         * @return
         *       current value of 'costPerMinMRCents'
         */
        public double getCostPerMinMRCents() {
            return costPerMinMRCents;
        }
        /**
         * Setter accessor for attribute 'costPerMinMRCents'.
         * @param costPerMinMRCents
         * 		new value for 'costPerMinMRCents '
         */
        public void setCostPerMinMRCents(double costPerMinMRCents) {
            this.costPerMinMRCents = costPerMinMRCents;
        }
        /**
         * Getter accessor for attribute 'costPerHourMRCents'.
         *
         * @return
         *       current value of 'costPerHourMRCents'
         */
        public double getCostPerHourMRCents() {
            return costPerHourMRCents;
        }
        /**
         * Setter accessor for attribute 'costPerHourMRCents'.
         * @param costPerHourMRCents
         * 		new value for 'costPerHourMRCents '
         */
        public void setCostPerHourMRCents(double costPerHourMRCents) {
            this.costPerHourMRCents = costPerHourMRCents;
        }
        /**
         * Getter accessor for attribute 'costPerDayMRCents'.
         *
         * @return
         *       current value of 'costPerDayMRCents'
         */
        public double getCostPerDayMRCents() {
            return costPerDayMRCents;
        }
        /**
         * Setter accessor for attribute 'costPerDayMRCents'.
         * @param costPerDayMRCents
         * 		new value for 'costPerDayMRCents '
         */
        public void setCostPerDayMRCents(double costPerDayMRCents) {
            this.costPerDayMRCents = costPerDayMRCents;
        }
        /**
         * Getter accessor for attribute 'costPerMonthMRCents'.
         *
         * @return
         *       current value of 'costPerMonthMRCents'
         */
        public double getCostPerMonthMRCents() {
            return costPerMonthMRCents;
        }
        /**
         * Setter accessor for attribute 'costPerMonthMRCents'.
         * @param costPerMonthMRCents
         * 		new value for 'costPerMonthMRCents '
         */
        public void setCostPerMonthMRCents(double costPerMonthMRCents) {
            this.costPerMonthMRCents = costPerMonthMRCents;
        }
        /**
         * Getter accessor for attribute 'costPerMinParkedCents'.
         *
         * @return
         *       current value of 'costPerMinParkedCents'
         */
        public double getCostPerMinParkedCents() {
            return costPerMinParkedCents;
        }
        /**
         * Setter accessor for attribute 'costPerMinParkedCents'.
         * @param costPerMinParkedCents
         * 		new value for 'costPerMinParkedCents '
         */
        public void setCostPerMinParkedCents(double costPerMinParkedCents) {
            this.costPerMinParkedCents = costPerMinParkedCents;
        }
        /**
         * Getter accessor for attribute 'costPerHourParkedCents'.
         *
         * @return
         *       current value of 'costPerHourParkedCents'
         */
        public double getCostPerHourParkedCents() {
            return costPerHourParkedCents;
        }
        /**
         * Setter accessor for attribute 'costPerHourParkedCents'.
         * @param costPerHourParkedCents
         * 		new value for 'costPerHourParkedCents '
         */
        public void setCostPerHourParkedCents(double costPerHourParkedCents) {
            this.costPerHourParkedCents = costPerHourParkedCents;
        }
        /**
         * Getter accessor for attribute 'costPerDayParkedCents'.
         *
         * @return
         *       current value of 'costPerDayParkedCents'
         */
        public double getCostPerDayParkedCents() {
            return costPerDayParkedCents;
        }
        /**
         * Setter accessor for attribute 'costPerDayParkedCents'.
         * @param costPerDayParkedCents
         * 		new value for 'costPerDayParkedCents '
         */
        public void setCostPerDayParkedCents(double costPerDayParkedCents) {
            this.costPerDayParkedCents = costPerDayParkedCents;
        }
        /**
         * Getter accessor for attribute 'costPerMonthParkedCents'.
         *
         * @return
         *       current value of 'costPerMonthParkedCents'
         */
        public double getCostPerMonthParkedCents() {
            return costPerMonthParkedCents;
        }
        /**
         * Setter accessor for attribute 'costPerMonthParkedCents'.
         * @param costPerMonthParkedCents
         * 		new value for 'costPerMonthParkedCents '
         */
        public void setCostPerMonthParkedCents(double costPerMonthParkedCents) {
            this.costPerMonthParkedCents = costPerMonthParkedCents;
        }
        /**
         * Getter accessor for attribute 'costPerNetworkGbCents'.
         *
         * @return
         *       current value of 'costPerNetworkGbCents'
         */
        public double getCostPerNetworkGbCents() {
            return costPerNetworkGbCents;
        }
        /**
         * Setter accessor for attribute 'costPerNetworkGbCents'.
         * @param costPerNetworkGbCents
         * 		new value for 'costPerNetworkGbCents '
         */
        public void setCostPerNetworkGbCents(double costPerNetworkGbCents) {
            this.costPerNetworkGbCents = costPerNetworkGbCents;
        }
    } 
    
    public static class Info {
        String name;
        Set<String> keyspaces;
        Set<Datacenter> datacenters;
        String user;
        String keyspace;
        String cloudPriver;
        String tier;
        int capacityUnits;
        String region;
        Set<String> additionalKeyspaces;
        /**
         * Getter accessor for attribute 'name'.
         *
         * @return
         *       current value of 'name'
         */
        public String getName() {
            return name;
        }
        /**
         * Setter accessor for attribute 'name'.
         * @param name
         * 		new value for 'name '
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * Getter accessor for attribute 'keyspaces'.
         *
         * @return
         *       current value of 'keyspaces'
         */
        public Set<String> getKeyspaces() {
            return keyspaces;
        }
        /**
         * Setter accessor for attribute 'keyspaces'.
         * @param keyspaces
         * 		new value for 'keyspaces '
         */
        public void setKeyspaces(Set<String> keyspaces) {
            this.keyspaces = keyspaces;
        }
        /**
         * Getter accessor for attribute 'datacenters'.
         *
         * @return
         *       current value of 'datacenters'
         */
        public Set<Datacenter> getDatacenters() {
            return datacenters;
        }
        /**
         * Setter accessor for attribute 'datacenters'.
         * @param datacenters
         * 		new value for 'datacenters '
         */
        public void setDatacenters(Set<Datacenter> datacenters) {
            this.datacenters = datacenters;
        }
        /**
         * Getter accessor for attribute 'user'.
         *
         * @return
         *       current value of 'user'
         */
        public String getUser() {
            return user;
        }
        /**
         * Setter accessor for attribute 'user'.
         * @param user
         * 		new value for 'user '
         */
        public void setUser(String user) {
            this.user = user;
        }
        /**
         * Getter accessor for attribute 'keyspace'.
         *
         * @return
         *       current value of 'keyspace'
         */
        public String getKeyspace() {
            return keyspace;
        }
        /**
         * Setter accessor for attribute 'keyspace'.
         * @param keyspace
         * 		new value for 'keyspace '
         */
        public void setKeyspace(String keyspace) {
            this.keyspace = keyspace;
        }
        /**
         * Getter accessor for attribute 'cloudPriver'.
         *
         * @return
         *       current value of 'cloudPriver'
         */
        public String getCloudPriver() {
            return cloudPriver;
        }
        /**
         * Setter accessor for attribute 'cloudPriver'.
         * @param cloudPriver
         * 		new value for 'cloudPriver '
         */
        public void setCloudPriver(String cloudPriver) {
            this.cloudPriver = cloudPriver;
        }
        /**
         * Getter accessor for attribute 'tier'.
         *
         * @return
         *       current value of 'tier'
         */
        public String getTier() {
            return tier;
        }
        /**
         * Setter accessor for attribute 'tier'.
         * @param tier
         * 		new value for 'tier '
         */
        public void setTier(String tier) {
            this.tier = tier;
        }
        /**
         * Getter accessor for attribute 'capacityUnits'.
         *
         * @return
         *       current value of 'capacityUnits'
         */
        public int getCapacityUnits() {
            return capacityUnits;
        }
        /**
         * Setter accessor for attribute 'capacityUnits'.
         * @param capacityUnits
         * 		new value for 'capacityUnits '
         */
        public void setCapacityUnits(int capacityUnits) {
            this.capacityUnits = capacityUnits;
        }
        /**
         * Getter accessor for attribute 'region'.
         *
         * @return
         *       current value of 'region'
         */
        public String getRegion() {
            return region;
        }
        /**
         * Setter accessor for attribute 'region'.
         * @param region
         * 		new value for 'region '
         */
        public void setRegion(String region) {
            this.region = region;
        }
        /**
         * Getter accessor for attribute 'additionalKeyspaces'.
         *
         * @return
         *       current value of 'additionalKeyspaces'
         */
        public Set<String> getAdditionalKeyspaces() {
            return additionalKeyspaces;
        }
        /**
         * Setter accessor for attribute 'additionalKeyspaces'.
         * @param additionalKeyspaces
         * 		new value for 'additionalKeyspaces '
         */
        public void setAdditionalKeyspaces(Set<String> additionalKeyspaces) {
            this.additionalKeyspaces = additionalKeyspaces;
        }
        
    }
    
    public static class Datacenter {
        String id;
        String name;
        String tier;
        String cloudProvider;
        String region;
        int capacityUnits;
        String secureBundleUrl;
        String secureBundleInternalUrl;
        String secureBundleMigrationProxyUrl;
        String secureBundleMigrationProxyInternalUrl;
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
         * Getter accessor for attribute 'name'.
         *
         * @return
         *       current value of 'name'
         */
        public String getName() {
            return name;
        }
        /**
         * Setter accessor for attribute 'name'.
         * @param name
         * 		new value for 'name '
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * Getter accessor for attribute 'tier'.
         *
         * @return
         *       current value of 'tier'
         */
        public String getTier() {
            return tier;
        }
        /**
         * Setter accessor for attribute 'tier'.
         * @param tier
         * 		new value for 'tier '
         */
        public void setTier(String tier) {
            this.tier = tier;
        }
        /**
         * Getter accessor for attribute 'cloudProvider'.
         *
         * @return
         *       current value of 'cloudProvider'
         */
        public String getCloudProvider() {
            return cloudProvider;
        }
        /**
         * Setter accessor for attribute 'cloudProvider'.
         * @param cloudProvider
         * 		new value for 'cloudProvider '
         */
        public void setCloudProvider(String cloudProvider) {
            this.cloudProvider = cloudProvider;
        }
        /**
         * Getter accessor for attribute 'region'.
         *
         * @return
         *       current value of 'region'
         */
        public String getRegion() {
            return region;
        }
        /**
         * Setter accessor for attribute 'region'.
         * @param region
         * 		new value for 'region '
         */
        public void setRegion(String region) {
            this.region = region;
        }
        /**
         * Getter accessor for attribute 'capacityUnits'.
         *
         * @return
         *       current value of 'capacityUnits'
         */
        public int getCapacityUnits() {
            return capacityUnits;
        }
        /**
         * Setter accessor for attribute 'capacityUnits'.
         * @param capacityUnits
         * 		new value for 'capacityUnits '
         */
        public void setCapacityUnits(int capacityUnits) {
            this.capacityUnits = capacityUnits;
        }
        /**
         * Getter accessor for attribute 'secureBundleUrl'.
         *
         * @return
         *       current value of 'secureBundleUrl'
         */
        public String getSecureBundleUrl() {
            return secureBundleUrl;
        }
        /**
         * Setter accessor for attribute 'secureBundleUrl'.
         * @param secureBundleUrl
         * 		new value for 'secureBundleUrl '
         */
        public void setSecureBundleUrl(String secureBundleUrl) {
            this.secureBundleUrl = secureBundleUrl;
        }
        /**
         * Getter accessor for attribute 'secureBundleInternalUrl'.
         *
         * @return
         *       current value of 'secureBundleInternalUrl'
         */
        public String getSecureBundleInternalUrl() {
            return secureBundleInternalUrl;
        }
        /**
         * Setter accessor for attribute 'secureBundleInternalUrl'.
         * @param secureBundleInternalUrl
         * 		new value for 'secureBundleInternalUrl '
         */
        public void setSecureBundleInternalUrl(String secureBundleInternalUrl) {
            this.secureBundleInternalUrl = secureBundleInternalUrl;
        }
        /**
         * Getter accessor for attribute 'secureBundleMigrationProxyUrl'.
         *
         * @return
         *       current value of 'secureBundleMigrationProxyUrl'
         */
        public String getSecureBundleMigrationProxyUrl() {
            return secureBundleMigrationProxyUrl;
        }
        /**
         * Setter accessor for attribute 'secureBundleMigrationProxyUrl'.
         * @param secureBundleMigrationProxyUrl
         * 		new value for 'secureBundleMigrationProxyUrl '
         */
        public void setSecureBundleMigrationProxyUrl(String secureBundleMigrationProxyUrl) {
            this.secureBundleMigrationProxyUrl = secureBundleMigrationProxyUrl;
        }
        /**
         * Getter accessor for attribute 'secureBundleMigrationProxyInternalUrl'.
         *
         * @return
         *       current value of 'secureBundleMigrationProxyInternalUrl'
         */
        public String getSecureBundleMigrationProxyInternalUrl() {
            return secureBundleMigrationProxyInternalUrl;
        }
        /**
         * Setter accessor for attribute 'secureBundleMigrationProxyInternalUrl'.
         * @param secureBundleMigrationProxyInternalUrl
         * 		new value for 'secureBundleMigrationProxyInternalUrl '
         */
        public void setSecureBundleMigrationProxyInternalUrl(String secureBundleMigrationProxyInternalUrl) {
            this.secureBundleMigrationProxyInternalUrl = secureBundleMigrationProxyInternalUrl;
        }
        
    }

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
    public Info getInfo() {
        return info;
    }

    /**
     * Setter accessor for attribute 'info'.
     * @param info
     * 		new value for 'info '
     */
    public void setInfo(Info info) {
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
     * Getter accessor for attribute 'storage'.
     *
     * @return
     *       current value of 'storage'
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Setter accessor for attribute 'storage'.
     * @param storage
     * 		new value for 'storage '
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Getter accessor for attribute 'cost'.
     *
     * @return
     *       current value of 'cost'
     */
    public Cost getCost() {
        return cost;
    }

    /**
     * Setter accessor for attribute 'cost'.
     * @param cost
     * 		new value for 'cost '
     */
    public void setCost(Cost cost) {
        this.cost = cost;
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
     * Setter accessor for attribute 'availableActions'.
     * @param availableActions
     * 		new value for 'availableActions '
     */
    public void setAvailableActions(Set<String> availableActions) {
        this.availableActions = availableActions;
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
       

}
