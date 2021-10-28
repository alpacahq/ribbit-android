package com.stockly.android.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * For Help read this link
 * interface TradingProfileDao
 * <p>
 * methods for this will be used to update,delete,insert etc int user TradingProfile of database
 * https://developer.android.com/training/data-storage/room/async-queries#java
 */
@Dao
public interface TradingProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveTradingProfile(TradingProfile profile);

    @Update
    Completable updateTradingProfile(TradingProfile... profiles);

    @Delete
    void deleteTradingProfile(TradingProfile... profiles);

    @Query("DELETE FROM tradingprofile")
    void deleteAll();

    @Query("SELECT * FROM tradingprofile")
    Observable<List<TradingProfile>> getAll();

    @Query("SELECT * FROM tradingprofile ORDER BY ID")
    List<TradingProfile> loadAllDataByID();

    @Query("SELECT * FROM tradingprofile")
    Single<TradingProfile> getTradingProfile();
}
