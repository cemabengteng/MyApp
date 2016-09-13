package com.example.plu.myapp.base.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.plu.myapp.App;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.plu.myapp.dagger.moudle.ActivityModule;

/**
 * Created by chengXing on 2016/9/12.
 */
public abstract class DaggerActivity<T extends BaseComponent> extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInject();
    }



    protected void initInject(){
        ActivityComponent component = App.getInstance().getApplicationComponent().provideActivityComponent(new ActivityModule(this));
        initComponent(component);
    }

    protected abstract void initComponent(@NonNull  ActivityComponent component);
}
