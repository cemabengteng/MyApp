package com.example.plu.myapp.dagger.component;

import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.moudle.ActivityModule;
import com.example.plu.myapp.dagger.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by plu on 2016/8/29.
 */
@ActivityScope
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent extends BaseComponent {
    CommonActivityComponent provideCommonComponent();


}
