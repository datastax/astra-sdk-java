package com.datastax.tutorial;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import com.datastax.oss.driver.api.core.loadbalancing.LoadBalancingPolicy;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.session.Session;

public class AstraCrossRegionLoadBalancingPolicy implements LoadBalancingPolicy {

    /** {@inheritDoc} */
    @Override
    public void init(Map<UUID, Node> nodes, DistanceReporter distanceReporter) {
    }

    /** {@inheritDoc} */
    @Override
    public Queue<Node> newQueryPlan(Request request, Session session) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void onAdd(Node node) {
    }

    /** {@inheritDoc} */
    @Override
    public void onUp(Node node) {
    }

    /** {@inheritDoc} */
    @Override
    public void onDown(Node node) {
    }

    /** {@inheritDoc} */
    @Override
    public void onRemove(Node node) {
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
    }

}
