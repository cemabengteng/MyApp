package com.example.plu.myapp.base.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.example.plu.myapp.App;
import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.base.mvp.MvpView;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.base.BaseLayoutDagger;
import com.example.plu.myapp.dagger.component.CommonLayoutComponent;
import com.example.plu.myapp.dagger.component.LayoutComponent;
import com.example.plu.myapp.dagger.moudle.LayoutModule;

/**
 * Created by chengXing on 2016/11/11.
 */

public abstract class DaggerRelativeLayout<C extends BaseComponent, V extends MvpView, P extends BasePresenter<V>> extends BaseRelativelayout implements MvpView, BaseLayoutDagger {
    protected P presenter;
    protected C component;
    protected CommonLayoutComponent commonLayoutComponent;

    public DaggerRelativeLayout(Context context) {
        super(context);
    }

    public DaggerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DaggerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        initInject();
    }

    public abstract P createPresenter();

    //dagger依赖注入实现
    public void initInject() {
        LayoutComponent layoutComponent = App.getInstance().getApplicationComponent().provideLayoutComponent(new LayoutModule(this, getContext()));
        component = initComponent(layoutComponent);
    }

    public CommonLayoutComponent initCommon() {
        commonLayoutComponent = App.getInstance().getApplicationComponent().provideLayoutComponent(new LayoutModule(this, getContext())).provideCommonComponent();
        return commonLayoutComponent;
    }

    @NonNull
    @Override
    public C initComponent(@NonNull LayoutComponent component) {
        return null;
    }

    @Override
    public void release() {
        super.release();
        if (null != presenter) {
            presenter.detachView();
            presenter = null;
        }
        if (null != commonLayoutComponent) {
            commonLayoutComponent = null;
        }
        if (null != component) {
            component = null;
        }
    }
}
