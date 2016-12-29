package com.example.plu.myapp.biggift.show;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.dagger.component.CommonActivityComponent;

/**
 * Created by chengXing on 2016/12/29.
 */

public class ShowActivity extends MvpActivity<CommonActivityComponent, BasePresenter> {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_show_lwf);
    }
}
