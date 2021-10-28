package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;


/**
 * A class represents errors over network call of apis
 * and its attributes like message to show when occur.
 */
@Keep
public class RetrofitError implements Parcelable {
    public String message;
    public int code;


    public RetrofitError(String message, int code) {
        this.message = message;
        this.code = code;
    }


    protected RetrofitError(Parcel in) {
        message = in.readString();
        code = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeInt(code);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RetrofitError> CREATOR = new Creator<RetrofitError>() {
        @Override
        public RetrofitError createFromParcel(Parcel in) {
            return new RetrofitError(in);
        }

        @Override
        public RetrofitError[] newArray(int size) {
            return new RetrofitError[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "RetrofitError{" +
                "error='" + message + '\'' +
                '}';
    }
}
