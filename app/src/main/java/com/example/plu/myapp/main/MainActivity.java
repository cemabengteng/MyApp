package com.example.plu.myapp.main;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {

    @Inject
    MainPresenter mPresenter;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
    }
}
