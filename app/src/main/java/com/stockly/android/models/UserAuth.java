package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * While user authentication or account creation userAuth
 * and its attribute are being used and User's object to be returned
 * from server.
 */
public class UserAuth implements Parcelable {

    public String token;
    public String expires;
    @SerializedName("refresh_token")
    public String refreshToken;
    public User user;

    protected UserAuth(Parcel in) {
        token = in.readString();
        expires = in.readString();
        refreshToken = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(expires);
        dest.writeString(refreshToken);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserAuth> CREATOR = new Creator<UserAuth>() {
        @Override
        public UserAuth createFromParcel(Parcel in) {
            return new UserAuth(in);
        }

        @Override
        public UserAuth[] newArray(int size) {
            return new UserAuth[size];
        }
    };
}
