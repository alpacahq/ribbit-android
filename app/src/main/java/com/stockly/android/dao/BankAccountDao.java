package com.stockly.android.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.stockly.android.models.BankAccount;
import com.stockly.android.models.TradingProfile;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * For Help read this link
 * interface BankAccountDao
 * <p>
 * methods for this will be used to update,delete,insert etc int user BankAccount of database
 * https://developer.android.com/training/data-storage/room/async-queries#java
 */
@Dao
public interface BankAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAccount(BankAccount account);

    @Update
    Completable updateAccount(BankAccount... accounts);

    @Delete
    void deleteAccount(BankAccount... accounts);

    @Query("DELETE FROM bankaccount")
    void deleteAll();

    @Query("SELECT * FROM bankaccount")
    Observable<List<BankAccount>> getAll();

    @Query("SELECT * FROM bankaccount ORDER BY ID")
    List<BankAccount> loadAllDataByID();

    @Query("SELECT * FROM bankaccount")
    Single<BankAccount> getBankAccount();
}
