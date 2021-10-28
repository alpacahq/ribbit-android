package com.stockly.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.adapter.SearchTickersAdapter;
import com.stockly.android.databinding.FragmentSearchTickerBinding;
import com.stockly.android.listners.ChangeListener;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.models.WatchList;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * SearchTickerFragment
 * It represents a search functionality of ticker.
 * Multiple ticker can be searched from server and set them favourites
 * also can choose any ticker and buy/sell their stock.
 */
@AndroidEntryPoint
public class SearchTickerFragment extends NetworkFragment {
    private FragmentSearchTickerBinding mBinding;
    private SearchTickersAdapter mAdapter;
    @Inject
    UserSession mUserSession;
    private User mUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPressActivity(this, MainActivity.class);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_ticker;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentSearchTickerBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);

        // search query listener for tickers
        mBinding.toolbar.search.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    searchAssets(charSequence.toString());
                    mBinding.toolbar.search.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0);
                } else {
                    searchAssets("");
                    mBinding.toolbar.search.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // close search and set empty
        mBinding.toolbar.search.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mBinding.toolbar.search.getCompoundDrawables()[2] != null) {
                    if (event.getX() >= (mBinding.toolbar.search.getRight() - mBinding.toolbar.search.getLeft() - mBinding.toolbar.search.getCompoundDrawables()[2].getBounds().width())) {
                        if (mBinding.toolbar.search.getText().length() > 0) {
                            mBinding.toolbar.search.setText("");
                        }
                    }
                }
            }
            return false;
        });

        // setup adapter
        mAdapter = new SearchTickersAdapter(requireActivity(), (s, position) -> {
            Bundle bundle = new Bundle();
            if (s != null) {
                bundle.putParcelable("asset", s);
            }
            ActivityUtils.launchFragment(requireActivity(), TickerDetailFragment.class, bundle);

        }, new ChangeListener<Positions>() {
            @Override
            public void checkedChangeListener(Positions positions, int pos, boolean isChecked) {
//                Toast.makeText(requireActivity(), "" + isChecked, Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    positions.isFavourite = true;
                    setFavourite(positions.symbol);

                } else {
                    positions.isFavourite = false;
                    removeFavourite(positions.symbol);

                }
            }
        });
        mBinding.items.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        getUser();
        super.onResume();
    }

    /**
     * Search ticker from server with
     *
     * @param query params
     */
    private void searchAssets(String query) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        Observable<List<Positions>> api;
        if (TextUtils.isEmpty(query)) {
            api = getApi().getAssetsList();
        } else {
            api = getApi().getAssetsList(query);
        }
        enqueue(api, new NetworkFragment.CallBack<List<Positions>>() {
            @Override
            public void onSuccess(List<Positions> list) {
                if (list != null && list.size() != 0) {
                    mBinding.items.setVisibility(View.VISIBLE);
                    mBinding.noSearchRecord.setVisibility(View.GONE);
                    mAdapter.setData(list, true, mUser);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mBinding.items.setVisibility(View.GONE);
                    mBinding.noSearchRecord.setVisibility(View.VISIBLE);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.items.setVisibility(View.GONE);
                mBinding.noSearchRecord.setVisibility(View.VISIBLE);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * set tickers to favourites by passing
     *
     * @param s params symbol
     */
    private void setFavourite(String s) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> body = new HashMap<>();
        body.put("symbol", CommonUtils.getValue(s));
        enqueue(getApi().setWatchList(body), new NetworkFragment.CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList t) {

                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Log.d(">>>", "onSuccess: " + t.name);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });

    }

    /**
     * remove tickers from favourites by passing
     *
     * @param symbol params
     */
    private void removeFavourite(String symbol) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().removeWatchlist(symbol), new NetworkFragment.CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList account) {
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
     * get user from local DB.
     */
    public void getUser() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                mUser = user;
                searchAssets("");
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }
}
