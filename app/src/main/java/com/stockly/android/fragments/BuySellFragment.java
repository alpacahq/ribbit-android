package com.stockly.android.fragments;

import static com.stockly.android.utils.CommonUtils.calculatePrice;
import static com.stockly.android.utils.CommonUtils.calculateQuantity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.databinding.FragmentBuySellBinding;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.fragments.wallet.AddFundsFragment;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Orders;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DecimalDigitsInputFilter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * BuySellFragment
 * It represents User's option to Buy/Sell stocks of Assets/Ticker.
 */
@AndroidEntryPoint
public class BuySellFragment extends NetworkFragment {
    private FragmentBuySellBinding mBinding;
    private Positions asset;
    private String tag;
    private User mUser;
    @Inject
    UserSession mUserSession;
    private BankAccount accounts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        // bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            asset = bundle.getParcelable("asset");
            tag = bundle.getString("tag");
        }

        if (asset == null)
            throw new IllegalArgumentException("Asset null");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_buy_sell;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentBuySellBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);
        mBinding.toolbar.save.setVisibility(View.GONE);
        // set toolbar title based on tag
        if (tag.equalsIgnoreCase(getString(R.string.buy))) {
            mBinding.toolbar.title.setText(getString(R.string.purchase_stock));
            mBinding.buySell.setText(getString(R.string.buy_x, asset.symbol));
        } else {
            mBinding.toolbar.title.setText(getString(R.string.sell_stock));
            mBinding.buySell.setText(getString(R.string.sell_x, asset.symbol));
        }

        getUser();
        // set image of ticker/asset
        updateImage(mBinding.winIcon, asset);

        mBinding.title.setText(asset.symbol);
        if (asset.name.contains("Stock") || asset.name.contains("Class")) {
            mBinding.desc.setText(String.format("%s.", asset.name.split("\\.", 2)[0]));
        } else {
            mBinding.desc.setText(asset.name);
        }

        // check if market value then set else set value of latest trade of ticker.
        if (asset.marketValue != null) {
            mBinding.tickerPrice.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(asset.marketValue), 2)));
        } else if (asset.ticker != null) {
            mBinding.tickerPrice.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(String.valueOf(asset.ticker.latestTrade.p)), 2)));
        }

        // set selected tab on basis of tag buy/sell.
        if (tag.equalsIgnoreCase(getString(R.string.buy))) {
            mBinding.tabLayout.getTabAt(0).select();
        } else {
            mBinding.fundsLayout.setVisibility(View.GONE);
            mBinding.tabLayout.getTabAt(1).select();
        }

        // set filter to allow input to two decimal places
        mBinding.price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});

        mBinding.price.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mUser.account_status.equalsIgnoreCase("APPROVED")) {
                    mBinding.buySell.setEnabled(charSequence.length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

//        if (PrefUtils.getNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Limit").equalsIgnoreCase("Limit")) {
//            mBinding.toggleValue.setChecked(true);
//            mBinding.marketToggle.setTextColor(Color.parseColor("#B4C6CC"));
//            mBinding.limitToggle.setTextColor(getResources().getColor(R.color.textColorSubHeading));
//            mBinding.priceText.setText("LIMIT PRICE");
//            mBinding.price.setEnabled(true);
//        } else {
//            mBinding.toggleValue.setChecked(false);
//            mBinding.limitToggle.setTextColor(Color.parseColor("#B4C6CC"));
//            mBinding.marketToggle.setTextColor(getResources().getColor(R.color.textColorSubHeading));
//            mBinding.priceText.setText("PRICE");
//            mBinding.price.setEnabled(false);
//        }

//        mBinding.toggleValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    PrefUtils.setNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Limit");
//                    mBinding.marketToggle.setTextColor(Color.parseColor("#B4C6CC"));
//                    mBinding.limitToggle.setTextColor(getResources().getColor(R.color.textColorSubHeading));
//                    mBinding.priceText.setText("LIMIT PRICE");
//                    mBinding.price.setText("0");
//                    mBinding.price.setEnabled(true);
//                } else {
//                    PrefUtils.setNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Market");
//                    mBinding.limitToggle.setTextColor(Color.parseColor("#B4C6CC"));
//                    mBinding.marketToggle.setTextColor(getResources().getColor(R.color.textColorSubHeading));
//                    mBinding.priceText.setText("PRICE");
//                    mBinding.price.setEnabled(false);
//                    calculatePrice(mBinding.quantity.getText().toString());
//                }
//            }
//        });

        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String s = tab.getText().toString();
                if (s.equalsIgnoreCase(getString(R.string.buy))) {
                    mBinding.toolbar.title.setText(R.string.purchase_stock);
                    mBinding.buySell.setText(getString(R.string.buy_x, asset.symbol));
                    tag = getString(R.string.buy);
                    mBinding.fundsLayout.setVisibility(View.VISIBLE);
                } else if (s.equalsIgnoreCase(getString(R.string.sell))) {
                    mBinding.toolbar.title.setText(getString(R.string.sell_stock));
                    mBinding.buySell.setText(getString(R.string.sell_x, asset.symbol));
                    tag = getString(R.string.sell);
                    mBinding.fundsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mBinding.buySell.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mBinding.price.getText().toString())) {
                HashMap<String, Object> body = new HashMap<>();
                body.put("symbol", asset.symbol);
//                if (PrefUtils.getNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Limit").equalsIgnoreCase("Limit")) {
//                    body.put("type", "limit");
//                    body.put("limit_price", CommonUtils.getValue(mBinding.price.getText().toString()));
//                    body.put("qty", CommonUtils.getValue(mBinding.quantity.getText().toString()));
//                } else {
//                }
                body.put("type", "market");
                body.put("notional", CommonUtils.getValue(mBinding.price.getText().toString()));
                body.put("time_in_force", "day");
                if (tag.equalsIgnoreCase(getString(R.string.buy))) {
                    body.put("side", "buy");
                } else {
                    body.put("side", "sell");
                }
                buySellAsset(body);
            } else {
                mBinding.price.setError(getResources().getString(R.string.error_non_empty_field));
            }
        });

