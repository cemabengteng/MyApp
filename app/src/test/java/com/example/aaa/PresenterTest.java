package com.example.aaa;

import android.os.Environment;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.junit.Test;

import java.io.File;

import okhttp3.Call;

/**
 * Created by chengXing on 2016/10/26.
 */

public class PresenterTest {
    @Test
    public void testOkhttpUtil() {
        OkHttpUtils
                .get()
                .url("lala")
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar") {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                });
    }
}
