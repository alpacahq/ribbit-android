package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

/**
 * Orders
 * Number of orders of users for buy/sell of ticker stock
 * is presented by server through api to this class.
 */
public class Orders implements Parcelable {

    public String id;
    @SerializedName("client_order_id")
    public String clientOrderId;
    @SerializedName("asset_id")
    public String assetId;
    public String notional;
    public boolean qty;
    @SerializedName("order_type")
    public boolean orderType;
    public String status;
    @SerializedName("time_in_force")
    public String timeInForce;
    public String symbol;
    public boolean type;
    public boolean side;
    //{"buying_power":"0","code":40310000,"cost_basis":"50","message":"insufficient buying power"}
    //this for error
    @Nullable
    public String message;


    protected Orders(Parcel in) {
        id = in.readString();
        clientOrderId = in.readString();
        assetId = in.readString();
        notional = in.readString();
        qty = in.readByte() != 0;
        orderType = in.readByte() != 0;
        status = in.readString();
        timeInForce = in.readString();
        symbol = in.readString();
        type = in.readByte() != 0;
        side = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(clientOrderId);
        dest.writeString(assetId);
        dest.writeString(notional);
        dest.writeByte((byte) (qty ? 1 : 0));
        dest.writeByte((byte) (orderType ? 1 : 0));
        dest.writeString(status);
        dest.writeString(timeInForce);
        dest.writeString(symbol);
        dest.writeByte((byte) (type ? 1 : 0));
        dest.writeByte((byte) (side ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Orders> CREATOR = new Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };
}
