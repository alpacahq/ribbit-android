package com.stockly.android.apis;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.stockly.android.AppController;
import com.stockly.android.BuildConfig;
import com.stockly.android.KycActivity;
import com.stockly.android.session.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * OkHttpProvider A class for Handling HTTP calls
 * <p>
 * OkHttp performs best when you create a single OkHttpClient instance and reuse it for all of your
 * HTTP calls. This is because each client holds its own connection pool and thread pools.
 * Reusing connections and threads reduces latency and saves memory. Conversely, creating a client
 * for each request wastes resources on idle pools.
 */
public class OkHttpProvider {
    private static final String APPLICATION_CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String APPLICATION_CONTENT_TYPE_MULTI_PART = "multipart/form-data";
    private static final String APPLICATION_CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded;charset=utf-8";

    /**
     * OkHttpBuilder function to to create request for Http client builder
     * to build request using headers and passing auth header as well by getting user's saved token.
     * If api call have new token in header it will update and will be used in next call so on.
     * In case of response.code() == 204 || response.code() == 205, while response body is empty
     * so we create custom body response for success.
     */
    public static void getUpHttpBuilder(OkHttpClient.Builder httpBuilder, UserSession session) {
        configClient(httpBuilder);
        httpBuilder.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder accept = original.newBuilder()
                    .header("Accept", APPLICATION_CONTENT_TYPE)
                    .header("Content-Type", APPLICATION_CONTENT_TYPE_URL_ENCODED);
            String token = session.getToken();
            if (!TextUtils.isEmpty(token)) {
                if (BuildConfig.DEBUG) {
                    Log.d(">>>Token", "Bearer " + token);
                }
                accept.addHeader("Authorization", "Bearer " + token);
            }
            Request.Builder requestBuilder = accept
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();

            Response response = chain.proceed(request);

            Headers headers = response.headers();

            String newToken = headers.get("New-Token");
            if (!TextUtils.isEmpty(newToken)) {
                session.setToken(headers.get("New-Token"));
            }
//            Log.d(">>>OkHttp", "getUpHttpBuilder: " + headers.get("New-Token") + " " + headers.values("Content-Type") + "" + headers.values("New-Token"));

            if (response.isSuccessful() && (response.code() == 204 || response.code() == 205)) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", true);
                    ResponseBody body = ResponseBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

                    response = response
                            .newBuilder()
                            .code(200)
                            .body(body)
                            .build();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return response;
        });
    }

    /**
     * configClient function to to create request for OkHttpClient configuration
     */
    private static void configClient(OkHttpClient.Builder httpBuilder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.level(HttpLoggingInterceptor.Level.BODY);
            httpBuilder.addInterceptor(logging);
        }
        httpBuilder.connectTimeout(45, TimeUnit.SECONDS);
        httpBuilder.readTimeout(45, TimeUnit.SECONDS);
    }

    /**
     * SSL
     * setOpenSSL function takes
     *
     * @param builder and configures SSL security certification
     */
    public static void setOpenSSL(OkHttpClient.Builder builder) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("okHttp", ": ");
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("okHttp", ": ");
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("okHttp", ": ");
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("okHttp", ": ");
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}