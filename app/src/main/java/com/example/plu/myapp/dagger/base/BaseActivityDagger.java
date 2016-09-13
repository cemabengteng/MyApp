package com.example.plu.myapp.dagger.base;

import android.support.annotation.NonNull;

import com.example.plu.myapp.dagger.component.ActivityComponent;

/**
 * Created by chengXing on 2016/9/13.
 */
public interface BaseActivityDagger<C extends BaseComponent> {
    @NonNull
    C initComponent(@NonNull ActivityComponent activityComponent);
}
