package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * BarGraph model class for list bars available of tickers.
 */
public class BarGraph implements Parcelable {

    public List<Bar> bars;

    protected BarGraph(Parcel in) {
        bars = in.createTypedArrayList(Bar.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(bars);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BarGraph> CREATOR = new Creator<BarGraph>() {
        @Override
        public BarGraph createFromParcel(Parcel in) {
            return new BarGraph(in);
        }

        @Override
        public BarGraph[] newArray(int size) {
            return new BarGraph[size];
        }
    };
}
