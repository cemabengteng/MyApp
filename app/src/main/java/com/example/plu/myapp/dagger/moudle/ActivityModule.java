package com.example.plu.myapp.dagger.moudle;

import android.app.Activity;
import android.content.Context;

import com.example.plu.myapp.dagger.provider.PresenterProvider;
import com.example.plu.myapp.dagger.qualifier.ContextLevel;
import com.example.plu.myapp.dagger.scope.ActivityScope;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by plu on 2016/8/29.
 */
@Module
public class ActivityModule {
    private final ActivityLifecycleProvider mActivityLifecycleProvider;

    public ActivityModule(ActivityLifecycleProvider provider) {
        this.mActivityLifecycleProvider = provider;
    }

    @Provides
    @ActivityScope
    @ContextLevel(ContextLevel.ACTIVITY)
    Context provideContext(){
        return (Activity)mActivityLifecycleProvider;
    }

    @Provides
    @ActivityScope
    ActivityLifecycleProvider providerActivityProvider(){
        return mActivityLifecycleProvider;
    }

    @Provides
    @ActivityScope
    PresenterProvider providerPresenterProvider(){
        return new PresenterProvider((Activity)mActivityLifecycleProvider,mActivityLifecycleProvider,null,null);
    }

}
