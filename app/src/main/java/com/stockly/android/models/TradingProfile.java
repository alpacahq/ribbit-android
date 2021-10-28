package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Trading profile represents whole a lot of
 * information broker account like balance, buying power and
 * account number of attached bank etc.
 */
@Entity
public class TradingProfile implements Parcelable {

    @NonNull
    @PrimaryKey
    public String id;
    @SerializedName("account_blocked")
    public String accountBlocked;
    @SerializedName("account_number")
    public String accountNumber;
    @SerializedName("buying_power")
    public String buyingPower;
    public String cash;
    @SerializedName("cash_transferable")
    public String cashTransferable;
    @SerializedName("cash_withdrawable")
    public String cashWithDrawable;
    @SerializedName("clearing_broker")
    public String clearing_broker;
    public String currency;
    @SerializedName("daytrade_count")
    public String dayTradeCount;
    @SerializedName("daytrading_buying_power")
    public String dayTradingBuyingPower;
    public String equity;
    @SerializedName("initial_margin")
    public String initialMargin;
    @SerializedName("last_buying_power")
    public String lastBuyingPower;
    @SerializedName("last_cash")
    public String lastCash;
    @SerializedName("last_daytrade_count")
    public String lastDayTradeCount;
    @SerializedName("last_daytrading_buying_power")
    public String lastDayTradingBuyingPower;
    @SerializedName("last_equity")
    public String lastEquity;
    @SerializedName("last_initial_margin")
    public String lastInitialMargin;
    @SerializedName("last_long_market_value")
    public String lastLongMarketValue;
    public String multiplier;
    public String status;
    @SerializedName("portfolio_value")
    public String portfolioValue;

    public TradingProfile() {
    }

    protected TradingProfile(Parcel in) {
        id = in.readString();
        accountBlocked = in.readString();
        accountNumber = in.readString();
        buyingPower = in.readString();
        cash = in.readString();
        cashTransferable = in.readString();
        cashWithDrawable = in.readString();
        clearing_broker = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accountBlocked);
        dest.writeString(accountNumber);
        dest.writeString(buyingPower);
        dest.writeString(cash);
        dest.writeString(cashTransferable);
        dest.writeString(cashWithDrawable);
        dest.writeString(clearing_broker);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TradingProfile> CREATOR = new Creator<TradingProfile>() {
        @Override
        public TradingProfile createFromParcel(Parcel in) {
            return new TradingProfile(in);
        }

        @Override
        public TradingProfile[] newArray(int size) {
            return new TradingProfile[size];
        }
    };
}
