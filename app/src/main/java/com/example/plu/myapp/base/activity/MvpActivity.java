package com.example.plu.myapp.base.activity;

import com.example.plu.myapp.base.mvp.MvpPresenter;
import com.example.plu.myapp.base.mvp.MvpView;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.orhanobut.logger.Logger;

/**
 * Created by chengXing on 2016/9/13.
 */
public abstract class MvpActivity<C extends BaseComponent, P extends MvpPresenter> extends DaggerActivity<C> implements MvpView {

    private P mPresenter;

    @Override
    protected void initFirst() {
        mPresenter = createPresenter();
    }

    protected abstract P createPresenter();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            Logger.d("Presenter被销毁了");
            mPresenter.detachView();
        }
    }
}
