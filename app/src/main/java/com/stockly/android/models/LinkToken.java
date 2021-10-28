package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * LinkToken
 * It represents token required for plaid from server for
 * initializing plaid sdk.
 */
public class LinkToken implements Parcelable {
    @SerializedName("link_token")
    public String linkToken;

    protected LinkToken(Parcel in) {
        linkToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(linkToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LinkToken> CREATOR = new Creator<LinkToken>() {
        @Override
        public LinkToken createFromParcel(Parcel in) {
            return new LinkToken(in);
        }

        @Override
        public LinkToken[] newArray(int size) {
            return new LinkToken[size];
        }
    };
}
