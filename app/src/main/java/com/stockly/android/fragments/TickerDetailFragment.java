package com.stockly.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.stockly.android.R;
import com.stockly.android.databinding.FragmentTickerDetailBinding;
import com.stockly.android.models.Bar;
import com.stockly.android.models.BarGraph;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.models.WatchList;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DateUtilz;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

import static com.stockly.android.utils.CommonUtils.amountConversion;
import static com.stockly.android.utils.CommonUtils.getDateInMilliSeconds;
import static com.stockly.android.utils.CommonUtils.round;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 */
@AndroidEntryPoint
public class TickerDetailFragment extends NetworkFragment implements View.OnClickListener {
    private FragmentTickerDetailBinding mBinding;
    private Positions asset;
    @Inject
    UserSession mUserSession;

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
        }

        if (asset == null)
            throw new IllegalArgumentException("Asset null");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ticker_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentTickerDetailBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);
        getUser();

        // setup data binding
        mBinding.setTicker(asset);
        //set title toolbar
        mBinding.toolbar.title.setText(asset.symbol);
        if (asset.name.contains("Stock") || asset.name.contains("Class")) {
            mBinding.toolbar.titleDesc.setText(String.format("%s.", asset.name.split("\\.", 2)[0]));
        } else {
            mBinding.toolbar.titleDesc.setText(asset.name);
        }

        // set ticker image
        updateImage(mBinding.winIcon, asset);

        if (asset.marketValue != null) {
            mBinding.winPrice.setText(String.format("$%s", round(Double.parseDouble(asset.marketValue), 2)));
            mBinding.winPercentage.setText(String.format("$%s", round(Double.parseDouble(String.valueOf(Float.parseFloat(asset.unrealizedPlpc) * 100)), 2)));
        } else if (asset.ticker != null) {
            mBinding.winPercentage.setVisibility(View.GONE);
            mBinding.winPrice.setText(String.format("$%s", round(Double.parseDouble(String.valueOf(asset.ticker.latestTrade.p)), 2)));
        }

        // set is favourite
        mBinding.toolbar.favourite.setChecked(asset.isFavourite);

        if (asset.unrealizedIntradayPlpc != null) {
            if (Double.parseDouble(asset.unrealizedPlpc) >= 0) {
                mBinding.winPercentage.setTextColor(getResources().getColor(R.color.greenColor, null));
                mBinding.winPercentage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_up, 0, 0, 0);
            } else {
                mBinding.winPercentage.setTextColor(getResources().getColor(R.color.colorError, null));
                mBinding.winPercentage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_down_24, 0, 0, 0);
            }
        }

        // listen to favourite check changes and perform action
        mBinding.toolbar.favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (asset != null) {
                    setFavourite(asset.symbol);
                }
            } else {
                if (asset != null) {
                    removeFavourite(asset.symbol);
                }
            }
        });


        mBinding.buy.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (asset != null) {
                bundle.putParcelable("asset", asset);
                bundle.putString("tag", getString(R.string.buy));
            }
            ActivityUtils.launchFragment(requireActivity(), BuySellFragment.class, bundle);
        });


        mBinding.sell.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (asset != null) {
                bundle.putParcelable("asset", asset);
                bundle.putString("tag", getString(R.string.sell));
            }
            ActivityUtils.launchFragment(requireActivity(), BuySellFragment.class, bundle);
        });

        mBinding.day1.setOnClickListener(this);
        mBinding.week1.setOnClickListener(this);
        mBinding.month3.setOnClickListener(this);
        mBinding.month6.setOnClickListener(this);
        mBinding.year1.setOnClickListener(this);
        mBinding.year5.setOnClickListener(this);

        getGraphData(asset.symbol, DateUtilz.getTimeFrame("1D"), "1D");

    }

    /**
     * Onclick for legends of graph to get data from server.
     *
     * @param v view provided for click listener.
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.day1) {
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("1D"), "1D");
        } else if (id == R.id.week1) {
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("1W"), "1W");
        } else if (id == R.id.month3) {
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("3M"), "3M");
        } else if (id == R.id.month6) {
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("6M"), "6M");
        } else if (id == R.id.year1) {
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("1Y"), "1Y");
        } else if (id == R.id.year5) {//                Log.d(">>>", "onClick: " + DateUtilz.getTimeFrame("5Y"));
            getGraphData(asset.symbol, DateUtilz.getTimeFrame("5Y"), "5Y");
        } else {
            throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    /**
     * get user from local DB.
     */
    public void getUser() {
        // local DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                mBinding.toolbar.favourite.setEnabled(user.account_status.equalsIgnoreCase("APPROVED"));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * Set graph properties and data
     *
     * @param bars       list of data to be shown on graph.
     * @param time_frame like 1D,1W etc.
     */
    public void renderGraphData(List<Bar> bars, String time_frame) {


        mBinding.chart.setTouchEnabled(false);
//        mBinding.chart.setClickable(false);
//        mBinding.chart.setDoubleTapToZoomEnabled(false);

        mBinding.chart.setDrawBorders(false);
        mBinding.chart.setDrawGridBackground(false);

        mBinding.chart.getDescription().setEnabled(false);
        mBinding.chart.getLegend().setEnabled(false);

        mBinding.chart.getAxisLeft().setDrawGridLines(false);
        mBinding.chart.getAxisLeft().setDrawLabels(false);
        mBinding.chart.getAxisLeft().setDrawAxisLine(false);


        mBinding.chart.getXAxis().setDrawGridLines(true);
        mBinding.chart.getXAxis().setDrawGridLinesBehindData(true);
        mBinding.chart.getXAxis().enableGridDashedLine(20, 10, 0);
        mBinding.chart.getXAxis().setGridColor(getResources().getColor(R.color.hintColor, null));
        mBinding.chart.getXAxis().setTextColor(getResources().getColor(R.color.hintColor, null));
        mBinding.chart.getXAxis().setDrawLabels(true);
        mBinding.chart.getXAxis().setDrawAxisLine(false);
        mBinding.chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mBinding.chart.getAxisRight().setDrawGridLines(false);
        mBinding.chart.getAxisRight().setDrawLabels(true);
        mBinding.chart.getAxisRight().setTextColor(getResources().getColor(R.color.hintColor, null));
        mBinding.chart.getAxisRight().setDrawAxisLine(false);
        final ArrayList<String> xLabel = new ArrayList<>();

        for (Bar bar : bars) {
            xLabel.add(bar.t);
        }

        if (time_frame.equalsIgnoreCase("1D")) {
            if (bars.size() > 4) {
                mBinding.chart.getXAxis().setLabelCount(5, true);
                mBinding.chart.getXAxis().setCenterAxisLabels(true);
            }
        } else if (time_frame.equalsIgnoreCase("5Y")) {
            mBinding.chart.getXAxis().setLabelCount(6, true);
            mBinding.chart.getXAxis().setCenterAxisLabels(true);
        }
        mBinding.chart.getXAxis().setValueFormatter(new ClaimsXAxisValueFormatter(xLabel, time_frame));

        setGraphData(bars);
    }


    /**
     * Set Graph data and update graph each time when legend selected.
     * Set some properties of graph.
     *
     * @param bars List of data to be shown.
     */
    private void setGraphData(List<Bar> bars) {

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < bars.size(); i++) {
            values.add(new Entry(i + 1, bars.get(i).o));
//            Log.d(">>>", "listOfData: " + Math.round(bars.get(i).o));
        }

        LineDataSet set1;
        if (mBinding.chart.getData() != null && mBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mBinding.chart.getData().notifyDataChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(true);
//            IMarker marker = new ChartMarkerView(requireActivity(), R.layout.chart_marker_layout);
//            mBinding.chart.setMarker(marker);
            set1.setColor(getResources().getColor(R.color.colorPrimary, null));

            set1.setLineWidth(2f);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setDrawFilled(true);
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.graph_fill);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(getResources().getColor(R.color.colorPrimary, null));
            }

