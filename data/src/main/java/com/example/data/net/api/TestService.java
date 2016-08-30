package com.example.data.net.api;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by plu on 2016/8/30.
 */

public interface TestService {


    @GET("api/test")
    Observable<String> getdata();
}
