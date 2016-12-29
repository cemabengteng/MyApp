package com.example.plu.myapp.biggift.setjson;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.component.CommonActivityComponent;
import com.example.plu.myapp.util.PluLog;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonActivity extends MvpActivity<CommonActivityComponent, SetJsonPersenter> implements SetJsonView {

    public static String PATH = "swf_path";

    @Inject
    SetJsonPersenter mPersenter;

    @Override
    protected void initInject() {
        initCommon().inject(this);
    }

    @Override
    protected SetJsonPersenter createPresenter() {
        return mPersenter;
    }

    @Override
    protected void initData(Bundle state) {
        LargeGift largeGift = getIntent().getParcelableExtra(PATH);
        PluLog.d(largeGift);
        mPersenter.loadJson(largeGift);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_set_json);
    }

    @Override
    public void onLoadBigGiftJson(boolean isSuccess, BigGiftConfigBean bean) {
        if (isSuccess) {
            PluLog.d(bean);
        }
    }
}
