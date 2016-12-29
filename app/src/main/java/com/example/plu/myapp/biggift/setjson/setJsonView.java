package com.example.plu.myapp.biggift.setjson;

import com.example.plu.myapp.base.mvp.MvpView;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;

/**
 * Created by chengXing on 2016/12/28.
 */

public interface SetJsonView extends MvpView {
    void onLoadBigGiftJson(boolean isSuccess, BigGiftConfigBean bean);
}
