package com.example.plu.myapp.biggift;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.component.ActivityComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/28.
 */

public class LargeGiftMainActivity extends MvpActivity<LargeGiftMainComponent, LargeGiftMainPresenter> {

    @Inject
    LargeGiftMainPresenter mPresenter;
    @Bind(R.id.recy_all_gifts)
    RecyclerView recyAllGifts;

    private List<LargeGift> mLargeGifts;

    @Override
    protected LargeGiftMainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
        mLargeGifts = mPresenter.getAllLargeGifts();
    }

    @Override
    public LargeGiftMainComponent initComponent(@NonNull ActivityComponent component) {
        LargeGiftMainComponent largeGiftMainComponent = component.provideLargeGiftMainComponent();
        largeGiftMainComponent.inject(this);
        return largeGiftMainComponent;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_large_gift_main);
    }
}
