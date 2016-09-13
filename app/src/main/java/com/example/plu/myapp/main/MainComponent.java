package com.example.plu.myapp.main;

import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by chengXing on 2016/9/13.
 */
@ActivityScope
@Subcomponent(modules = {MainActivityMoudle.class})
public interface MainComponent extends BaseComponent {
    void inject(MainActivity activity);
}
