package com.stockly.android;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.stockly.android.apis.ApiServices;
import com.stockly.android.dao.UserDao;
import com.stockly.android.datbase.AppDatabase;
import com.stockly.android.session.UserSession;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;


/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * AppController represents app level and defined
 * functions and instance.
 */
@HiltAndroidApp
public class AppController extends Application {

    @Inject
    ApiServices mApiServices;
    @Inject
    UserSession mSession;

    private AppDatabase databaseInstance;


    public static AppController get(Context context) {
        return (AppController) context.getApplicationContext();
    }

    public static AppController getInstance(Context context) {
        return AppController.get(context);
    }

    public static AppController getInstance(Fragment context) {
        return AppController.get(context.requireContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseInstance = AppDatabase.getDatabase(getApplicationContext());

    }

    public ApiServices getApiServices() {
        return mApiServices;
    }

    public AppDatabase getDatabaseInstance() {
        return databaseInstance;
    }

    /**
     * Use Hilt mechanism to getUserSession instance.
     */
    @Deprecated
    public UserSession getUserSession() {
        return mSession;
    }


}
