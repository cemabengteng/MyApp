package com.example.plu.myapp.main;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.dagger.provider.PresenterProvider;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    public MainPresenter(PresenterProvider presenterProvider) {
        super(presenterProvider);
    }
}
