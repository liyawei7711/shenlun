package com.example.myapplication;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class CommonSubscriber<T> implements Observer<T> {
    @Override
    public void onNext(T o) {

    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
