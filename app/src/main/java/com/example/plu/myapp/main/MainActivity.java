package com.example.plu.myapp.main;

import android.os.Bundle;
import android.os.Looper;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.base.mvp.MvpView;
import com.safframework.app.annotation.Trace;
import com.safframework.log.L;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> implements MvpView {

    @Inject
    MainPresenter mPresenter;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
        useAsync();
    }

    @Trace
    public void useAsync() {
        L.e(" thread=" + Thread.currentThread().getId());
        L.e("ui thread=" + Looper.getMainLooper().getThread().getId());
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
    }
}
