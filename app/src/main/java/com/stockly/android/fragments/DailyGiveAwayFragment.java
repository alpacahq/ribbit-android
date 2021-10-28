package com.stockly.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stockly.android.R;
import com.stockly.android.adapter.DailyGiveAwayAdapter;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentDailyGiveAwayBinding;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.ShareableLink;
import com.stockly.android.utils.CommonUtils;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * DailyGiveAwayFragment
 * It represents a contest to enter for user win daily rewards.
 */
@AndroidEntryPoint
public class DailyGiveAwayFragment extends NetworkFragment {
    private FragmentDailyGiveAwayBinding mBinding;
    private DailyGiveAwayAdapter mAdapter;

    @Inject
    UserDao userDao;
    private String code;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        requireActivity().setTheme(R.style.Theme_Base_Main_Home_Blue);
        super.onCreate(savedInstanceState);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#7674FF"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily_give_away;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentDailyGiveAwayBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);

        getShareableLink();

        mBinding.items.setNestedScrollingEnabled(false);
        mBinding.items.setHasFixedSize(true);
        mBinding.items.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new DailyGiveAwayAdapter(requireActivity());
        mBinding.items.setAdapter(mAdapter);

        mBinding.share.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mBinding.inviteLink.getText().toString()))
                shareableLink(CommonUtils.getValue(mBinding.inviteLink.getText().toString()), CommonUtils.getValue(code));
        });
    }

    public void shareableLink(String link, String code) {
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
                mBinding.inviteLink.setText(link.url);
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


}
