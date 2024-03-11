package com.datastax.astra.devops.streaming.domain;

/**
 * Bean to hold value for a Streaming region.
 */
public class StreamingRegion {

    /** Attribute returned by the API. */
    private String classification;

    /** Attribute returned by the API. */
    private String cloudProvider;

    /** Attribute returned by the API. */
    private String displayName;

    /** Attribute returned by the API. */
    private boolean enabled;

    /** Attribute returned by the API. */
    private String name;

    /** Attribute returned by the API. */
    private String zone;

    /**
     * Default constructor for introspection.
     */
    public StreamingRegion() {
    }

    /**
     * Full fledged constructor.
     * @param classification
     *      classification
     * @param cloudProvider
     *      cloudProvider
     * @param displayName
     *      displayName
     * @param enabled
     *      enabled
     * @param name
     *      name
     * @param zone
     *      zone
     */
    public StreamingRegion(String classification, String cloudProvider, String displayName, boolean enabled, String name, String zone) {
        this.classification = classification;
        this.cloudProvider = cloudProvider;
        this.displayName = displayName;
        this.enabled = enabled;
        this.name = name;
        this.zone = zone;
    }

    /**
     * Getter.
     *
     * @return
     *      classification value
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Setter for classification.
     *
     * @param classification
     *      classification value
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * Getter.
     *
     * @return
     *      cloud provider value
     */
    public String getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Setter for cloudProvider.
     *
     * @param cloudProvider
     *      cloud provider value
     */
    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    /**
     * Getter.
     *
     * @return
     *      display name value
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter for displayName.
     *
     * @param displayName
     *      displayName value
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter.
     *
     * @return
     *      enable value
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Setter for enabled.
     *
     * @param enabled
     *      enabled value
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Getter.
     *
     * @return
     *      name value
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     *
     * @param name
     *      name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter.
     *
     * @return
     *      zone value
     */
    public String getZone() {
        return zone;
    }

    /**
     * Setter for name.
     *
     * @param zone
     *      zone value
     */
    public void setZone(String zone) {
        this.zone = zone;
    }
}
