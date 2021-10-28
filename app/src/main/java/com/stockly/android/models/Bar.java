package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bar model class for bars available for trading of tickers.
 * Api call params for latest bars are defined here.
 */
public class Bar implements Parcelable {

    public float c;
    public float h;
    public float l;
    public float o;
    public String t;
    public long v;


    public Bar(Parcel in) {
        c = in.readFloat();
        h = in.readFloat();
        l = in.readFloat();
        o = in.readFloat();
        t = in.readString();
        v = in.readLong();
    }

    public Bar() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(c);
        dest.writeFloat(h);
        dest.writeFloat(l);
        dest.writeFloat(o);
        dest.writeString(t);
        dest.writeLong(v);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bar> CREATOR = new Creator<Bar>() {
        @Override
        public Bar createFromParcel(Parcel in) {
            return new Bar(in);
        }

        @Override
        public Bar[] newArray(int size) {
            return new Bar[size];
        }
    };
}
