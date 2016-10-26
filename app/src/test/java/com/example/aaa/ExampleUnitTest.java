package com.example.aaa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;

import static junit.framework.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testToyRoom() {
        Shelly.<String>createDomino("file name")
                .background()
                .flatMap(new Function1<String, List<Byte>>() {
                    @Override
                    public List<Byte> call(String input) {
//                        File[] files = new File(input).listFiles();
                        byte[] bytes = input.getBytes();
                        List<Byte> result = new ArrayList<>();
                        for (Byte by : bytes) {
                            result.add(by);
                        }
                        return result;
                    }
                })
                .perform(new Action1<Byte>() {
                    @Override
                    public void call(Byte input) {
                        System.out.print(input.toString());
                    }
                })
                .commit();
    }

    @Test
    public void test() {
        long playDuration = 0;
        System.out.print(playDuration / 3600);
        System.out.print(playDuration % 3600 / 60);
        System.out.print(playDuration % 60);
    }

    @Test
    public void testExecutors() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.print(aLong + "");
                    }
                });
    }

    @Test
    public void stringText() {
        String likesTxt = -1 >= 0 ? (10 + "\n赞") : ("--\n赞");
        System.out.print(likesTxt);
    }

    @Test
    public void testRxJava() {
        Scheduler io = Schedulers.io();
        Scheduler.Worker worker = io.createWorker();
        System.out.print("mainThread: " + Thread.currentThread().getName() + "\n");
        worker.schedule(new Action0() {
            @Override
            public void call() {
                System.out.print("callThread: " + Thread.currentThread().getName() + "\n");
                System.out.print("call method run" + "\n");
            }
        });
        worker.unsubscribe();
    }

    @Test
    public void testRxjava() {
        Scheduler scheduler = Schedulers.newThread();
        System.out.print("scheduler " + scheduler.toString() + "\n");
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        System.out.print(Thread.currentThread().getName() + "\n");
                        System.out.print("haha" + "\n");
                        subscriber.onNext("haha");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(scheduler)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.print(Thread.currentThread().getName() + "\n");
                        System.out.print("doOnSubscribe \n");
                    }
                })
                .doOnNext(new rx.functions.Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.print(Thread.currentThread().getName() + "\n");
                        System.out.print("do on next \n");
                    }
                })
                .subscribe(new rx.functions.Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.print(Thread.currentThread().getName() + "\n");
                        System.out.print(s);
                    }
                });
    }


    @Test
    public void testSchedule() {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        System.out.print(Thread.currentThread().getName() + "\n");
                        subscriber.onNext("haha");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new rx.functions.Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.print(Thread.currentThread().getName() + "\n");
                    }
                });

    }


}