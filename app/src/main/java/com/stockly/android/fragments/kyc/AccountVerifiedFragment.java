package com.stockly.android.fragments.kyc;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.stockly.android.R;
import com.stockly.android.databinding.FragmentAccountVerifiedBinding;
import com.stockly.android.fragments.BaseFragment;
import com.stockly.android.fragments.plaid.BankIntroFragment;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * AccountVerifiedFragment
 * This Fragment class represents status of kyc after user successfully submitted his info.
 * So in case the status for kyc is approved this Fragment will be presented to user.
 */
@AndroidEntryPoint
public class AccountVerifiedFragment extends BaseFragment {
    FragmentAccountVerifiedBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite, null));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_verified, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentAccountVerifiedBinding.bind(view);


        mBinding.linked.setOnClickListener(view1 -> replaceFragment(new BankIntroFragment()));

    }
}
