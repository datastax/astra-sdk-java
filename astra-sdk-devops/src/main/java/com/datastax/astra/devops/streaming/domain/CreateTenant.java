package com.datastax.astra.devops.streaming.domain;

/**
 * Tenant Request Creation.
 */
public class CreateTenant {

    /** cloud provider. */
    private String cloudProvider = "aws";

    /** cloud region. */
    private String cloudRegion = "useast2";

    /** pan. */
    private String plan = "free";

    /** tenant name. */
    private String tenantName;

    /** user email. */
    private String userEmail;

    /** cluster name for DEDICATED clusters. */
    private String clusterName;

    /**
     * Default constructor.
     */
    private CreateTenant() {}

    /**
     * Work with builder.
     * @return
     *      builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder
     */
    public static class Builder {

        /** cloud provider. */
        private String cloudProvider = "aws";

        /** cloud region. */
        private String cloudRegion = "useast2";

        /** pan. */
        private String plan = "free";

        /** tenant name. */
        private String tenantName;

        /** user email. */
        private String userEmail;

        /** cluster name for DEDICATED clusters. */
        private String clusterName;

        /** default/ */
        public Builder() {}

        /**
         * Builder.
         *
         * @param tenantName
         *     current param.
         * @return
         *      current reference.
         */
        public Builder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        /**
         * Builder.
         *
         * @param email
         *     current param.
         * @return
         *      current reference.
         */
        public Builder userEmail(String email) {
            this.userEmail = email;
            return this;
        }

        /**
         * Builder.
         *
         * @param cloudProvider
         *     current param.
         * @return
         *      current reference.
         */
        public Builder cloudProvider(String cloudProvider) {
            this.cloudProvider = cloudProvider;
            return this;
        }

        /**
         * Builder.
         *
         * @param cloudRegion
         *     current param.
         * @return
         *      current reference.
         */
        public Builder cloudRegion(String cloudRegion) {
            this.cloudRegion = cloudRegion;
            return this;
        }

        /**
         * Builder.
         *
         * @param plan
         *     current param.
         * @return
         *      current reference.
         */
        public Builder plan(String plan) {
            this.plan = plan;
            return this;
        }

        /**
         * Builder.
         *
         * @param clusterName
         *     current param.
         * @return
         *      current reference.
         */
        public Builder clusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        /**
         * Builder.
         *
         * @return
         *      target object
         */
        public CreateTenant build() {
            CreateTenant tenant = new CreateTenant();
            tenant.cloudProvider = this.cloudProvider;
            tenant.cloudRegion = this.cloudRegion;
            tenant.plan = this.plan;
            tenant.tenantName = this.tenantName;
            tenant.userEmail = this.userEmail;
            tenant.clusterName = this.clusterName;
            return tenant;
        }
    }

    /**
     * Gets cloudProvider
     *
     * @return value of cloudProvider
     */
    public String getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Gets cloudRegion
     *
     * @return value of cloudRegion
     */
    public String getCloudRegion() {
        return cloudRegion;
    }

    /**
     * Gets plan
     *
     * @return value of plan
     */
    public String getPlan() {
        return plan;
    }

    /**
     * Gets tenantName
     *
     * @return value of tenantName
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * Gets userEmail
     *
     * @return value of userEmail
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Gets clusterName
     *
     * @return value of clusterName
     */
    public String getClusterName() {
        return clusterName;
    }
}
