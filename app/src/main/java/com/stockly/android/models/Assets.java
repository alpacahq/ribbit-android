package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Assets
 * a model class used to represent data of assets
 * by using number parameter defined here like, id, name
 * and there representative data types.
 */

@Entity
public class Assets implements Parcelable {
    @NonNull
    @PrimaryKey
    public String id;
    @SerializedName("class")
    public String claas;
    @SerializedName("easy_to_borrow")
    public String easyToBorrow;
    public String exchange;
    public boolean fractionable;
    public boolean marginable;
    public String status;
    public String name;
    public String symbol;
    public boolean tradable;
    public boolean shortable;
    public Tickers ticker;

    public Assets() {
    }

    protected Assets(Parcel in) {
        id = in.readString();
        claas = in.readString();
        easyToBorrow = in.readString();
        exchange = in.readString();
        fractionable = in.readByte() != 0;
        marginable = in.readByte() != 0;
        status = in.readString();
        name = in.readString();
        symbol = in.readString();
        tradable = in.readByte() != 0;
        shortable = in.readByte() != 0;
        ticker = in.readParcelable(Tickers.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(claas);
        dest.writeString(easyToBorrow);
        dest.writeString(exchange);
        dest.writeByte((byte) (fractionable ? 1 : 0));
        dest.writeByte((byte) (marginable ? 1 : 0));
        dest.writeString(status);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeByte((byte) (tradable ? 1 : 0));
        dest.writeByte((byte) (shortable ? 1 : 0));
        dest.writeParcelable(ticker, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Assets> CREATOR = new Creator<Assets>() {
        @Override
        public Assets createFromParcel(Parcel in) {
            return new Assets(in);
        }

        @Override
        public Assets[] newArray(int size) {
            return new Assets[size];
        }
    };
}
