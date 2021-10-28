package com.stockly.android.datbase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.stockly.android.BuildConfig;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;


/**
 * AppDatabase
 * The abstract class which is annotated with {@link Database} and extends
 * * {@link RoomDatabase}.
 * <p>
 * Database class have entities representing their model classes names as tables.
 * version and abstract method for entities dao classes representation.
 * For Help read this link
 * https://developer.android.com/training/data-storage/room/async-queries#java
 */
@Database(entities = {User.class, TradingProfile.class, BankAccount.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract TradingProfileDao tradingProfileDao();

    public abstract BankAccountDao bankAccountDao();

    /**
     * GetDatabase
     * This function provides database reference for usage in different classes or hilt for DataBaseModule class
     * so can use Database using hilt mechanism
     * @link provideDatabase in DatabaseModule class.
     */
    public static AppDatabase getDatabase(Context appContext) {
        return Room.databaseBuilder(appContext,
                AppDatabase.class, "db_" + BuildConfig.APPLICATION_ID).allowMainThreadQueries().build();
    }
}