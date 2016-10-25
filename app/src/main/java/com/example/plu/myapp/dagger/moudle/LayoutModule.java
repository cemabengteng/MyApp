package com.example.plu.myapp.dagger.moudle;

import android.content.Context;

import com.example.plu.myapp.dagger.qualifier.ContextLevel;
import com.example.plu.myapp.dagger.scope.LayoutScope;
import com.example.plu.myapp.rx.rxlifecycle.LayoutLifecycleProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by plu on 2016/8/29.
 */
@Module
public class LayoutModule {
    private LayoutLifecycleProvider provider;
    private Context context;

    public LayoutModule(Context context) {
        this(null, context);
    }

    public LayoutModule(LayoutLifecycleProvider provider, Context context) {
        this.provider = provider;
        this.context = context;
    }

    @Provides
    @LayoutScope
    @ContextLevel(ContextLevel.ACTIVITY)
    Context proviedContext() {
        return context;
    }

    @Provides
    @LayoutScope
    LayoutLifecycleProvider providerActivityProvider() {
        return provider;
    }
}
