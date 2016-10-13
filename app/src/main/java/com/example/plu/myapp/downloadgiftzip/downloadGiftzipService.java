package com.example.plu.myapp.downloadgiftzip;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by chengXing on 2016/10/13.
 */

public interface downloadGiftzipService {
    @Streaming
    @GET
    Observable<ResponseBody> downloadPicture(@Url String fileUrl);
}
