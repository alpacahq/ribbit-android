package com.stockly.android.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stockly.android.BuildConfig;
import com.stockly.android.R;
import com.stockly.android.adapter.MarketListAdapter;
import com.stockly.android.adapter.MyFavouritesAdapter;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.databinding.FragmentHomeBinding;

import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.PortfolioHistory;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.ShareableLink;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.models.WatchList;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.PrefUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * HomeFragment
 * It represent user home screen for portfolio graph and user's info
 * and favourites list.
 */

@AndroidEntryPoint
public class HomeFragment extends NetworkFragment implements View.OnClickListener {

    private FragmentHomeBinding mBinding;
    private String code;
    private String url;
    @Inject
    UserSession mUserSession;
    @Inject
    TradingProfileDao profileDao;
    @Inject
    BankAccountDao accountDao;
    private User mUser;
    private MyFavouritesAdapter mFavouriteAdapter;
    private MarketListAdapter mMarketListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorWindowBackground));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentHomeBinding.bind(view);
//        setUpToolBar(mBinding.toolbar.toolbar, false);
        accountDao.deleteAll();
        profileDao.deleteAll();
        getUser();
//        mBinding.toolbar.bellIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtils.launchFragment(requireActivity(), NotificationFragment.class);
//            }
//        });

//        mBinding.prizeCard.setOnClickListener(view1 -> ActivityUtils.launchFragment(requireActivity(), DailyGiveAwayFragment.class));

//        mBinding.inviteFriends.setOnClickListener(v -> {
//            if (!TextUtils.isEmpty(url))
//                shareLink(CommonUtils.getValue(url), CommonUtils.getValue(code));
//        });


//        mBinding.watchlistItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        // favourite adapter
        mFavouriteAdapter = new MyFavouritesAdapter(requireActivity(), (s, position) -> {

        });
        mBinding.watchlistItems.setAdapter(mFavouriteAdapter);

//        // market adapter
//        mBinding.marketItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        mMarketListAdapter = new MarketListAdapter(requireActivity(), new ItemClickListener<Positions>() {
//            @Override
//            public void onItemClick(Positions s, int position) {
//
//            }
//        });
//        mBinding.marketItems.setAdapter(mMarketListAdapter);

        // fetch favourite
        getWatchlist();

        mBinding.viewAll.setOnClickListener(v -> {
            BottomNavigationView view1 = getActivity().findViewById(R.id.bottom_navigation);
            view1.setSelectedItemId(R.id.profile);
            replaceFragment(new ProfileFragment());
        });

//        account stats
//        getAccountStates();
//        getShareableLink();


        mBinding.connectBank.setOnClickListener(v -> {
            if (mUser.account_status.equalsIgnoreCase("APPROVED")) {
                ActivityUtils.launchFragment(requireActivity(), BankIntroFragment.class);
            }
        });

        mBinding.day1.setOnClickListener(this);
        mBinding.week1.setOnClickListener(this);
        mBinding.month1.setOnClickListener(this);

        HashMap<String, Object> body = new HashMap<>();
        body.put("period", "1D");
        body.put("timeframe", "1Min");
