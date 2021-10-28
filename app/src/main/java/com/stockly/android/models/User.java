package com.stockly.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * User a class represents user's record.
 * Entity represent it to local DB as User table and
 * attribute as column of table.
 */
@Entity
public class User implements Parcelable {
    @PrimaryKey
    public long id;
    @ColumnInfo(name = "address")
    public String address;
    @SerializedName("first_name")
    @ColumnInfo(name = "first_name")
    public String firstName;
    @SerializedName("last_name")
    @ColumnInfo(name = "last_name")
    public String lastName;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "mobile")
    public String mobile;
    @SerializedName("country_code")
    @ColumnInfo(name = "country_code")
    public String countryCode;
    @ColumnInfo(name = "verified")
    public boolean verified;
    @SerializedName("last_login")
    public String lastLogin;
    @ColumnInfo(name = "active")
    public boolean active;
    @Embedded
    public UserRole role;
    @ColumnInfo(name = "dob_lay")
    public String dob;
    @ColumnInfo(name = "city")
    public String city;
    @ColumnInfo(name = "country")
    public String country;
    @ColumnInfo(name = "state")
    public String state;
    @SerializedName("zip_code")
    public String zipCode;
    @SerializedName("tax_id")
    public String taxId;
    @SerializedName("tax_id_type")
    public String taxIdType;
    @SerializedName("device_id")
    public String deviceId;
    @ColumnInfo(name = "account_id")
    public String account_id;
    @ColumnInfo(name = "account_number")
    public String account_number;
    @ColumnInfo(name = "account_currency")
    public String account_currency;
    @ColumnInfo(name = "account_status")
    public String account_status;
    @SerializedName("funding_source")
    public String fundingSource;
    @SerializedName("employment_status")
    public String employmentStatus;
    @SerializedName("investing_experience")
    public String investingExperience;
    @SerializedName("public_shareholder")
    public String publicShareholder;
    @SerializedName("another_brokerage")
    public String anotherBrokerage;
    @SerializedName("profile_completion")
    public String profileCompletion;
    public String bio;
    @SerializedName("facebook_url")
    public String facebookUrl;
    @SerializedName("twitter_url")
    public String twitterUrl;
    @SerializedName("instagram_url")
    public String instagramUrl;
    @SerializedName("public_portfolio")
    public String publicPortfolio;
    @SerializedName("employer_name")
    public String employerName;
    public String occupation;
    @SerializedName("unit_apt")
    public String unitApt;
    @SerializedName("shareholder_company_name")
    public String shareholderCompanyName;
    @SerializedName("stock_symbol")
    public String stockSymbol;
    @SerializedName("brokerage_firm_name")
    public String brokerageFirmName;
    @SerializedName("brokerage_firm_employee_name")
    public String brokerageEmployeeName;
    @SerializedName("brokerage_firm_employee_relationship")
    public String brokerageEmployeeRelationship;
    public String avatar;
    @SerializedName("referred_by")
    public String referredBy;
    @SerializedName("referral_code")
    public String referralCode;
    @SerializedName("watchlist_id")
    public String watchlistId;
    @SerializedName("per_account_limit")
    public int perAccountLimit;

    public User() {

    }


    protected User(Parcel in) {
        id = in.readLong();
        address = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        username = in.readString();
        email = in.readString();
        mobile = in.readString();
        countryCode = in.readString();
        verified = in.readByte() != 0;
        lastLogin = in.readString();
        active = in.readByte() != 0;
        role = in.readParcelable(UserRole.class.getClassLoader());
        dob = in.readString();
        city = in.readString();
        country = in.readString();
        state = in.readString();
        zipCode = in.readString();
        taxId = in.readString();
        taxIdType = in.readString();
        deviceId = in.readString();
        account_id = in.readString();
        account_number = in.readString();
        account_currency = in.readString();
        account_status = in.readString();
        fundingSource = in.readString();
        employmentStatus = in.readString();
        investingExperience = in.readString();
        publicShareholder = in.readString();
        anotherBrokerage = in.readString();
        profileCompletion = in.readString();
        bio = in.readString();
        facebookUrl = in.readString();
        twitterUrl = in.readString();
        instagramUrl = in.readString();
        publicPortfolio = in.readString();
        employerName = in.readString();
        occupation = in.readString();
        unitApt = in.readString();
        shareholderCompanyName = in.readString();
        stockSymbol = in.readString();
        brokerageFirmName = in.readString();
        brokerageEmployeeName = in.readString();
        brokerageEmployeeRelationship = in.readString();
        avatar = in.readString();
        referredBy = in.readString();
        referralCode = in.readString();
        watchlistId = in.readString();
        perAccountLimit = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(countryCode);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeString(lastLogin);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeParcelable(role, flags);
        dest.writeString(dob);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(zipCode);
        dest.writeString(taxId);
        dest.writeString(taxIdType);
        dest.writeString(deviceId);
        dest.writeString(account_id);
        dest.writeString(account_number);
        dest.writeString(account_currency);
        dest.writeString(account_status);
        dest.writeString(fundingSource);
        dest.writeString(employmentStatus);
        dest.writeString(investingExperience);
        dest.writeString(publicShareholder);
        dest.writeString(anotherBrokerage);
        dest.writeString(profileCompletion);
        dest.writeString(bio);
        dest.writeString(facebookUrl);
        dest.writeString(twitterUrl);
        dest.writeString(instagramUrl);
        dest.writeString(publicPortfolio);
        dest.writeString(employerName);
        dest.writeString(occupation);
        dest.writeString(unitApt);
        dest.writeString(shareholderCompanyName);
        dest.writeString(stockSymbol);
        dest.writeString(brokerageFirmName);
        dest.writeString(brokerageEmployeeName);
        dest.writeString(brokerageEmployeeRelationship);
        dest.writeString(avatar);
        dest.writeString(referredBy);
        dest.writeString(referralCode);
        dest.writeString(watchlistId);
        dest.writeInt(perAccountLimit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
