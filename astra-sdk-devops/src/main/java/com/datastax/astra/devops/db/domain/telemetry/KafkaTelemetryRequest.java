package com.datastax.astra.devops.db.domain.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Pojo to setup Telemetry with Astra and Kafka.
 */
public class KafkaTelemetryRequest {

    /** BootStrapServers. */
    @JsonProperty("bootstrap_servers")
    private Set<String> bootstrapServers;

    /** Json. */
    private String topic;

    /** Json. */
    @JsonProperty("sasl_mechanism")
    private String saslMechanism;

    /** Json. */
    @JsonProperty("sasl_username")
    private String saslUsername;

    /** Json. */
    @JsonProperty("sasl_password")
    private String saslPassword;

    /** Json. */
    private String security_protocol = "SASL_PLAINTEXT";

    /**
     * Default Constructor.
     */
    public KafkaTelemetryRequest() {}

    /**
     * Full Constructor.
     *
     * @param bootstrapServers
     *      bootstraps
     * @param topic
     *      topic
     * @param sasl_mechanism
     *      mechanism
     * @param sasl_username
     *      username
     * @param sasl_password
     *      password
     * @param security_protocol
     *      protocol
     */
    public KafkaTelemetryRequest(Set<String> bootstrapServers, String topic, String sasl_mechanism, String sasl_username, String sasl_password, String security_protocol) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.saslMechanism = sasl_mechanism;
        this.saslUsername = sasl_username;
        this.saslPassword = sasl_password;
        this.security_protocol = security_protocol;
    }

    /**
     * Set value for saslMechanism
     *
     * @param saslMechanism new value for saslMechanism
     */
    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    /**
     * Set value for saslUsername
     *
     * @param saslUsername new value for saslUsername
     */
    public void setSaslUsername(String saslUsername) {
        this.saslUsername = saslUsername;
    }

    /**
     * Set value for saslPassword
     *
     * @param saslPassword new value for saslPassword
     */
    public void setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
    }

    /**
     * Gets saslMechanism
     *
     * @return value of saslMechanism
     */
    public String getSaslMechanism() {
        return saslMechanism;
    }

    /**
     * Gets saslUsername
     *
     * @return value of saslUsername
     */
    public String getSaslUsername() {
        return saslUsername;
    }

    /**
     * Gets saslPassword
     *
     * @return value of saslPassword
     */
    public String getSaslPassword() {
        return saslPassword;
    }

    /**
     * Set value for bootstrapServers
     *
     * @param bootstrapServers new value for bootstrapServers
     */
    public void setBootstrapServers(Set<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Set value for topic
     *
     * @param topic new value for topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Set value for security_protocol
     *
     * @param security_protocol new value for security_protocol
     */
    public void setSecurity_protocol(String security_protocol) {
        this.security_protocol = security_protocol;
    }

    /**
     * Gets bootstrapServers
     *
     * @return value of bootstrapServers
     */
    public Set<String> getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * Gets topic
     *
     * @return value of topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets security_protocol
     *
     * @return value of security_protocol
     */
    public String getSecurity_protocol() {
        return security_protocol;
    }
}
