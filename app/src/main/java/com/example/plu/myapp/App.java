package com.example.plu.myapp;

import com.example.plu.myapp.base.rx.RxApp;
import com.example.plu.myapp.dagger.component.ApplicationComponent;
import com.example.plu.myapp.dagger.component.DaggerApplicationComponent;
import com.example.plu.myapp.dagger.moudle.ApplicationMoudle;

/**
 * Created by plu on 2016/8/29.
 */
public class App extends RxApp {
    private static App mApp;
    private ApplicationComponent mApplicationComponent;

    public static App getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mApp == null) {
            mApp = this;
        }
        initApplicationComponent();

    }

    private void initApplicationComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder().applicationMoudle(new ApplicationMoudle(this)).build();
        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
