package com.example.plu.myapp.dagger.component;

import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.moudle.FragmentModule;
import com.example.plu.myapp.dagger.scope.FragmentScope;

import dagger.Subcomponent;

/**
 * Created by plu on 2016/8/29.
 */
@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent extends BaseComponent {
    CommonFragmentComponent provideCommonComponent();
}
