package com.example.plu.myapp.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.plu.myapp.base.rx.RxActivity;

import butterknife.ButterKnife;

/**
 * Created by chengXing on 2016/9/12.
 */
public abstract class BaseActivity extends RxActivity {
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        ButterKnife.bind(this);
        initData(savedInstanceState);
    }

    protected abstract void initData(Bundle state);

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
