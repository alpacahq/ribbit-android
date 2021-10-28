package com.stockly.android.session;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.stockly.android.BuildConfig;
import com.stockly.android.dao.UserDao;
import com.stockly.android.models.UserAuth;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.PrefUtils;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * User's session for his record. like logged in/out and
 * token expiry/update.
 */
public class UserSession {
    private static final String KEY_TOKEN = BuildConfig.APPLICATION_ID + "_user_token_";
    private static final String LOGGED_IN_USER_ID = BuildConfig.APPLICATION_ID + "_logged_in_user_id";

    private final Context mContext;
    @Inject
    UserDao userDao;


    @Inject
    public UserSession(@ApplicationContext Context appContext, UserDao userDao) {
        this.mContext = appContext;
        this.userDao = userDao;
    }


    public void setUserID(long userID) {
        PrefUtils.setLong(mContext, UserSession.LOGGED_IN_USER_ID, userID);
    }

    public long getUserID() {

        return PrefUtils.getLong(mContext, LOGGED_IN_USER_ID);
    }

    public String getToken() {
        return getOriginalToken(PrefUtils.getString(mContext, KEY_TOKEN));
    }

    public void setToken(String token) {
        PrefUtils.setString(mContext, KEY_TOKEN, modifyToken(token));
    }

    private String modifyToken(String originalToken) {
        originalToken += CommonUtils.getRandomString();
        return originalToken;
    }

    private String getOriginalToken(String modifiedToken) {
        if (modifiedToken == null || TextUtils.isEmpty(modifiedToken))
            return "";
        return modifiedToken.substring(0, modifiedToken.length() - 6);
    }

}
