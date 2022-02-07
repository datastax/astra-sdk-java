package com.datastax.stargate.sdk.cql;

import java.util.concurrent.CompletionStage;

import com.datastax.oss.driver.api.core.config.DriverConfig;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;

/**
 * Reload configuration.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class NonReloadableDriverConfigLoader implements DriverConfigLoader {

    /** Config Loader. */
    private final DriverConfigLoader delegate;

    /**
     * Constructor.
     *
     * @param delegate
     *      config loader
     */
    public NonReloadableDriverConfigLoader(DriverConfigLoader delegate) {
      this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public DriverConfig getInitialConfig() {
      return delegate.getInitialConfig();
    }

    /** {@inheritDoc} */
    @Override
    public void onDriverInit(DriverContext context) {
      delegate.onDriverInit(context);
    }

    /** {@inheritDoc} */
    @Override
    public CompletionStage<Boolean> reload() {
      return CompletableFutures.failedFuture(
          new UnsupportedOperationException("reload not supported"));
    }

    /** {@inheritDoc} */
    @Override
    public boolean supportsReloading() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
      delegate.close();
    }
  }