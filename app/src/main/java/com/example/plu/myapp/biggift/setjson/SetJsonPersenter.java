package com.example.plu.myapp.biggift.setjson;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.dagger.provider.PresenterProvider;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonPersenter extends BasePresenter<SetJsonView> {

    @Inject
    public SetJsonPersenter(PresenterProvider presenterProvider) {
        super(presenterProvider);
    }
}
