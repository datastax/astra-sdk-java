package com.datastax.stargate.sdk.loadbalancer;

import java.util.Date;
import java.util.UUID;


/**
 * Bean repr&eacute;sentant un wrapper pour un envoi de mail.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <RSC>
 *      resource to be monitored
 */
public class LoadBalancingResource < RSC > implements Comparable < LoadBalancingResource < RSC > > {

    /** Give the resource a name **/
    private String id;

    /** Default weight. **/
    private double defaultWeigth;

    /** Current weight coputed each time. **/
    private double currentWeight;

    /** Current invocation count **/
    private double nbUse;

    /** Sorting resource with priority. **/
    private int priority;

    /** Check if available. **/
    private boolean available;

    /** Unavailability cause. **/
    private String unavailabilityCause;

    /** Unavailability error. **/
    private Throwable unavailabilityError;

    /** Unavailability date. **/
    private Date unavailabilityTriggerDate;
  
    /** Target resource. */
    private RSC resource;
    
   /**
    * Load Balancing resource.
    *
    * @param resource
    *      current resource
    */
    public LoadBalancingResource(RSC resource) {
        this(UUID.randomUUID().toString(), 0, resource);
    }
    
   /**
    * Load Balancing resource.
    *
    * @param id
    *      identifier
    * @param resource
    *      current resource
    */
    public LoadBalancingResource(String id, RSC resource) {
        this(id, 0, resource);
    }
   
   /**
    * Load Balancing resource.
    *
    * @param id
    *      identifier
    * @param defaultWeigth
    *      current weight
    * @param resource
    *      current resource
    */
    public LoadBalancingResource(String id, double defaultWeigth, RSC resource) {
        this.id            = id;
        this.defaultWeigth = defaultWeigth;
        this.resource      = resource;
    } 

    /** {@inheritDoc} **/
    public final int compareTo(final LoadBalancingResource < RSC > o) {
        // Du plus grand au plus petit et toujours unavailable en premier (sinon plus utilis�)
        int exitValue = 0;
        if (o.isAvailable() == available) {
            exitValue = (o.getPriority() - priority);
        } else if (o.isAvailable()) {
            // Je suis 'invalide' je passe en premier
            exitValue = -1;
        } else {
            exitValue = 1;
        }
        return exitValue;
    }

    /** {@inheritDoc} **/
    @Override
    public final String toString() {
        // Affichage de l'�tat du Composant
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(this.id);
        strBuild.append("(" + Double.valueOf(this.defaultWeigth).intValue() + "%)");
        if (isAvailable()) {
            strBuild.append(" currentweight " + Double.valueOf(this.currentWeight).intValue() + "% " + resource.toString());
        } else {
            strBuild.append(" ---");
        }
        return strBuild.toString();
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
     * Getter accessor for attribute 'defaultWeigth'.
     *
     * @return
     *       current value of 'defaultWeigth'
     */
    public double getDefaultWeigth() {
        return defaultWeigth;
    }

    /**
     * Setter accessor for attribute 'defaultWeigth'.
     * @param defaultWeigth
     * 		new value for 'defaultWeigth '
     */
    public void setDefaultWeigth(double defaultWeigth) {
        this.defaultWeigth = defaultWeigth;
    }

    /**
     * Getter accessor for attribute 'currentWeight'.
     *
     * @return
     *       current value of 'currentWeight'
     */
    public double getCurrentWeight() {
        return currentWeight;
    }

    /**
     * Setter accessor for attribute 'currentWeight'.
     * @param currentWeight
     * 		new value for 'currentWeight '
     */
    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    /**
     * Getter accessor for attribute 'nbUse'.
     *
     * @return
     *       current value of 'nbUse'
     */
    public double getNbUse() {
        return nbUse;
    }

    /**
     * Setter accessor for attribute 'nbUse'.
     * @param nbUse
     * 		new value for 'nbUse '
     */
    public void setNbUse(double nbUse) {
        this.nbUse = nbUse;
    }

    /**
     * Getter accessor for attribute 'priority'.
     *
     * @return
     *       current value of 'priority'
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Setter accessor for attribute 'priority'.
     * @param priority
     * 		new value for 'priority '
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Getter accessor for attribute 'available'.
     *
     * @return
     *       current value of 'available'
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Setter accessor for attribute 'available'.
     * @param available
     * 		new value for 'available '
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Getter accessor for attribute 'unavailabilityCause'.
     *
     * @return
     *       current value of 'unavailabilityCause'
     */
    public String getUnavailabilityCause() {
        return unavailabilityCause;
    }

    /**
     * Setter accessor for attribute 'unavailabilityCause'.
     * @param unavailabilityCause
     * 		new value for 'unavailabilityCause '
     */
    public void setUnavailabilityCause(String unavailabilityCause) {
        this.unavailabilityCause = unavailabilityCause;
    }

    /**
     * Getter accessor for attribute 'unavailabilityError'.
     *
     * @return
     *       current value of 'unavailabilityError'
     */
    public Throwable getUnavailabilityError() {
        return unavailabilityError;
    }

    /**
     * Setter accessor for attribute 'unavailabilityError'.
     * @param unavailabilityError
     * 		new value for 'unavailabilityError '
     */
    public void setUnavailabilityError(Throwable unavailabilityError) {
        this.unavailabilityError = unavailabilityError;
    }

    /**
     * Getter accessor for attribute 'unavailabilityTriggerDate'.
     *
     * @return
     *       current value of 'unavailabilityTriggerDate'
     */
    public Date getUnavailabilityTriggerDate() {
        return unavailabilityTriggerDate;
    }

    /**
     * Setter accessor for attribute 'unavailabilityTriggerDate'.
     * @param unavailabilityTriggerDate
     * 		new value for 'unavailabilityTriggerDate '
     */
    public void setUnavailabilityTriggerDate(Date unavailabilityTriggerDate) {
        this.unavailabilityTriggerDate = unavailabilityTriggerDate;
    }

    /**
     * Getter accessor for attribute 'wrappee'.
     *
     * @return
     *       current value of 'wrappee'
     */
    public RSC getResource() {
        return resource;
    }

    /**
     * Setter accessor for attribute 'wrappee'.
     * @param wrappee
     * 		new value for 'wrappee '
     */
    public void setResource(RSC wrappee) {
        this.resource = wrappee;
    }
    
}
