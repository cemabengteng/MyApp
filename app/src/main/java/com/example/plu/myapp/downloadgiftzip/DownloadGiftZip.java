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
        if (isHavaGiftFile()) {
            Log.i("test", "存在gift文件");
        } else {
            if (isHaveGiftZip()) {
                Log.i("test", "存在gift压缩包");
                //TODO 校验压缩包  解压
                if (verifyZip()) {
                    try {
                        unCompressZip();
                        Log.i("test", "解压完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //删除当前zip，从新下载
                    downLoadZip();
                }

            } else {
                //下载zip包
                downLoadZip();
            }
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
            Log.i("test", crc32);
            if (("0x" + crc32).equals(mValueOfCrc32)) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
                    if (!f.exists()){
                        f.mkdir();
                    }
                    BufferedInputStream input = new BufferedInputStream(
                            zipFile.getInputStream(zipEntry));
                    BufferedOutputStream output = new BufferedOutputStream(
                            new FileOutputStream(mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/" + zipEntry.getName()));
                    Log.i("test","path: " + mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/" + zipEntry.getName());

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


//        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mStorageDirectory.getAbsolutePath() + "/" +  mValueOfCrc32));
//        int count;
//        byte data[] = new byte[1024];
//        while ((count = zis.read(data, 0, 1024)) != -1) {
//            bos.write(data, 0, count);
//        }
//        zis.close();
//        bos.close();
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
