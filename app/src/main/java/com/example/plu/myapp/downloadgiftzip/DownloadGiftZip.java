package com.example.plu.myapp.downloadgiftzip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        if (isHaveGiftZip()) {
            if (verifyZip()) {
                if (!isHavaGiftFile()) {
                    try {
                        unCompressZip();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("test", "都准备好了");
                }
            } else {
                downLoadZip();
            }
        } else {
            downLoadZip();
        }
    }

    private void downLoadZip() {
        File file = new File(mStorageDirectory.getAbsolutePath(), mValueOfCrc32 + ".zip");
        try {
            file.createNewFile();
            Log.i("test", "下载了zip文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyZip() {
        File file = new File(mStorageDirectory.getAbsolutePath(), mValueOfCrc32 + ".zip");
        try {
            CheckedInputStream cis = new CheckedInputStream(
                    new FileInputStream(file), new CRC32());
            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }
            long checksum = cis.getChecksum().getValue();
            String crc32 = Long.toHexString(checksum);

            crc32 = "0x" + crc32 + "99549f44044695707a564586d2d1aad6";
            Log.i("test", crc32);

            String s = MD5(crc32);

            Log.i("test", s);

            if (s.toLowerCase().equals(mValueOfCrc32)) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void unCompressZip() throws Exception {
        File file = new File(mStorageDirectory.getAbsolutePath(), mValueOfCrc32 + ".zip");
        ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<?> entrys = zipFile.entries();
            while (entrys.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entrys.nextElement();
                if (zipEntry.isDirectory()) {
                    File temp = new File(mStorageDirectory.getAbsolutePath() + zipEntry.getName());
                    if (!temp.exists()) {
                        if (!temp.mkdirs()) {
                            throw new Exception();
                        }
                    }
                } else {
                    File f = new File(mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/");
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    BufferedInputStream input = new BufferedInputStream(
                            zipFile.getInputStream(zipEntry));
                    BufferedOutputStream output = new BufferedOutputStream(
                            new FileOutputStream(mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/" + zipEntry.getName()));
                    Log.i("test", "path: " + mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/" + zipEntry.getName());

                    int len = -1;
                    byte[] bytes = new byte[2048];
                    while ((len = input.read(bytes)) != -1) {
                        output.write(bytes, 0, len);
                    }
                    output.close();
                    input.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != zipFile) {
                try {
                    zipFile.close();
                    zipFile = null;
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
