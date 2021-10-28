package com.stockly.android.models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.stockly.android.R;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DateUtilz;

/**
 * A class that represents funds adding
 * transfer history and statuses of transactions
 * all attributes required for Transfers by server.
 */
public class Payment implements Parcelable {

    public String id;
    @SerializedName("account_id")
    public String accountId;
    public String direction;
    @SerializedName("relationship_id")
    public String relationshipId;
    public String amount;
    public String status;
    public String type;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("expires_at")
    public String expiresAt;


    protected Payment(Parcel in) {
        id = in.readString();
        relationshipId = in.readString();
        direction = in.readString();
        amount = in.readString();
        status = in.readString();
        createdAt = in.readString();
        accountId = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(relationshipId);
        dest.writeString(direction);
        dest.writeString(amount);
        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeString(accountId);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    @DrawableRes
    public int getTransferDirectionDrawable() {
        if (direction.equalsIgnoreCase("INCOMING")) {
            return R.drawable.ic_credited;
        } else {
            return R.drawable.ic_debited;
        }
    }

    public String getAmount() {
        if (direction.equalsIgnoreCase("INCOMING")) {
            return "+ $" + CommonUtils.round(Double.parseDouble(amount), 2) + " USD";
        } else {
            return "- $" + CommonUtils.round(Double.parseDouble(amount), 2) + " USD";
        }
    }

    public String getTitle() {
        return "" + DateUtilz.formatDateOnly(createdAt);
    }

    @ColorRes
    public int getStatusColor() {
        if (status.equalsIgnoreCase("QUEUED")) {
            return R.color.normal_status_color;
        } else if (status.equalsIgnoreCase("CANCELED")) {
            return R.color.colorError;
        } else {
            return R.color.normal_status_color;
        }
    }

    public String getStatus() {
        if (status.equalsIgnoreCase("QUEUED")) {
            return "Funds Initiated";
        } else if (status.equalsIgnoreCase("CANCELED")) {
            return "Funds Canceled";
        } else {
            if (direction.equalsIgnoreCase("INCOMING")) {
                return "Funds Credited";
            } else {
                return "Funds Debited";

            }
        }
    }
}
