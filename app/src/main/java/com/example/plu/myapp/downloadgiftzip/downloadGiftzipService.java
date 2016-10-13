package com.example.plu.myapp.downloadgiftzip;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by chengXing on 2016/10/13.
 */

public interface DownloadGiftzipService {
    @Streaming
    @GET
    Call<ResponseBody> downloadPicture(@Url String fileUrl);
}