//        mBinding.quantity.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//                if (!TextUtils.isEmpty(mBinding.quantity.getText().toString())) {
//                    int value = Integer.parseInt(mBinding.quantity.getText().toString());
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        if (event.getRawX() >= (mBinding.quantity.getRight() - mBinding.quantity.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                            // your action here
//                            value++;
//                            mBinding.quantity.setText("" + value);
//                            return true;
//                        } else if (event.getX() <= (mBinding.quantity.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                            // your action here
//                            if (value > 0) {
//                                value--;
//                                mBinding.quantity.setText("" + value);
//                            }
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//

        final Context activityRef = this.getActivity();
        mBinding.quantity.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!PrefUtils.getNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Limit").equalsIgnoreCase("Limit")) {
//                }
                if (mBinding.quantity.hasFocus()) {
                    try {
                        calculatePrice(charSequence.toString(), asset, mBinding.price);
                    }
                    catch (Throwable th) {
                        Toast.makeText(activityRef, "'"+ charSequence + "'" + " is not a valid price", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mBinding.price.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!PrefUtils.getNonDeleteAbleString(requireActivity(), CommonKeys.MARKET_LIMIT, "Limit").equalsIgnoreCase("Limit")) {
//                }
                if (mBinding.price.hasFocus()) {
                    try {
                        calculateQuantity(charSequence.toString(), asset, mBinding.quantity);
                    }
                    catch (Throwable th) {
                        Toast.makeText(activityRef, "'"+ charSequence + "'" + " is not a valid quantity", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mBinding.addFund.setOnClickListener(v -> {
            if (mUser.account_status.equalsIgnoreCase("APPROVED")) {
                checkAccounts();
            }
        });
    }


    @Override
    public void onResume() {
        getUser();
        getBankAccounts();
        getTradingProfile();
        super.onResume();
    }

    /**
     * get user from local DB.
     */
    public void getUser() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                mUser = user;
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * Call server for buy/sell stock of Asset/Ticker.
     *
     * @param body having params required.
     */
    private void buySellAsset(HashMap body) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().buyAsset(body), new NetworkFragment.CallBack<Orders>() {
            @Override
            public void onSuccess(Orders t) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                if (t.message != null) {
                    showErrorMessage(mBinding.errorMessage, t.message);
//                    Toast.makeText(requireActivity(), "" + t.message, Toast.LENGTH_SHORT).show();
                } else {
                    if (tag.equalsIgnoreCase(getString(R.string.buy))) {
                        Toast.makeText(requireActivity(), R.string.purchased_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity(), R.string.sold_success, Toast.LENGTH_SHORT).show();
                    }

                    requireActivity().finish();
                }

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                if (!isInternetIssue) {
                    showErrorMessage(mBinding.errorMessage, error.message);
                    return true;
                }
                return super.onError(error, isInternetIssue);

            }
        });

    }

    /**
     * Checks bank account is attached.
     */
    private void checkAccounts() {
        Bundle bundle = new Bundle();
        if (accounts != null) {
//            replaceFragment(new AddFundsFragment());
            bundle.putString(CommonKeys.KEY_FUNDS, "transaction");
            ActivityUtils.launchFragment(requireActivity(), AddFundsFragment.class, bundle);
        } else {
//            BankIntroFragment fragment = new BankIntroFragment();
            bundle.putString("path", "funds");
//            fragment.setArguments(bundle);
//            replaceFragment(fragment);
            ActivityUtils.launchFragment(requireActivity(), BankIntroFragment.class, bundle);
        }
    }

    /**
     * get attached bank account from local DB.
     */
    public void getBankAccounts() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        requestSingle(accountDao.getBankAccount(), new CallBackSingle<BankAccount>() {
            @Override
            public void onSuccess(@NotNull BankAccount account) {
                accounts = account;
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                super.onError(e);
            }
        });
    }

    /**
     * get user account info from trading profile from local DB.
     */
    public void getTradingProfile() {
        requestSingle(profileDao.getTradingProfile(), new CallBackSingle<TradingProfile>() {
            @Override
            public void onSuccess(@NotNull TradingProfile profile) {
                updateBalance(profile);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });

    }

    /**
     * Update balance on UI of user's personal balance.
     *
     * @param profile from local DB Trading profile.
     */
    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.balance.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(profile.cash), 4)));
        }
    }


}
