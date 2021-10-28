package com.stockly.android.fragments.kyc;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.stockly.android.BuildConfig;
import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentReviewInfoBinding;
import com.stockly.android.databinding.FragmentTermsBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;

import java.util.HashMap;

import javax.inject.Inject;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 */

/**
 * TermsFragment
 * This fragment represents a webview with terms and condition are listed.
 */
@AndroidEntryPoint
public class TermsFragment extends NetworkFragment {
    private FragmentTermsBinding mBinding;
    @Inject
    UserDao userDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new ReviewInfoFragment());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_terms;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentTermsBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);


        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        mBinding.webView.getSettings().setJavaScriptEnabled(true); // enable javascript


        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                scrollEnable();

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), description, Toast.LENGTH_SHORT).show();
                scrollEnable();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                scrollEnable();
            }
        });

        // loaded url to webView
        mBinding.webView.loadUrl(BuildConfig.BASE_URL + "template/terms_conditions.html");


        mBinding.submit.setOnClickListener(view1 -> signAccount());

        // scroll to bottom and changes button
        mBinding.review.setOnClickListener(view12 -> {
            mBinding.scroll.fullScroll(ScrollView.FOCUS_DOWN);
            mBinding.scroll.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.review.setVisibility(View.GONE);
                    mBinding.submit.setVisibility(View.VISIBLE);
                }
            }, 1000);

        });

        // change button on scroll to bottom
        mBinding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (mBinding.scroll.getChildAt(0).getBottom() <= (mBinding.scroll.getHeight() + mBinding.scroll.getScrollY())) {
                //scroll view is at bottom
                mBinding.review.setVisibility(View.GONE);
                mBinding.submit.setVisibility(View.VISIBLE);
            }  //scroll view is not at bottom

        });

    }

    /**
     * enable scroll if it is scrollable
     */
    private void scrollEnable() {
        mBinding.scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                int viewHeight = mBinding.scroll.getMeasuredHeight();
                int contentHeight = mBinding.scroll.getChildAt(0).getHeight();
                if (viewHeight - contentHeight < 0) {
                    Log.d(">>>", "onGlobalLayout: scroll");
                    mBinding.review.setVisibility(View.VISIBLE);
                    mBinding.review.setEnabled(true);
                    mBinding.submit.setVisibility(View.GONE);
                    // scrollable
                } else {
                    mBinding.review.setVisibility(View.GONE);
                    mBinding.submit.setVisibility(View.VISIBLE);
                    Log.d(">>>", "onGlobalLayout: not scroll");
                }
            }
        }, 2000);
    }

    /**
     * Update tag to server for complete kyc.
     */
    private void updateProfileReview() {
        mBinding.submit.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("profile_completion", "complete");
        enqueue(getApi().updateProfile(body), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.submit.revertAnimation();
                if (user.account_status.equalsIgnoreCase("APPROVED")) {
                    replaceFragment(new AccountVerifiedFragment());
                } else if (user.account_status.equalsIgnoreCase("REJECTED")) {
                    replaceFragment(new KycRejectedFragment());
                } else {
                    replaceFragment(new UnderReviewFragment());
                }
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.submit.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * Broker api call to server when kyc submitted to update brokers account.
     */
    private void signAccount() {
        mBinding.submit.startAnimation();
        enqueue(getApi().signAccount(), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.submit.revertAnimation();
                updateProfileReview();

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.submit.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }
}
