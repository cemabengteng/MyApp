package com.example.plu.myapp.biggift;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.plu.myapp.util.PluLog;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/28.
 */

public class LargeGiftMainActivity extends MvpActivity<LargeGiftMainComponent, LargeGiftMainPresenter> implements LargeGiftMainView {

    @Inject
    LargeGiftMainPresenter mPresenter;
    @Bind(R.id.recy_all_gifts)
    RecyclerView recyAllGifts;


    @Override
    protected LargeGiftMainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    public LargeGiftMainComponent initComponent(@NonNull ActivityComponent component) {
        LargeGiftMainComponent largeGiftMainComponent = component.provideLargeGiftMainComponent();
        largeGiftMainComponent.inject(this);
        return largeGiftMainComponent;
    }

    @Override
    protected void initData(Bundle state) {
        mPresenter.getAllLargeGifts();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_large_gift_main);
    }

    @Override
    public void onGetLargeGiftFile(boolean isSuccess, List<LargeGift> list) {
        if (isSuccess) {
            if (list.size() > 0) {
                PluLog.d(list);
            } else {
                Toast.makeText(this, "请检查是否有文件", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请检查是否有文件", Toast.LENGTH_SHORT).show();
        }
    }
}
