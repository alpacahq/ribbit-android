package com.stockly.android.modules;

import android.content.Context;

import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.datbase.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * https://developer.android.com/training/data-storage/room#java
 * DataBase module for hilt mechanism by Singleton and provides
 * for database and its tables.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public AppDatabase provideDatabase(@ApplicationContext Context appContext) {
        return AppDatabase.getDatabase(appContext);
    }

    @Singleton
    @Provides
    public UserDao provideUserDao(AppDatabase db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    public TradingProfileDao provideTradingProfileDao(AppDatabase db) {
        return db.tradingProfileDao();
    }

    @Singleton
    @Provides
    public BankAccountDao provideBankAccountDao(AppDatabase db) {
        return db.bankAccountDao();
    }


}
