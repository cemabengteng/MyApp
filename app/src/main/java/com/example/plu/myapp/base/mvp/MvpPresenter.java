package com.example.plu.myapp.base.mvp;

import android.support.annotation.UiThread;

/**
 * Created by chengXing on 2016/9/13.
 */
public interface MvpPresenter<V extends MvpView> {
    @UiThread
    void attachView(V view);

    @UiThread
    void detachView();
}
