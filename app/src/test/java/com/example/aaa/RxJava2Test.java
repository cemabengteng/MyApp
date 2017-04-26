package com.example.aaa;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by chengXing on 2017/4/7.
 */

public class RxJava2Test {

    @Test
    public void test() {
        // Observable/Observer用法
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

            }
        });


        new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        //  Flowable/Subscriber
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {

            }
        }, BackpressureStrategy.BUFFER);

        new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        // Single/SingleObserver
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) throws Exception {

            }
        });

        new SingleObserver<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }
        };

        /**
         *  操作符相关
         *  Rx1.0-----------Rx2.0

         Action0--------Action
         Action1--------Consumer
         Action2--------BiConsumer
         */

    }

    @Test
    public void test2() {
        Observable.just("gogo")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        String s1 = null;
                        s1.length();
                        return "go";
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.print(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.print("error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Test
    public void test3() {
        rx.Observable<String> aa = rx.Observable.just("aa");

        io.reactivex.Flowable f2 = RxJavaInterop.toV2Flowable(aa);


        f2.subscribe(new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                System.out.print("aa");
            }
        });

    }
}
