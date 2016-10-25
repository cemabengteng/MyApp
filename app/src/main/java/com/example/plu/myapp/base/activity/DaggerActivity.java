package com.example.plu.myapp.base.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.plu.myapp.App;
import com.example.plu.myapp.dagger.base.BaseActivityDagger;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.plu.myapp.dagger.component.CommonActivityComponent;
import com.example.plu.myapp.dagger.moudle.ActivityModule;

/**
 * Created by chengXing on 2016/9/12.
 */
public abstract class DaggerActivity<T extends BaseComponent> extends BaseActivity implements BaseActivityDagger<T> {


    private CommonActivityComponent mCommonActivityComponent;
    private T mComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initInject();
        initFirst();
        super.onCreate(savedInstanceState);
    }


    protected void initInject() {
        ActivityComponent activityComponent = App.getInstance().getApplicationComponent().provideActivityComponent(new ActivityModule(this));
        mComponent = initComponent(activityComponent);
    }


    protected void initFirst() {
    }

    @Override
    public T initComponent(@NonNull ActivityComponent component) {
        return null;
    }

    public CommonActivityComponent initCommon() {
        mCommonActivityComponent = App.getInstance().getApplicationComponent().provideActivityComponent(new ActivityModule(this)).provideCommonComponent();
        return mCommonActivityComponent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommonActivityComponent = null;
        mComponent = null;
    }
}
