package com.stockly.android.modules;

import com.stockly.android.BuildConfig;
import com.stockly.android.apis.ApiServices;
import com.stockly.android.apis.OkHttpProvider;
import com.stockly.android.session.UserSession;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 4/16/21.
 * <p>
 * Hilt mechanism for retrofit and Api services through
 * network module.
 */

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    public Retrofit provideRetrofit(OkHttpClient httpClient) {
        return new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//              .addConverterFactory(CustomGsonConverterFactory.create(GsonUtils.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();
    }

    @Provides
    public ApiServices provideApiServices(Retrofit retrofit) {
        return retrofit.create(ApiServices.class);
    }

    @Provides
    public OkHttpClient provideHttpClient(UserSession session) {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpProvider.getUpHttpBuilder(httpBuilder, session);
        OkHttpProvider.setOpenSSL(httpBuilder);
        return httpBuilder.build();
    }
}
