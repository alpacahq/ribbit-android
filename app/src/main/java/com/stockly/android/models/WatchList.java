package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.stockly.android.utils.ListTypeConverter;

import java.util.List;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * WatchList it represent number of assets/ticker
 * save as favourites .
 */
@Entity
public class WatchList implements Parcelable {
    @NonNull
    @PrimaryKey
    public String id;
    @SerializedName("account_id")
    public String accountId;
    @Nullable
    public String name;
    @TypeConverters(ListTypeConverter.class)
    public List<Positions> assets;

    public WatchList() {
    }

    protected WatchList(Parcel in) {
        id = in.readString();
        accountId = in.readString();
        name = in.readString();
        assets = in.createTypedArrayList(Positions.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accountId);
        dest.writeString(name);
        dest.writeTypedList(assets);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatchList> CREATOR = new Creator<WatchList>() {
        @Override
        public WatchList createFromParcel(Parcel in) {
            return new WatchList(in);
        }

        @Override
        public WatchList[] newArray(int size) {
            return new WatchList[size];
        }
    };
}
