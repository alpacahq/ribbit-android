package com.stockly.android.fragments.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.databinding.FragmentAddFundsBinding;
import com.stockly.android.databinding.FragmentFundsAddedBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.Payment;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DateUtilz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FundsAddedFragment
 * It represents to user when funds added and status of funds,
 * Either accepted or rejected
 */
@AndroidEntryPoint
public class FundsAddedFragment extends NetworkFragment {
    private FragmentFundsAddedBinding mBinding;
    private Payment payment;
    private String key;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            payment = bundle.getParcelable("payment");
            key = bundle.getString(CommonKeys.KEY_FUNDS);
        }
        // handles navigate back depending on where it comes from.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AddFundsFragment fragment = new AddFundsFragment();
                if (key != null) {
                    if (key.equalsIgnoreCase("funds")) {
                        bundle.putString(CommonKeys.KEY_FUNDS, "funds");
                        fragment.setArguments(bundle);
                        replaceFragment(fragment);
                    } else if (key.equalsIgnoreCase("transaction")) {
                        bundle.putString(CommonKeys.KEY_FUNDS, "transaction");
                        fragment.setArguments(bundle);
                        replaceFragment(fragment);
                    } else {
                        requireActivity().finish();
                    }
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_funds_added;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentFundsAddedBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        mBinding.addedFund.setText(String.format("$%s Added", CommonUtils.round(Double.parseDouble(payment.amount), 2)));
        String currentTime = DateUtilz.formatTimeAmPm(payment.createdAt);

        mBinding.availableTime.setText(currentTime);

        /**
         * checks payment value
         * checks status of payments
         * set data according to statuses
         * whether accepted or canceled etc.
         */
        if (payment != null) {
//            QUEUED
            if (payment.status.equalsIgnoreCase(getString(R.string.queued))) {
                Log.d(">>>", "onViewCreated: " + payment.status);
            } else if (payment.status.equalsIgnoreCase(getString(R.string.complete))) {
//                mBinding.view1.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.lightGrey));
//                mBinding.view2.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                mBinding.initiate.setTextColor(Color.parseColor("#C8C8C8"));
                mBinding.deposit.setTextColor(getResources().getColor(R.color.textColorSubHeading, null));
                mBinding.view3.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                mBinding.viewCancel.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary));
                mBinding.view4.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary));
            } else if (payment.status.equalsIgnoreCase(getString(R.string.canceled))) {
//                mBinding.view1.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.lightGrey));
//                mBinding.view2.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                mBinding.initiate.setTextColor(Color.parseColor("#C8C8C8"));
                mBinding.depositCancel.setTextColor(getResources().getColor(R.color.textColorSubHeading, null));
                mBinding.viewCancel.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.colorError));
                mBinding.view3.setBackgroundColor(getResources().getColor(R.color.colorError, null));
                mBinding.view4.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.colorError));
            }

        }
        mBinding.done.setOnClickListener(v -> {
            requireActivity().finish();
        });
    }

}
