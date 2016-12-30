package com.example.plu.myapp.biggift.setjson;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.provider.PresenterProvider;
import com.example.plu.myapp.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonPersenter extends BasePresenter<SetJsonView> {

    public static final String DEFAULT_CONFIG = "config.txt";

    private boolean isHaveAni = false;

    @Inject
    public SetJsonPersenter(PresenterProvider presenterProvider) {
        super(presenterProvider);
    }

    public void loadJson(LargeGift largeGift) {
        Observable.just(largeGift)
                .map(new Func1<LargeGift, BigGiftConfigBean>() {
                    @Override
                    public BigGiftConfigBean call(LargeGift largeGift) {
                        File file = new File(largeGift.getPath());
                        BigGiftConfigBean bean = new BigGiftConfigBean();
                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            for (File f : files) {
                                if (f.getName().toLowerCase().endsWith(".ani")) {
                                    isHaveAni = true;
                                    bean.setPath(f.getPath());
                                }
                                if (f.getName().toLowerCase().endsWith(DEFAULT_CONFIG)) {
                                    String json = FileUtils.getFileJson(f.getPath());
                                    bean = new Gson().fromJson(json, BigGiftConfigBean.class);
                                }
                            }
                        }
                        return bean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigGiftConfigBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().onLoadBigGiftJson(false, null);
                        }
                    }

                    @Override
                    public void onNext(BigGiftConfigBean bean) {
                        if (isViewAttached()) {
                            getView().onLoadBigGiftJson(true, bean);
                        }
                    }
                });
    }


    public boolean checkAniFile() {
        return isHaveAni;
    }
}
