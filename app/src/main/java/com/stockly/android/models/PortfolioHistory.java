package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * A model class that defined data structure and attribute
 * for portfolio graph data.
 *
 * @apiNote  equity is number of value that being represented on portfolio graph.
 */
public class PortfolioHistory implements Parcelable {

    public String base_value;
    public List<String> equity;


    protected PortfolioHistory(Parcel in) {
        base_value = in.readString();
        equity = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(base_value);
        dest.writeStringList(equity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PortfolioHistory> CREATOR = new Creator<PortfolioHistory>() {
        @Override
        public PortfolioHistory createFromParcel(Parcel in) {
            return new PortfolioHistory(in);
        }

        @Override
        public PortfolioHistory[] newArray(int size) {
            return new PortfolioHistory[size];
        }
    };
}
