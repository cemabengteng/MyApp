package com.example.plu.myapp.base.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.view.CommonContainer;

/**
 * Created by chengXing on 2016/9/18.
 * 布局文件如果有
 * TitleBarView,Id必须为titleBar
 * CommonTainer ,id必须为viewContainer
 */
public abstract class MvpStatusActivity<C extends BaseComponent,P extends BasePresenter> extends MvpActivity<C,P> implements CommonContainer.CommonView {

    private CommonContainer mActivityContainer;

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void initView() {
        mActivityContainer = (CommonContainer) findViewById(R.id.action_compose);
        if (mActivityContainer != null){
            mActivityContainer.setCommonView(this);
        }
    }

    @Override
    public void setErrorView(@LayoutRes int res) {

    }

    @Override
    public void setLoadingView(@LayoutRes int res) {

    }

    @Override
    public void setEmptyView(@LayoutRes int res) {

    }

    @Override
    public void onErrorClick(View view) {

    }

    @Override
    protected P createPresenter() {
        return null;
    }
}
