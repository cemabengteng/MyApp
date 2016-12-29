package com.example.plu.myapp.biggift.setjson;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.util.PluLog;

import javax.inject.Inject;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonActivity extends MvpActivity<BaseComponent, SetJsonPersenter> implements SetJsonView {

    public static String PATH = "swf_path";
    private static final String DEFAULT_CONFIG = "config.txt";

    @Inject
    SetJsonPersenter mPersenter;

    @Override
    protected SetJsonPersenter createPresenter() {
        return mPersenter;
    }

    @Override
    protected void initData(Bundle state) {
        LargeGift largeGift = getIntent().getParcelableExtra(PATH);
        PluLog.d(largeGift);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_set_json);
    }
}
