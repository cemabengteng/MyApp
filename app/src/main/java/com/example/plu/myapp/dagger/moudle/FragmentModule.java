package com.example.plu.myapp.dagger.moudle;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.plu.myapp.dagger.qualifier.ContextLevel;
import com.example.plu.myapp.dagger.scope.FragmentScope;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by plu on 2016/8/29.
 */
@Module
public class FragmentModule {

    private final FragmentLifecycleProvider mProvider;

    public FragmentModule(FragmentLifecycleProvider provider) {
        this.mProvider = provider;
    }

    @Provides
    @FragmentScope
    @ContextLevel(ContextLevel.FRAGMENT)
    Context proviedContext() {
        return ((Fragment) mProvider).getActivity();
    }

    @Provides
    @FragmentScope
    FragmentLifecycleProvider providerFragmentProvider() {
        return mProvider;
    }
}
