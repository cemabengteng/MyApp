package com.example.data.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by plu on 2016/8/30.
 */
public class RetrofitHelper {
    private Map<Class<?>, String> apiMap;
    private Set<Interceptor> defInterceptors;
    private CookieManager cookieManager;

    @Inject
    public RetrofitHelper(Map<Class<?>, String> apiMap, Set<Interceptor> defInterceptors, CookieManager cookieManager) {
        this.apiMap = apiMap;
        this.defInterceptors = defInterceptors;
        this.cookieManager = cookieManager;
    }


    public <T> T createService(Class<T> apiClazz, Interceptor... interceptors) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (defInterceptors != null) {
            for (Interceptor interceptor : defInterceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        OkHttpClient okHttpClient = builder.sslSocketFactory(getSslSocketFactory()).cookieJar(cookieManager).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(apiMap.get(apiClazz)).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(new CustomConverterFactory())
                .client(okHttpClient).build();

        return retrofit.create(apiClazz);
    }

    private static SSLSocketFactory getSslSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (Exception e) {

        }
        return sslSocketFactory;
    }

}
