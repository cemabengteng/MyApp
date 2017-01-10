package com.example.plu.myapp.main;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.facebook.drawee.backends.pipeline.Fresco;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {

    @Inject
    MainPresenter mPresenter;

    @Bind(R.id.cobo_view)
    ComBoView coboView;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
        Gifts gifts = new Gifts();
        gifts.setImg("http://img.plures.net/live/props/cute/gift-control-b-cute-2.png");
        coboView.setData(gifts);
    }


    @Override
    protected void initView() {
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
    }
}
