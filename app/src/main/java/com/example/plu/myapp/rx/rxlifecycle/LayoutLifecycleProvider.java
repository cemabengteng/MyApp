package com.example.plu.myapp.rx.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;

/**
 * Created by plu on 2016/8/29.
 */
public interface LayoutLifecycleProvider {
    @NonNull
    @CheckResult
    <T>LifecycleTransformer<T> bindUntilEvent(@NonNull LayoutEvent event);
}
