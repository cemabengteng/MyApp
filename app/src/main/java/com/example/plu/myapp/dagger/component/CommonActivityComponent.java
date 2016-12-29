package com.example.plu.myapp.dagger.component;

import com.example.plu.myapp.biggift.main.LargeGiftMainActivity;
import com.example.plu.myapp.biggift.setjson.SetJsonActivity;
import com.example.plu.myapp.dagger.base.BaseComponent;

import dagger.Subcomponent;

/**
 * Created by chengXing on 2016/9/13.
 */
@Subcomponent
public interface CommonActivityComponent extends BaseComponent {
    void inject(LargeGiftMainActivity largeGiftMainActivity);

    void inject(SetJsonActivity setJsonActivity);
}
