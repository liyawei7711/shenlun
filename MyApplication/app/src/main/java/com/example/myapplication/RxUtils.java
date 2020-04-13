package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: admin
 * date: 2018/04/17
 * version: 0
 * mail: secret
 * desc: RxUtils
 */

public class RxUtils<T> {

    public interface IThreadAndMainDeal<T> {
        T doOnThread();

        void doOnMain(T data);
    }

    public interface IMainDelay {

        void onMainDelay();
    }

    public void doOnThreadObMain(final IThreadAndMainDeal iThreadAndMainDeal) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                if (iThreadAndMainDeal != null) {

                    T data = (T) iThreadAndMainDeal.doOnThread();
                    emitter.onNext(data);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<T>() {
                    @Override
                    public void onNext(T tempAll) {
                        if (iThreadAndMainDeal != null) {
                            iThreadAndMainDeal.doOnMain(tempAll);
                        }
                    }
                });
    }

    Map<String, IMainDelay> iMainDelay = new HashMap<>();


    public void clearAll() {
        for (Map.Entry<String, IMainDelay> entry : iMainDelay.entrySet()) {
            IMainDelay temp = entry.getValue();
            temp = null;
            iMainDelay.put(entry.getKey(), null);
        }
        iMainDelay.clear();
    }

    private void clearTag(String tag) {
        IMainDelay temp = iMainDelay.get(tag);
        temp = null;
        iMainDelay.put(tag, null);
        iMainDelay.remove(tag);
    }

    public void doDelay(int time, IMainDelay delay, final String tag) {
        iMainDelay.put(tag, delay);
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onNext(Long o) {
                        super.onNext(o);
                        if (iMainDelay.get(tag) != null) {
                            iMainDelay.get(tag).onMainDelay();
                            clearTag(tag);
                        }
                    }
                });

    }

    public void doDelayOn(int time, final IMainDelay delay) {
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onNext(Long o) {
                        super.onNext(o);
                        if (delay != null)
                            delay.onMainDelay();
                    }
                });

    }

}
