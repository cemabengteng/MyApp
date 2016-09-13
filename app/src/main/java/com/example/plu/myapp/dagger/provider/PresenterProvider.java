package com.example.plu.myapp.dagger.provider;

import android.content.Context;

import com.example.plu.myapp.rx.rxlifecycle.LayoutLifecycleProvider;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

/**
 * Created by chengXing on 2016/9/12.
 */
public class PresenterProvider {
    private final Context mContext;
    private final ActivityLifecycleProvider mActivityLifecycleProvider;
    private final FragmentLifecycleProvider mFragmentLifecycleProvider;
    private final LayoutLifecycleProvider mLayoutLifecycleProvider;

    public PresenterProvider(Context context, ActivityLifecycleProvider activityLifecycleProvider, FragmentLifecycleProvider fragmentLifecycleProvider, LayoutLifecycleProvider layoutLifecycleProvider) {
        this.mContext = context;
        this.mActivityLifecycleProvider = activityLifecycleProvider;
        this.mFragmentLifecycleProvider = fragmentLifecycleProvider;
        this.mLayoutLifecycleProvider = layoutLifecycleProvider;
    }

    public Context getContext() {
        return mContext;
    }

    public ActivityLifecycleProvider getActivityLifecycleProvider() {
        return mActivityLifecycleProvider;
    }

    public FragmentLifecycleProvider getFragmentLifecycleProvider() {
        return mFragmentLifecycleProvider;
    }

    public LayoutLifecycleProvider getLayoutLifecycleProvider() {
        return mLayoutLifecycleProvider;
    }
}
