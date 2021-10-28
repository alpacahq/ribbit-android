package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;


/**
 * Error a class represent error over a network call.
 */
public class Errors implements Parcelable {
    public int code;

    public Errors(String message, int code) {
        this.code = code;
    }


    protected Errors(Parcel in) {
        code = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Errors> CREATOR = new Creator<Errors>() {
        @Override
        public Errors createFromParcel(Parcel in) {
            return new Errors(in);
        }

        @Override
        public Errors[] newArray(int size) {
            return new Errors[size];
        }
    };

}
