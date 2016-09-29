package com.example.plu.myapp.love;

import android.os.Bundle;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.DaggerActivity;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.view.loveview.LoveView;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/9/28.
 */

public class LoveActivity extends DaggerActivity<BaseComponent> {
    @Bind(R.id.loveView)
    LoveView loveView;

    @Override
    protected void initData(Bundle state) {
        loveView.startPathAnim(3000);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_love);
    }
}
