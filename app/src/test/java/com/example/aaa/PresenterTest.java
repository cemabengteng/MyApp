package com.example.aaa;

import android.os.Environment;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

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

    @Test
    public void test() {
        System.out.print(spliteTime("/Date(1479312000000+0800)") + "\n");
        System.out.print(spliteTime("/Date(1481472000000+0800)") + "\n");
        System.out.print(spliteTime("/Date(1479375483610+0800)") + "\n");
        System.out.print(getCurrentSystemTime() + 3 * 60 * 1000 + "\n");
        System.out.print(getCurrentSystemTime() + 4 * 60 * 1000 + "\n");
        System.out.print(getCurrentSystemTime() + 10 * 60 * 1000 + "\n");
        System.out.print(getCurrentSystemTime() + 30 * 60 * 1000);
    }

    public static String spliteTime(String dateStr) {
        String sequence = dateStr.replace("/Date(", "");
        return sequence.substring(0, 13).trim();
    }

    public static Long getCurrentSystemTime() {
        return Calendar.getInstance().getTimeInMillis();
    }


    private static final String KEY = "99549f44044695707a564586d2d1aad6";

    @Test
    public void tttt() {
        CheckedInputStream cis;
        byte[] buf;
        try {
            cis = new CheckedInputStream(
                    new FileInputStream("d:/2e5c.zip"), new CRC32());
            buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }
            long checksum = cis.getChecksum().getValue();
            String crc32 = Long.toHexString(checksum);
            crc32 = "0x" + crc32 + KEY;
            System.out.print("crc32: " + crc32 + "\n");
            String s = md5(crc32);
            System.out.print("md5: " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

}
