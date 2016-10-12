package com.example.plu.myapp.downloadgiftzip;

import android.app.Activity;
import android.os.Bundle;

import com.example.plu.myapp.R;

/**
 * Created by chengXing on 2016/10/12.
 */

public class DownLoadGiftTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_gift_test);
        gogogo();
    }

    private void gogogo() {
        DownloadGiftZip downloadGiftZip = new DownloadGiftZip("0xb9ffc158",
                "http://img.plures.net/v2/303f/6124/f5e6/60b7/101f/3a84/b225/f895.zip",
                this);

    }
}