//            set1.setCubicIntensity();
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mBinding.chart.setData(data);
        }
        mBinding.chart.notifyDataSetChanged();
        mBinding.chart.invalidate();
    }

    /**
     * Get Graph Data from server to display.
     *
     * @param symbol    Ticker or asset of which data will be displayed.
     * @param body      payload params required for data access.
     * @param timeFrame Required data for time period like 1D,1W etc.
     */
    private void getGraphData(String symbol, HashMap body, String timeFrame) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getGraphData(symbol, body), new CallBack<BarGraph>() {
            @Override
            public void onSuccess(BarGraph t) {
                if (t.bars != null) {
                    if (t.bars.size() != 0) {
                        renderGraphData(t.bars, timeFrame);
                    } else {
                        getGraphData(asset.symbol, DateUtilz.getTimeFrame(""), "1D");
                    }
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
     * save Ticker to favourite items.
     *
     * @param s is ticker symbol.
     */
    private void setFavourite(String s) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> body = new HashMap<>();
        body.put("symbol", CommonUtils.getValue(s));
        enqueue(getApi().setWatchList(body), new CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList t) {
                mBinding.toolbar.favourite.setChecked(true);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.toolbar.favourite.setChecked(false);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });

    }

    /**
     * Remove Ticker to favourite items.
     *
     * @param symbol is ticker symbol.
     */
    private void removeFavourite(String symbol) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().removeWatchlist(symbol), new CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList account) {
                mBinding.toolbar.favourite.setChecked(false);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.toolbar.favourite.setChecked(true);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
    }


    /**
     * This class returns x-axis labels for graph based on legend provided
     * by formatting date for each legend
     */
    public static class ClaimsXAxisValueFormatter extends ValueFormatter {

        List<String> datesList;
        String timeFrame;

        public ClaimsXAxisValueFormatter(List<String> arrayOfDates, String time_frame) {
            this.datesList = arrayOfDates;
            this.timeFrame = time_frame;
        }


        @SuppressLint("SimpleDateFormat")
        @Override
        public String getAxisLabel(float value, AxisBase axis) {

            int position = Math.round(value);
            SimpleDateFormat sdf;
            if (timeFrame.equalsIgnoreCase("1D")) {
                sdf = new SimpleDateFormat("hh:mm a");
            } else if (timeFrame.equalsIgnoreCase("1Y")) {
                sdf = new SimpleDateFormat("MMM yyyy");
            } else if (timeFrame.equalsIgnoreCase("5Y")) {
                sdf = new SimpleDateFormat("yyyy");
            } else {
                sdf = new SimpleDateFormat("M/d");
            }
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            if (position < datesList.size()) {
//                Log.d(">>>", "getAxisLabel: " + sdf.format(new Date((getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd'T'HH:mm:ss'Z'")))));
                return sdf.format(new Date((getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd'T'HH:mm:ss'Z'"))));
            }
            return "";
        }

    }

    @Override
    public void onResume() {
        getTradingProfile();
        super.onResume();
    }

    /**
     * Get Trading profile from local DB.
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
     * Set balance getting from
     *
     * @param profile from Trading Profile local DB.
     */
    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.balance.setText(String.format("$%s", round(Double.parseDouble(profile.portfolioValue), 2)));
        }
    }
}

