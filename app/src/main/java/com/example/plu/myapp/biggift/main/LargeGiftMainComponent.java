package com.example.plu.myapp.biggift.main;

import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by chengXing on 2016/9/13.
 */
@ActivityScope
@Subcomponent(modules = {LargeGiftMainActivityMoudle.class})
public interface LargeGiftMainComponent extends BaseComponent {
    void inject(LargeGiftMainActivity activity);
}
