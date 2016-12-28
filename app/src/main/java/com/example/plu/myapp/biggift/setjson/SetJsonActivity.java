package com.example.plu.myapp.biggift.setjson;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.dagger.base.BaseComponent;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonActivity extends MvpActivity<BaseComponent, SetJsonPersenter> implements SetJsonView {

    @Inject
    SetJsonPersenter mPersenter;

    @Override
    protected SetJsonPersenter createPresenter() {
        return mPersenter;
    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_set_json);
    }
}
