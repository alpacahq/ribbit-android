package com.stockly.android.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.stockly.android.models.User;
import com.stockly.android.models.UserAuth;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * For Help read this link
 * interface UserDao
 * <p>
 * methods for this will be used to update,delete,insert etc int user table of database
 * https://developer.android.com/training/data-storage/room/async-queries#java
 */

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveUser(User user);

    @Update
    Completable updateUsers(User... users);

    @Delete
    public void deleteUsers(User... users);

    @Query("DELETE FROM user")
    void deleteAll();

    @Query("SELECT * FROM user")
    Observable<List<User>> getAll();

    @Query("SELECT * FROM User ORDER BY ID")
    List<User> loadAllUsers();

    @Query("SELECT * FROM user WHERE id=:id")
    Single<User> getUserById(long id);
}
