package com.example.plu.myapp.dagger.moudle;

import android.content.Context;

import com.example.plu.myapp.App;
import com.example.plu.myapp.dagger.qualifier.ContextLevel;
import com.example.plu.myapp.dagger.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by plu on 2016/8/29.
 */
@Module
public class ApplicationMoudle {
    private App app;

    public ApplicationMoudle(App app) {
        this.app = app;
    }

    @Provides
    @ApplicationScope
    @ContextLevel
    public Context provideContext(){
        return app.getApplicationContext();
    }
}
