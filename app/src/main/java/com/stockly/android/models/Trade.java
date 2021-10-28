package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Trade represents data for tickers price and other attributes.
 */
public class Trade implements Parcelable {

    public float i;
    public float p;
    public float s;


    protected Trade(Parcel in) {
        i = in.readFloat();
        p = in.readFloat();
        s = in.readFloat();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(i);
        dest.writeFloat(p);
        dest.writeFloat(s);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trade> CREATOR = new Creator<Trade>() {
        @Override
        public Trade createFromParcel(Parcel in) {
            return new Trade(in);
        }

        @Override
        public Trade[] newArray(int size) {
            return new Trade[size];
        }
    };
}
