package com.stockly.android.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stockly.android.R;
import com.stockly.android.adapter.NotificationsAdapter;
import com.stockly.android.databinding.FragmentHomeBinding;
import com.stockly.android.databinding.FragmentNotificationBinding;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.PrefUtils;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * NotificationFragment
 * Notification of application will be presented here.
 * List of notification will be called from server and
 * number of unread notification will be shown.
 */
@AndroidEntryPoint
public class NotificationFragment extends NetworkFragment {

    private FragmentNotificationBinding mBinding;
    private BankAccount account;
    private NotificationsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notification;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentNotificationBinding.bind(view);

        setUpToolBar(mBinding.toolbar.toolbar);

        mBinding.notificationItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new NotificationsAdapter(requireActivity());
        mBinding.notificationItems.setAdapter(mAdapter);

    }


    private void getNotifications() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getBankAccounts(), new CallBack<List<BankAccount>>() {
            @Override
            public void onSuccess(List<BankAccount> accounts) {
                if (accounts != null && accounts.size() != 0) {
                    account = accounts.get(0);
                    PrefUtils.setBoolean(requireActivity(), "account", true);
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

}
