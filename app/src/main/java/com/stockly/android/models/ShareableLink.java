package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * A referral code and sharable link from server.
 * These attribute used when user need to share it with friends.
 */

public class ShareableLink implements Parcelable {
    public String url;
    public String code;


    protected ShareableLink(Parcel in) {
        url = in.readString();
        code = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShareableLink> CREATOR = new Creator<ShareableLink>() {
        @Override
        public ShareableLink createFromParcel(Parcel in) {
            return new ShareableLink(in);
        }

        @Override
        public ShareableLink[] newArray(int size) {
            return new ShareableLink[size];
        }
    };
}
