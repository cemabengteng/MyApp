package com.example.plu.myapp.dagger.moudle;

import android.app.Application;
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
    private Application app;

    public ApplicationMoudle(App app) {
        this.app = app;
    }

    @Provides
    @ApplicationScope
    @ContextLevel(ContextLevel.APPLICATION)
    public Context provideContext(){
        return app.getApplicationContext();
    }
}
