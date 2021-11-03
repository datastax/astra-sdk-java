package com.datastax.stargate.sdk.config;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;

/**
 * You may want to customize the CqlSession exactly as you like.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@FunctionalInterface
public interface CqlSessionBuilderCustomizer {

    /**
     * Customize the {@link CqlSessionBuilder}.
     * @param cqlSessionBuilder the builder to customize
     */
    void customize(CqlSessionBuilder cqlSessionBuilder);

}