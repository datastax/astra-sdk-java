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

/** 
 * Hold bean for reference cost.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseCost {
    
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
    double costPerWrittenGbCents   = 0;
    double costPerReadGbCents      = 0;

    /**
     * Default constructor.
     */
    public DatabaseCost() {}

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
     *      new value for 'costPerMinCents '
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
     *      new value for 'costPerHourCents '
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
     *      new value for 'costPerDayCents '
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
     *      new value for 'costPerMonthCents '
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
     *      new value for 'costPerMinMRCents '
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
     *      new value for 'costPerHourMRCents '
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
     *      new value for 'costPerDayMRCents '
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
     *      new value for 'costPerMonthMRCents '
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
     *      new value for 'costPerMinParkedCents '
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
     *      new value for 'costPerHourParkedCents '
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
     *      new value for 'costPerDayParkedCents '
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
     *      new value for 'costPerMonthParkedCents '
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
     *      new value for 'costPerNetworkGbCents '
     */
    public void setCostPerNetworkGbCents(double costPerNetworkGbCents) {
        this.costPerNetworkGbCents = costPerNetworkGbCents;
    }
    /**
     * Getter accessor for attribute 'costPerWrittenGbCents'.
     *
     * @return
     *       current value of 'costPerWrittenGbCents'
     */
    public double getCostPerWrittenGbCents() {
        return costPerWrittenGbCents;
    }
    /**
     * Setter accessor for attribute 'costPerWrittenGbCents'.
     * @param costPerWrittenGbCents
     * 		new value for 'costPerWrittenGbCents '
     */
    public void setCostPerWrittenGbCents(double costPerWrittenGbCents) {
        this.costPerWrittenGbCents = costPerWrittenGbCents;
    }
    /**
     * Getter accessor for attribute 'costPerReadGbCents'.
     *
     * @return
     *       current value of 'costPerReadGbCents'
     */
    public double getCostPerReadGbCents() {
        return costPerReadGbCents;
    }
    /**
     * Setter accessor for attribute 'costPerReadGbCents'.
     * @param costPerReadGbCents
     * 		new value for 'costPerReadGbCents '
     */
    public void setCostPerReadGbCents(double costPerReadGbCents) {
        this.costPerReadGbCents = costPerReadGbCents;
    }
}
