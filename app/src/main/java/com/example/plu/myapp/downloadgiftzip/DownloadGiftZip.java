package com.example.plu.myapp.downloadgiftzip;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by chengXing on 2016/10/12.
 * 用来下载礼物的压缩包
 */

public class DownloadGiftZip {

    private File mStorageDirectory;
    private String mValueOfCrc32;
    private String mzipUrl;

    public DownloadGiftZip(@NonNull List<Gifts> gifts, final File storageDirectory) {
        if (gifts == null) return;
        Observable.from(gifts)
                .observeOn(Schedulers.io())
                .filter(new Func1<Gifts, Boolean>() {
                    @Override
                    public Boolean call(Gifts gifts) {
                        if (!TextUtils.isEmpty(gifts.getBackgroundAppIcon2()) &&
                                !TextUtils.isEmpty(gifts.getBackgroundAppIcon2Url())) {
                            return true;
                        }
                        return false;
                    }
                })
                .subscribe(new Action1<Gifts>() {
                    @Override
                    public void call(Gifts gifts) {
                        DownloadGiftZip.this.mValueOfCrc32 = gifts.getBackgroundAppIcon2();
                        DownloadGiftZip.this.mzipUrl = gifts.getBackgroundAppIcon2Url();
                        DownloadGiftZip.this.mStorageDirectory = storageDirectory;
                        doCheck();
                    }
                });
    }


    private void doCheck() {
        if (!mStorageDirectory.exists()) {
            mStorageDirectory.mkdir();
        }
        //检查是否含有该zip
        if (isHaveGiftZip()) {
            //检查签名
            if (verifyZip()) {
                //检查是否解压
                if (!isHavaGiftFile()) {
                    unCompressZip();
                } else {
                    Log.i("test", "都准备好了");
                }
            } else {
                //签名不对的话，重新下载并解压
                if (downLoadZip()) {
                    unCompressZip();
                }
            }
        } else {
            //没有的话，下载并解压
            if (downLoadZip()) {
                unCompressZip();
            }
        }
    }

    private boolean downLoadZip() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.baidu.com").build();
        DownloadGiftzipService service = retrofit.create(DownloadGiftzipService.class);
        try {
            Response<ResponseBody> response = service.downloadPicture(mzipUrl).execute();
            if (response.isSuccessful()) {
                if (writeZipToDisk(response.body())) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
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


    private void unCompressZip() {
        File file = new File(mStorageDirectory.getAbsolutePath(), mValueOfCrc32 + ".zip");
        String outPutFile = mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/";
        org.apache.tools.zip.ZipFile zipFile = null;
        try {
            zipFile = new org.apache.tools.zip.ZipFile(file);
            File f = new File(mStorageDirectory.getAbsolutePath() + "/" + mValueOfCrc32 + "/");
            if (!f.exists()) {
                f.mkdir();
            }
            Enumeration entrys = zipFile.getEntries();
            int i = 0;
            while (entrys.hasMoreElements()) {
                org.apache.tools.zip.ZipEntry zipEntry = (org.apache.tools.zip.ZipEntry) entrys.nextElement();
                if (zipEntry == null) {
                    continue;
                }
                if (zipEntry.isDirectory()) continue;
                Log.i("test", "我读到了一张图片" + (i++));
                BufferedInputStream input = new BufferedInputStream(
                        zipFile.getInputStream(zipEntry));
                BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(outPutFile + zipEntry.getName()));
                Log.i("test", outPutFile + zipEntry.getName());

                int len;
                byte[] bytes = new byte[1024];
                while ((len = input.read(bytes)) > 0) {
                    Log.i("test", "len: " + len);
                    output.write(bytes, 0, len);
                    output.flush();
                }
                output.close();
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            File f = new File(outPutFile);
            if (f.exists()) {
                delFile(f);
            }
        } finally {
            if (null != zipFile) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //删除文件
    private void delFile(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
                f.delete();
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //把zip包保存起来
    private boolean writeZipToDisk(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(mStorageDirectory.getAbsolutePath(), mValueOfCrc32 + ".zip");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
