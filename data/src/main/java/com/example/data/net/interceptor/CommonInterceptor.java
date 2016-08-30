package com.example.data.net.interceptor;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by plu on 2016/8/30.
 */
public class CommonInterceptor implements Interceptor {
    protected String reportType;
    protected String reportContent;
    protected long startTime;
    protected Context mContext;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder = interceptBuilder(builder);
        request = build(builder.build());
        Response response = chain.proceed(request);
        if (response.isSuccessful()) {
            onSuccess(response, request);
        } else {
            onFail(response, request);
        }
        return response;
    }

    public Request.Builder interceptBuilder(Request.Builder builder) {
        return builder;
    }

    public Request build(Request request) {
        return request;
    }

    public void onFail(Response response, Request request) {
        // 上报接口请求错误
        if (!TextUtils.isEmpty(reportType) && !TextUtils.isEmpty(reportContent)) {

        }
    }

    public void onSuccess(Response response, Request request) {
        // 上报接口请求成功的耗时
        if (!TextUtils.isEmpty(reportType) && !TextUtils.isEmpty(reportContent) && startTime > 0) {
//            reportContent += "，success，" + Utils.getResponseLevel(startTime);

//            PluLog.e("Umeng report ----------  reportType : " + reportType + " , reportContent : " + reportContent);
        }

    }

    public void setReportInfo(String reportType, String reportContent) {
        this.reportType = reportType;
        this.reportContent = reportContent;
    }
}
