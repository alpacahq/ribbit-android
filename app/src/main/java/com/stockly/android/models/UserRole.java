package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * While in User class userRole represents
 * to access level being allowed to user for particular.
 */

@Entity
public class UserRole implements Parcelable {

    @Embedded
    public String access_level;
    @Embedded
    public String name;

    public UserRole() {
    }

    protected UserRole(Parcel in) {
        access_level = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_level);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRole> CREATOR = new Creator<UserRole>() {
        @Override
        public UserRole createFromParcel(Parcel in) {
            return new UserRole(in);
        }

        @Override
        public UserRole[] newArray(int size) {
            return new UserRole[size];
        }
    };
}
