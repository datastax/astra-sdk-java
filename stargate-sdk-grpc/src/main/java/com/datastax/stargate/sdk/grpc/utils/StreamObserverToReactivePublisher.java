package com.datastax.stargate.sdk.grpc.utils;

import io.grpc.stub.StreamObserver;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;

public class StreamObserverToReactivePublisher<T> implements Publisher<T>, StreamObserver<T> {

    private Subscriber<? super T> subscriber;

    @Override
    public void onNext(T l) {
        subscriber.onNext(l);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onCompleted() {
        subscriber.onComplete();
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(new BaseSubscriber() {});
    }
}
