package com.stockly.android.models;

import static com.stockly.android.utils.CommonUtils.amountConversion;
import static com.stockly.android.utils.CommonUtils.round;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.google.gson.annotations.SerializedName;
import com.stockly.android.R;
import com.stockly.android.utils.CommonUtils;

/**
 * A class contain information of ticker/assets and
 * its attributes being used in different api calls
 * for stock details of tickers.
 */
public class Positions implements Parcelable {

    public String id;
    @SerializedName("asset_id")
    public String asset_id;
    @SerializedName("avg_entry_price")
    public String avgEntryPrice;
    public String exchange;
    public String status;
    public String name;
    public String symbol;
    public String qty;
    public String side;
    @SerializedName("market_value")
    public String marketValue;
    @SerializedName("cost_basis")
    public String costBasis;
    @SerializedName("unrealized_pl")
    public String unrealizedPl;
    @SerializedName("unrealized_intraday_pl")
    public String unrealizedIntradayPl;
    @SerializedName("unrealized_plpc")
    public String unrealizedPlpc;
    @SerializedName("unrealized_intraday_plpc")
    public String unrealizedIntradayPlpc;
    @SerializedName("current_price")
    public String currentPrice;
    @SerializedName("lastday_price")
    public String lastdayPrice;
    @SerializedName("change_today")
    public String changeToday;
    @SerializedName("is_watchlisted")
    public boolean isFavourite;
    public Tickers ticker;


    protected Positions(Parcel in) {
        id = in.readString();
        asset_id = in.readString();
        avgEntryPrice = in.readString();
        exchange = in.readString();
        status = in.readString();
        name = in.readString();
        symbol = in.readString();
        qty = in.readString();
        side = in.readString();
        marketValue = in.readString();
        costBasis = in.readString();
        unrealizedPl = in.readString();
        unrealizedIntradayPl = in.readString();
        unrealizedPlpc = in.readString();
        unrealizedIntradayPlpc = in.readString();
        currentPrice = in.readString();
        lastdayPrice = in.readString();
        changeToday = in.readString();
        isFavourite = in.readByte() != 0;
        ticker = in.readParcelable(Tickers.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(asset_id);
        dest.writeString(avgEntryPrice);
        dest.writeString(exchange);
        dest.writeString(status);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeString(qty);
        dest.writeString(side);
        dest.writeString(marketValue);
        dest.writeString(costBasis);
        dest.writeString(unrealizedPl);
        dest.writeString(unrealizedIntradayPl);
        dest.writeString(unrealizedPlpc);
        dest.writeString(unrealizedIntradayPlpc);
        dest.writeString(currentPrice);
        dest.writeString(lastdayPrice);
        dest.writeString(changeToday);
        dest.writeByte((byte) (isFavourite ? 1 : 0));
        dest.writeParcelable(ticker, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Positions> CREATOR = new Creator<Positions>() {
        @Override
        public Positions createFromParcel(Parcel in) {
            return new Positions(in);
        }

        @Override
        public Positions[] newArray(int size) {
            return new Positions[size];
        }
    };

    @Override
    public String toString() {
        return symbol;
    }

    /**
     * @return it returns description of tickers.
     */
    public String getTitleDescription() {
        String titleDescription;
        if (name.contains("Stock") || name.contains("Class")) {
            titleDescription = name.split("\\.", 2)[0] + ".";
        } else {
            titleDescription = name;
        }
        return titleDescription;
    }

    /**
     * @return it returns price of tickers.
     */
    public String getStockPrice() {
        if (ticker != null) {
            return "$" + CommonUtils.round(Double.parseDouble(String.valueOf(ticker.latestTrade.p)), 2);
        }
        return "";
    }

    /**
     * @return it returns Profit/Loss percentage of tickers.
     */
    public String getProfitLossPercentage() {
        return "$" + CommonUtils.round(Double.parseDouble(String.valueOf(Float.parseFloat(unrealizedPlpc) * 100)), 2);
    }

    /**
     * @return it returns color res on basis of percentage up/down of tickers.
     */
    @ColorRes
    public int getPercentageTextColor() {
        if (Double.parseDouble(unrealizedPlpc) >= 0) {
            return R.color.greenColor;
        } else {
            return R.color.colorError;
        }
    }

    /**
     * @return it returns drawable res on basis of percentage up/down of tickers.
     */
    @DrawableRes
    public int getPercentageDrawableCompat() {
        if (Double.parseDouble(unrealizedPlpc) >= 0) {
            return R.drawable.ic_arrow_drop_up;
        } else {
            return R.drawable.ic_baseline_arrow_drop_down_24;
        }
    }

    /**
     * @return it returns drawable graph icon on basis of percentage up/down of tickers.
     */
    @DrawableRes
    public int getGraphIconDrawable() {
        if (Double.parseDouble(unrealizedPlpc) >= 0) {
            return R.drawable.ic_graph_icon;
        } else {
            return R.drawable.ic_graph_icon_down;
        }
    }

    /**
     * @return it returns image of ticker symbol initial.
     */
    public NameInitialsCircleImageView.ImageInfo getImageInfo() {
        return new NameInitialsCircleImageView.ImageInfo
                .Builder("" + symbol.charAt(0))
                .setTextColor(R.color.colorWhite)
                .setTextFont(R.font.inter_semibold)
                .build();
    }

    /**
     * Set statistics of ticker details for day.
     * <p>
     * ticker stats to be shown.
     */
    public String getTickerOpenPrice() {
        return String.format("%s", round(Double.parseDouble(String.valueOf(ticker.dailyBar.o)), 2));
    }

    public String getTickerHighPrice() {
        return String.format("%s", round(Double.parseDouble(String.valueOf(ticker.dailyBar.h)), 2));
    }

    public String getTickerLowPrice() {
        return String.format("%s", round(Double.parseDouble(String.valueOf(ticker.dailyBar.l)), 2));
    }

    public String getTickerVolume() {
        return amountConversion(ticker.dailyBar.v);
    }

}