//        body.put("date_end", date_end);
        getPortfolioGraph(body, "1D");
    }

    /**
     * Click listener for legends.
     *
     * @param v view.
     */
    @Override
    public void onClick(View v) {
        HashMap<String, Object> body = new HashMap<>();

        int id = v.getId();
        if (id == R.id.day1) {
            body.put("period", "1D");
            body.put("timeframe", "1Min");
            getPortfolioGraph(body, "1D");
        } else if (id == R.id.week1) {
            body.put("period", "1W");
            body.put("timeframe", "1H");
            getPortfolioGraph(body, "1W");
        } else if (id == R.id.month1) {
            body.put("period", "1M");
            body.put("timeframe", "1D");
            getPortfolioGraph(body, "1M");
        } else {
            throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    /**
     * Get Portfolio graph data from server.
     *
     * @param body      params required.
     * @param timeFrame for time period.
     */
    private void getPortfolioGraph(HashMap body, String timeFrame) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getPortfolioHistory(body), new CallBack<PortfolioHistory>() {
            @Override
            public void onSuccess(PortfolioHistory t) {
                if (t.equity.size() != 0) {
                    renderGraphData(t.equity, timeFrame);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });

    }

    /**
     * get list of favourite assets and show from server.
     */
    private void getWatchlist() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getWatchlist(), new NetworkFragment.CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList list) {

                mFavouriteAdapter.setData(list.assets, true);
                if (list.assets.size() == 0) {
                    mBinding.noDataWatch.setVisibility(View.VISIBLE);
                } else {
                    mBinding.noDataWatch.setVisibility(View.GONE);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                mBinding.noDataWatch.setVisibility(View.VISIBLE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * get attached bank account of user from server and save in local DB
     * and show and hide fund your account.
     */
    private void getBankAccounts() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getBankAccounts(), new NetworkFragment.CallBack<List<BankAccount>>() {
            @Override
            public void onSuccess(List<BankAccount> accounts) {
                accountDao.deleteAll();
                if (accounts != null && accounts.size() != 0) {
                    if (!accounts.get(0).status.equalsIgnoreCase("CANCEL_REQUESTED")) {
                        mBinding.connectBank.setVisibility(View.GONE);
                        accountDao.saveAccount(accounts.get(0)).observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(">>>", "onComplete: Bank Account Saved");
                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {
                                        Log.d(">>>", "onError: " + e.getMessage());

                                    }
                                });
                        PrefUtils.setBoolean(requireActivity(), CommonKeys.KEY_ACCOUNT, true);
                    } else {
                        mBinding.connectBank.setVisibility(View.VISIBLE);
                    }
                } else {
                    mBinding.connectBank.setVisibility(View.VISIBLE);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Log.d(">>>", "onSuccess: " + accounts);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

//    private void getAccountStates() {
//        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
//        enqueue(getApi().getAccountStats(), new NetworkFragment.CallBack<AccountStats>() {
//            @Override
//            public void onSuccess(AccountStats stats) {
//                if (stats != null) {
//                    updateStats(stats);
//                }
//                mBinding.progressBar.progressBar.setVisibility(View.GONE);
//                Log.d(">>>", "onSuccess: " + stats);
//            }
//
//            @Override
//            public boolean onError(RetrofitError error, boolean isInternetIssue) {
//                mBinding.progressBar.progressBar.setVisibility(View.GONE);
//                return super.onError(error, isInternetIssue);
//            }
//        });
//    }
//
//    public void updateStats(AccountStats stats) {
//        mBinding.peopleInvited.setText(stats.peopleInvited);
//        mBinding.rewardTotal.setText(stats.rewardEarned);
//        mBinding.toolbar.rewardTicket.setText(stats.rewardEarned);
//    }

    public void shareLink(String link, String code) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Candy Stock");
            String shareMessage = "\nUse my referral code for application\n" + code + "\n";
            shareMessage = shareMessage + link;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    private void getShareableLink() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getShareableLink(), new NetworkFragment.CallBack<ShareableLink>() {
            @Override
            public void onSuccess(ShareableLink link) {
                url = link.url;
                code = link.code;
                mBinding.progressBar.progressBar.setVisibility(View.GONE);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * get trading profile from server and save it to local DB.
     */
    public void getTradingProfile() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getTradingProfile(), new NetworkFragment.CallBack<TradingProfile>() {
            @Override
            public void onSuccess(TradingProfile accounts) {
                updateBalance(accounts);
                profileDao.deleteAll();
                profileDao.saveTradingProfile(accounts).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                            }

                            @Override
                            public void onComplete() {
//                                Toast.makeText(requireActivity(), "saved", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.d(">>>", "onError: " + e.getMessage());

                            }
                        });
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Log.d(">>>", "onSuccess: " + accounts);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
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
                updateUI(user);
                if (user.account_status.equalsIgnoreCase("APPROVED")) {
                    getBankAccounts();
                    getTradingProfile();
                } else {
                    mBinding.connectBank.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
//                    mBinding.tickStar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDots)));
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * update balance from trading profile.
     *
     * @param profile Trading profile
     */
    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.portfolioValue.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(profile.portfolioValue), 2)));
        }
    }

    /**
     * update user's info from local DB.
     *
     * @param user User's object.
     */
    public void updateUI(User user) {
        updateImage(user);
        mBinding.name.setText(getString(R.string.hello, user.firstName));
    }

    /**
     * update image with name initials
     *
     * @param user
     */
    public void updateImage(User user) {
        NameInitialsCircleImageView.ImageInfo imageInfo = new NameInitialsCircleImageView.ImageInfo
                .Builder("" + user.firstName.charAt(0) + user.lastName.charAt(0))
                .setTextColor(R.color.colorWhite)
                .setTextFont(R.font.inter_semibold)
//                .setImageUrl(imageUrl)
                .setCircleBackgroundColorRes(R.color.colorBlue)
                .build();
        mBinding.profileImg.setImageInfo(imageInfo);
        if (!TextUtils.isEmpty(user.avatar)) {
            Glide.with(this).load(BuildConfig.BASE_URL + "file/users/" + user.avatar).error(imageInfo).into(mBinding.profileImg);
        }
    }


    /**
     * set properties for portfolio graph.
     *
     * @param positions  List of data
     * @param time_frame Time period for data.
     */
    public void renderGraphData(List<String> positions, String time_frame) {
        double total = 0;
        mBinding.portfolioChart.setTouchEnabled(true);
//        mBinding.chart.setClickable(false);
//        mBinding.chart.setDoubleTapToZoomEnabled(false);

        mBinding.portfolioChart.setDrawBorders(false);
        mBinding.portfolioChart.setDrawGridBackground(false);

        mBinding.portfolioChart.getDescription().setEnabled(false);
        mBinding.portfolioChart.getLegend().setEnabled(false);

        mBinding.portfolioChart.getAxisLeft().setDrawGridLines(false);
        mBinding.portfolioChart.getAxisLeft().setDrawLabels(false);
        mBinding.portfolioChart.getAxisLeft().setDrawAxisLine(false);

        mBinding.portfolioChart.getXAxis().setDrawGridLines(true);
        mBinding.portfolioChart.getXAxis().setDrawGridLinesBehindData(true);
        mBinding.portfolioChart.getXAxis().enableGridDashedLine(20, 10, 0);
        mBinding.portfolioChart.getXAxis().setGridColor(getResources().getColor(R.color.hintColor));
        mBinding.portfolioChart.getXAxis().setTextColor(getResources().getColor(R.color.hintColor));
        mBinding.portfolioChart.getXAxis().setDrawLabels(false);
        mBinding.portfolioChart.getXAxis().setDrawAxisLine(false);
        mBinding.portfolioChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mBinding.portfolioChart.getAxisRight().setDrawGridLines(false);
        mBinding.portfolioChart.getAxisRight().setDrawLabels(true);
        mBinding.portfolioChart.getAxisRight().setTextColor(getResources().getColor(R.color.hintColor));
        mBinding.portfolioChart.getAxisRight().setDrawAxisLine(false);

        for (String str : positions) {
            Log.d(">>>", "renderData: " + str);
            if (str != null) {
                total = total + Double.parseDouble(str);
            }
        }
//        mBinding.chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabel));

        if (total > 0) {
            setGraphData(positions);
        } else {
            mBinding.portfolioChart.clear();
            mBinding.portfolioChart.setNoDataText("No chart data available");
        }
    }

    /**
     * set graph data from server to portfolio and some properties
     *
     * @param positions List of data.
     */
    private void setGraphData(List<String> positions) {

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {

            if (positions.get(i) != null) {
                values.add(new Entry(i + 1, Float.parseFloat(positions.get(i))));
            }
//            Log.d(">>>", "listOfData: " + Math.round(Float.parseFloat(positions.get(i).marketValue)));
        }

        LineDataSet set1;
        if (mBinding.portfolioChart.getData() != null && mBinding.portfolioChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mBinding.portfolioChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mBinding.portfolioChart.getData().notifyDataChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(true);
//            IMarker marker = new ChartMarkerView(requireActivity(), R.layout.chart_marker_layout);
//            mBinding.chart.setMarker(marker);
            set1.setColor(getResources().getColor(R.color.colorPrimary));

            set1.setLineWidth(2f);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setDrawFilled(true);
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.graph_fill);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(getResources().getColor(R.color.colorPrimary));
            }

//            set1.setCubicIntensity(0);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mBinding.portfolioChart.setData(data);
        }
        mBinding.portfolioChart.notifyDataSetChanged();
        mBinding.portfolioChart.invalidate();
    }
}
