package com.example.plu.myapp.base.mvp;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private ViewHolder<V> viewHolder;
    @UiThread
    @Override
    public void attachView(V view) {
        viewHolder = new ViewHolder<>(view);
    }


    @UiThread
    @Nullable
    public V getView() {
        return viewHolder == null ? null : viewHolder.getView();
    }


    @UiThread
    public boolean isViewAttached() {
        return viewHolder != null && viewHolder.isViewAttached();
    }

    @UiThread
    @Override
    public void detachView() {
        if (viewHolder != null)
            viewHolder.detachView();
    }
}
