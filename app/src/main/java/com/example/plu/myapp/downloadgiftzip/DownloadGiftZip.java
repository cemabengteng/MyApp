package com.example.plu.myapp.downloadgiftzip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by chengXing on 2016/10/12.
 */

public class DownloadGiftZip {

    private final File mStorageDirectory;
    private final String mValueOfCrc32;
    private final String mzipUrl;
    private final Context mContext;

    public DownloadGiftZip(@NonNull String backgroundAppIcon2, @NonNull String backgroundAppIcon2Url,
                           Context context) {
        this.mValueOfCrc32 = checkNull(backgroundAppIcon2);
        this.mzipUrl = checkNull(backgroundAppIcon2Url);
        this.mContext = context;
        mStorageDirectory = context.getExternalFilesDir("gift");
        doCheck();
    }


    private void doCheck() {
        if (isHavaGiftFile()) {
            Log.i("test", "存在gift文件");
        } else {
            if (isHaveGiftZip()) {
                Log.i("test", "存在gift压缩包");
            } else {
                //下载zip包
                File file = new File(mStorageDirectory.getAbsolutePath(), "0xb9ffc158.zip");
                try {
                    file.createNewFile();
                    Log.i("test", "下载了zip文件");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isHavaGiftFile() {
        File[] files = mStorageDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (mValueOfCrc32.equals(file.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHaveGiftZip() {
        File[] files = mStorageDirectory.listFiles();
        String zipName = mValueOfCrc32 + ".zip";
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                if (zipName.equals(file.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String checkNull(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            return s;
        }
    }
}
