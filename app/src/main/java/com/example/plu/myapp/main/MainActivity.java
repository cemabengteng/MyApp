package com.example.plu.myapp.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {

    @Inject
    MainPresenter mPresenter;

    @Bind(R.id.cobo_view)
    ComBoView coboView;

    Gifts gifts;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
        gifts = new Gifts();
        gifts.setImg("http://img.plures.net/live/props/cute/gift-control-b-cute-2.png");
        Options option1 = new Options();
        option1.setNum(100);
        Options option2 = new Options();
        option2.setNum(200);
        Options option3 = new Options();
        option3.setNum(300);
        Options option4 = new Options();
        option4.setNum(400);
        List<Options> optionsList = new ArrayList<>();
        optionsList.add(option1);
        optionsList.add(option2);
        optionsList.add(option3);
        optionsList.add(option4);
        gifts.setOptionses(optionsList);

        coboView.setData(gifts);

        coboView.setOnComboListener(new ComBoView.OnComboListener() {
            @Override
            public void sendGift(int num, boolean isCombo) {
                Toast.makeText(mContext, num + "", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.bt_1)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1:
                coboView.show(gifts);
                break;
        }
    }


    @Override
    protected void initView() {
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
    }
}
