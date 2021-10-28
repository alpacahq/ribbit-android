package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Bank Account model class for user's attached banks.
 * Api call params for bank records are defined here.
 */
@Entity
public class BankAccount implements Parcelable {

    @NonNull
    @PrimaryKey
    public String id;
    @SerializedName("account_owner_name")
    public String ownerName;
    @SerializedName("bank_routing_number")
    public String routingNumber;
    @SerializedName("bank_account_type")
    public String bankAccountType;
    public String status;
    @SerializedName("bank_account_number")
    public String bankAccountNumber;
    @SerializedName("account_id")
    public String accountId;
    public String nickname;

    public BankAccount() {
    }

    protected BankAccount(Parcel in) {
        id = in.readString();
        ownerName = in.readString();
        routingNumber = in.readString();
        bankAccountType = in.readString();
        status = in.readString();
        bankAccountNumber = in.readString();
        accountId = in.readString();
        nickname = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerName);
        dest.writeString(routingNumber);
        dest.writeString(bankAccountType);
        dest.writeString(status);
        dest.writeString(bankAccountNumber);
        dest.writeString(accountId);
        dest.writeString(nickname);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BankAccount> CREATOR = new Creator<BankAccount>() {
        @Override
        public BankAccount createFromParcel(Parcel in) {
            return new BankAccount(in);
        }

        @Override
        public BankAccount[] newArray(int size) {
            return new BankAccount[size];
        }
    };
}
