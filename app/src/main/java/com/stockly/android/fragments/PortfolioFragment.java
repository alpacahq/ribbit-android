package com.stockly.android.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.adapter.MyStocksAdapter;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.databinding.FragmentPortfolioBinding;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.fragments.wallet.AddFundsFragment;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.PortfolioHistory;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * PortfolioFragment
 * It represents portfolio graph History and
 * List of Purchased and sold stocks of Ticker with its
 * up and down price percentage.
 */
@AndroidEntryPoint
public class PortfolioFragment extends NetworkFragment implements View.OnClickListener {
    private FragmentPortfolioBinding mBinding;
    private MyStocksAdapter mAdapter;
    @Inject
    TradingProfileDao profileDao;
    @Inject
    BankAccountDao accountDao;
    private BankAccount accounts;
    @Inject
    UserSession mUserSession;
    private User mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPressActivity(this, MainActivity.class);
        // set status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_portfolio;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentPortfolioBinding.bind(view);
        mBinding.toolbar.save.setVisibility(View.GONE);
        mBinding.toolbar.title.setText(R.string.portfolio);
        setUpToolBar(mBinding.toolbar.toolbar, true);


        mBinding.addFund.setOnClickListener(v -> {
            if (mUser.account_status.equalsIgnoreCase("APPROVED")) {
                checkAccounts();
            }
        });

        mBinding.stockItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new MyStocksAdapter(requireActivity(), (s, position) -> {
//                addFragment(new BuySellFragment());
            Bundle bundle = new Bundle();
            if (s != null) {
                bundle.putParcelable("asset", s);
            }
            ActivityUtils.launchFragment(requireActivity(), TickerDetailFragment.class, bundle);
        });
        mBinding.stockItems.setAdapter(mAdapter);

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
     * Click listener for legends like 1D, 1W.
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
     * Get list of Purchased/Sold Stocks of tickers from server.
     */
    private void getPositionsList() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getPositionsList(), new NetworkFragment.CallBack<List<Positions>>() {
            @Override
            public void onSuccess(List<Positions> list) {

                if (list.size() != 0) {
                    mAdapter.setData(list, true);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mBinding.noData.setVisibility(View.VISIBLE);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Log.d(">>>", "onSuccess: " + list);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                mBinding.noData.setVisibility(View.VISIBLE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * Get Portfolio graph data from server.
     *
     * @param body      params
     * @param timeFrame time period.
     */
    private void getPortfolioGraph(HashMap body, String timeFrame) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getPortfolioHistory(body), new CallBack<PortfolioHistory>() {
            @Override
            public void onSuccess(PortfolioHistory t) {
                if (t.equity != null) {
                    renderGraphData(t.equity, timeFrame);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Log.d(">>>", "onSuccess: " + t);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });

    }

    /**
     * get trading profile from local DB.
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
     * update balance from trading profile of user's
     *
     * @param profile Trading profile
     */
    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.balance.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(profile.cash), 2)));
            mBinding.portfolioValue.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(profile.portfolioValue), 2)));
        }
    }

    /**
     * check attached bank account of user's.
     */
    private void checkAccounts() {
        Bundle bundle = new Bundle();
        if (accounts != null) {
            bundle.putString(CommonKeys.KEY_FUNDS, "transaction");
            ActivityUtils.launchFragment(requireActivity(), AddFundsFragment.class, bundle);
        } else {
            bundle.putString("path", "funds");
            ActivityUtils.launchFragment(requireActivity(), BankIntroFragment.class, bundle);
        }
    }

    /**
     * get bank account of user's from local DB.
     */
    public void getBankAccounts() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        requestSingle(accountDao.getBankAccount(), new CallBackSingle<BankAccount>() {
            @Override
            public void onSuccess(@NotNull BankAccount account) {
                if (account != null) {
                    accounts = account;
                } else {
                    accounts = null;
                }
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
     * get user from local DB.
     */
    public void getUser() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                mUser = user;
                if (user.account_status.equalsIgnoreCase("APPROVED")) {
                    getPositionsList();
                } else {
                    mBinding.addFund.setEnabled(false);
                    mBinding.noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
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
     * set up graph and its properties.
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
            if (str != null) {
                total = total + Double.parseDouble(str);
            }
        }

        if (total > 0) {
            setGraphData(positions);
        } else {
            mBinding.portfolioChart.clear();
            mBinding.portfolioChart.setNoDataText("No chart data available");
        }

    }

    /**
     * set data on portfolio graph
     *
     * @param positions data list.
     */
    private void setGraphData(List<String> positions) {

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) != null) {
                values.add(new Entry(i + 1, Float.parseFloat(positions.get(i))));
            }
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
