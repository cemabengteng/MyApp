package com.example.plu.myapp.base.rx;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.example.plu.myapp.rx.rxlifecycle.ApplicationEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.subjects.BehaviorSubject;

/**
 * Created by chengXing on 2016/9/12.
 */
public class RxApp extends Application {
    private final BehaviorSubject<ApplicationEvent> lifycycleSubject = BehaviorSubject.create();

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ApplicationEvent event) {
        return RxLifecycle.bindUntilEvent(lifycycleSubject, event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lifycycleSubject.onNext(ApplicationEvent.ONCREATE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        lifycycleSubject.onNext(ApplicationEvent.ONLOWMEMORY);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        lifycycleSubject.onNext(ApplicationEvent.ONTERMINATE);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        lifycycleSubject.onNext(ApplicationEvent.ONTRIMMENORY);
    }
}
