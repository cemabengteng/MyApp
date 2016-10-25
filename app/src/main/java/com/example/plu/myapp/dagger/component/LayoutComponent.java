package com.example.plu.myapp.dagger.component;

import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.moudle.LayoutModule;

import dagger.Subcomponent;

/**
 * Created by plu on 2016/8/29.
 */
@Subcomponent(modules = LayoutModule.class)
public interface LayoutComponent extends BaseComponent {
    CommonLayoutComponent provideCommonComponent();
}
