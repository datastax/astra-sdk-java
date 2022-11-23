package io.stargate.sdk.grpc.utils;

import io.grpc.stub.StreamObserver;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;

/**
 * Mapping Grpc StreamObserver to Flux.
 *
 * @param <T>
 *     current param.
 */
public class StreamObserverToReactivePublisher<T> implements Publisher<T>, StreamObserver<T> {

    /** subscriber. */
    private Subscriber<? super T> subscriber;

    @Override
    /** @{inheritDocs} */
    public void onNext(T l) {
        subscriber.onNext(l);
    }

    @Override
    /** @{inheritDocs} */
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    /** @{inheritDocs} */
    public void onCompleted() {
        subscriber.onComplete();
    }

    @Override
    /** @{inheritDocs} */
    public void subscribe(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(new BaseSubscriber() {});
    }
}
