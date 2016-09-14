package com.example.plu.myapp.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.dagger.base.BaseComponent;

/**
 * Created by chengXing on 2016/9/14.
 */
public abstract class MvpFragment<C extends BaseComponent, P extends BasePresenter> extends DaggerFragment<C> {

    private P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    protected abstract P createPresenter() ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }
    }
}
