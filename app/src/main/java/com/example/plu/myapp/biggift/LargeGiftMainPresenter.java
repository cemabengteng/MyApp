package com.example.plu.myapp.biggift;

import com.example.plu.myapp.base.mvp.BasePresenter;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.provider.PresenterProvider;
import com.example.plu.myapp.main.MainView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/9/13.
 */
public class LargeGiftMainPresenter extends BasePresenter<MainView> {

    private List<LargeGift> mAllLargeGifts;

    @Inject
    public LargeGiftMainPresenter(PresenterProvider presenterProvider) {
        super(presenterProvider);
    }

    public List<LargeGift> getAllLargeGifts() {
        return mAllLargeGifts;
    }
}
