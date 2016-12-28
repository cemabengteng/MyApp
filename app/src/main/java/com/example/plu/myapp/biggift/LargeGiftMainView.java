package com.example.plu.myapp.biggift;

import com.example.plu.myapp.base.mvp.MvpView;
import com.example.plu.myapp.biggift.bean.LargeGift;

import java.util.List;

/**
 * Created by chengXing on 2016/9/13.
 */
public interface LargeGiftMainView extends MvpView {
    void onGetLargeGiftFile(boolean isSuccess, List<LargeGift> list);
}
