package com.example.plu.myapp.downloadgiftzip;

import android.app.Activity;
import android.os.Bundle;

import com.example.plu.myapp.R;

import java.util.ArrayList;
import java.util.List;

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
        List<Gifts> gifts = new ArrayList<>();

        Gifts gifts1 = new Gifts();
        gifts1.setBackgroundAppIcon2("20f7558e90b590032f945935ec4c5ec4");
        gifts1.setBackgroundAppIcon2Url("http://img.plures.net/v2/5890/7b57/6557/01b1/b67a/eb30/6ed7/4f27.zip");

        Gifts gifts2 = new Gifts();
        gifts2.setBackgroundAppIcon2("87fb9683386c321b9993e609d7fd0975");
        gifts2.setBackgroundAppIcon2Url("http://img.plures.net/v2/303f/6124/f5e6/60b7/101f/3a84/b225/f895.zip");

        gifts.add(gifts1);
        gifts.add(gifts2);

        new DownloadGiftZip(gifts, this.getExternalFilesDir("gift"));
    }
}
