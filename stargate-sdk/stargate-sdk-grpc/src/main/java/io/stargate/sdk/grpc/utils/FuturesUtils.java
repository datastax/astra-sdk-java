package io.stargate.sdk.grpc.utils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Mapping listenable future to CompletableFuture.
 */
public class FuturesUtils {

    /**
     * Hide default constructor.
     */
    private  FuturesUtils() {}

    /**
     * Mapping to completable future.
     *
     * @param listenableFuture
     *      guava future
     * @param <T>
     *      type
     * @return
     *      java8 future
     */
    public final static <T> CompletableFuture<T> asCompletableFuture(final ListenableFuture<T> listenableFuture) {

        //create an instance of CompletableFuture
        CompletableFuture<T> completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                // propagate cancel to the listenable future
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        // add callback
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            public void onSuccess(T result) {
                completable.complete(result);
            }
            public void onFailure(Throwable t) {
                completable.completeExceptionally(t);
            }
        }, Executors.newFixedThreadPool(5));
        return completable;
    }
}
