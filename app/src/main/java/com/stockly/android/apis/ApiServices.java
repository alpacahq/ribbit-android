package com.stockly.android.apis;


import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Delete;

import com.google.gson.JsonObject;
import com.stockly.android.models.AccountStats;
import com.stockly.android.models.Assets;
import com.stockly.android.models.Avatar;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.BarGraph;
import com.stockly.android.models.City;
import com.stockly.android.models.Country;
import com.stockly.android.models.LinkToken;
import com.stockly.android.models.Orders;
import com.stockly.android.models.Payment;
import com.stockly.android.models.PortfolioHistory;
import com.stockly.android.models.Positions;
import com.stockly.android.models.ReferralCode;
import com.stockly.android.models.ShareableLink;
import com.stockly.android.models.State;
import com.stockly.android.models.Success;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.models.UserAuth;
import com.stockly.android.models.WatchList;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * interface for Api Services
 *
 * <p>This interface provides a Api call function with api method name and takes Parameter of
 * Different type depending on body of api call.
 * Observable take <T> parameters and return source of data that object/list etc from api call
 * and each function name represent its functionality/usage</p>
 */
public interface ApiServices {

    @POST("signup")
    Observable<UserAuth> signUp(@Body HashMap<String, Object> body);

    @POST("login")
    Observable<UserAuth> login(@Body HashMap<String, Object> body);

    @POST("forgot-password")
    Observable<UserAuth> forgotPass(@Body HashMap<String, Object> body);

    @POST("recover-password")
    Observable<UserAuth> resetPass(@Body HashMap<String, Object> body);

    @GET("verification/{otp}")
    Observable<UserAuth> verifyOTP(@Path("otp") String otp);

    @POST("magic")
    Observable<UserAuth> magicLink(@Body HashMap<String, Object> body);

    @POST("refresh")
    Observable<UserAuth> refreshToken(@Body HashMap<String, Object> body);

    @PATCH("v1/profile")
    Observable<User> updateProfile(@Body HashMap<String, Object> body);

    @Multipart
    @POST("/v1/profile/avatar")
    Observable<Avatar> updateProfileImage(@Part() MultipartBody.Part file);

    @DELETE("/v1/profile/avatar")
    Observable<Avatar> deleteProfileImage();


    @GET("v1/countries")
    Observable<List<Country>> getCountries();

    @GET("v1/countries/{country_code}/states")
    Observable<List<State>> getStates(@Path("country_code") String countryCode);

    @GET("v1/countries/{country_code}/states/{state_code}/cities")
    Observable<List<City>> getCities(@Path("country_code") String countryCode, @Path("state_code") String stateCode);

    @GET("v1/plaid/create_link_token")
    Observable<LinkToken> getLinkToken();

    @POST("v1/plaid/set_access_token")
    Observable<BankAccount> setAccessToken(@Body HashMap<String, Object> body);

    @GET("v1/plaid/recipient_banks")
    Observable<List<BankAccount>> getBankAccounts();

    @GET("v1/account/stats")
    Observable<AccountStats> getAccountStats();

    @POST("v1/account/sign")
    Observable<User> signAccount();

    @FormUrlEncoded
    @POST("v1/transfer/bank/{bank_id}/deposit")
    Observable<Payment> addFunds(@Path("bank_id") String bankId, @Field("amount") String amount);


    @DELETE("v1/plaid/recipient_banks/{bank_id}")
    Observable<Success> deAttachBank(@Path("bank_id") String bankId);

    @GET("v1/transfer/")
    Observable<List<Payment>> getTransfersHistory();

    @POST("v1/watchlist")
    Observable<WatchList> setWatchList(@Body HashMap<String, Object> body);

    @GET("v1/watchlist")
    Observable<WatchList> getWatchlist();

    @GET("v1/account/trading-profile")
    Observable<TradingProfile> getTradingProfile();

    @GET("v1/assets/")
    Observable<List<Positions>> getAssetsList(@Query("q") String q);

    @GET("v1/assets/")
    Observable<List<Positions>> getAssetsList();

    @GET("v1/positions")
    Observable<List<Positions>> getPositionsList();

    @DELETE("v1/watchlist/{symbol}")
    Observable<WatchList> removeWatchlist(@Path("symbol") String symbol);

    @GET("referral_code/verify/{referral_code}")
    Observable<ReferralCode> verifyReferralCode(@Path("referral_code") String referralCode);

    @GET("v1/profile/shareable-link")
    Observable<ShareableLink> getShareableLink();

    @POST("v1/orders")
    Observable<Orders> buyAsset(@Body HashMap<String, Object> body);

    @GET("v1/market/stocks/{symbol}/bars")
    Observable<BarGraph> getGraphData(@Path("symbol") String symbol, @QueryMap HashMap<String, Object> body);

    @GET("v1/account/portfolio/history")
    Observable<PortfolioHistory> getPortfolioHistory(@QueryMap HashMap<String, Object> body);
}