package com.example.plu.myapp.biggift;

import android.os.Environment;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.provider.PresenterProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chengXing on 2016/9/13.
 */
public class LargeGiftMainPresenter extends BasePresenter<LargeGiftMainView> {

    private String sbPath = Environment.getExternalStorageDirectory().getPath();
    private File mFile = new File(sbPath + "/largegift/");

    @Inject
    public LargeGiftMainPresenter(PresenterProvider presenterProvider) {
        super(presenterProvider);
    }

    public void getAllLargeGifts() {
        Observable.just(mFile)
                .map(new Func1<File, List<LargeGift>>() {
                    @Override
                    public List<LargeGift> call(File file) {
                        List<LargeGift> largeGifts = new ArrayList<>();
                        if (file.exists() && file.isDirectory()) {
                            File[] files = file.listFiles();
                            if (files != null && files.length > 0) {
                                for (int i = 0; i < files.length; i++) {
                                    File f = files[i];
                                    if (f.isDirectory()) {
                                        LargeGift largeGift = new LargeGift();
                                        largeGift.setName(f.getName());
                                        largeGift.setPath(f.getPath());
                                        largeGifts.add(largeGift);
                                    }
                                }
                            }
                        }
                        return largeGifts;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<LargeGift>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().onGetLargeGiftFile(false, null);
                        }
                    }

                    @Override
                    public void onNext(List<LargeGift> largeGiftList) {
                        if (isViewAttached()) {
                            getView().onGetLargeGiftFile(true, largeGiftList);
                        }
                    }
                });
    }
}
