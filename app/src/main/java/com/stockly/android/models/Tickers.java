package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Tickers a class that represents data for trades
 * and dailyBars for graphical representation.
 */
public class Tickers implements Parcelable {

    public Trade latestTrade;
    public Bar dailyBar;


    protected Tickers(Parcel in) {
        latestTrade = in.readParcelable(Trade.class.getClassLoader());
        dailyBar = in.readParcelable(Bar.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latestTrade, flags);
        dest.writeParcelable(dailyBar, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tickers> CREATOR = new Creator<Tickers>() {
        @Override
        public Tickers createFromParcel(Parcel in) {
            return new Tickers(in);
        }

        @Override
        public Tickers[] newArray(int size) {
            return new Tickers[size];
        }
    };
}
