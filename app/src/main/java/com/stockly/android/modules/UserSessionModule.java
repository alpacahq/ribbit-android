package com.stockly.android.modules;

import android.content.Context;

import com.stockly.android.dao.UserDao;
import com.stockly.android.session.UserSession;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * Hilt mechanism for User's session that is provided
 * as module.
 */
@Module
@InstallIn(SingletonComponent.class)
public class UserSessionModule {
    @Provides
    public UserSession provideUserSession(@ApplicationContext Context appContext, UserDao userDao) {
        return new UserSession(appContext, userDao);
    }

}
