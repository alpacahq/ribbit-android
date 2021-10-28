package com.stockly.android.fragments.plaid;

import android.content.Intent;
import android.graphics.Color;
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

import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.databinding.FragmentAccountLinkBinding;
import com.stockly.android.databinding.FragmentUnderReviewBinding;
import com.stockly.android.fragments.BaseFragment;
import com.stockly.android.fragments.wallet.AddFundsFragment;
import com.stockly.android.utils.ActivityUtils;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * AccountLinkFragment
 * * This Fragment represents result of attached bank account
 * * and further movement to app.
 */
@AndroidEntryPoint
public class AccountLinkFragment extends BaseFragment {
    FragmentAccountLinkBinding mBinding;
    private String path = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return inflater.inflate(R.layout.fragment_account_link, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentAccountLinkBinding.bind(view);
        //status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite, null));

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString("path");
        }


        mBinding.linked.setOnClickListener(view1 -> {
            if (path.equalsIgnoreCase("")) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            } else if (path.equalsIgnoreCase("funds")) {
                AddFundsFragment fragment = new AddFundsFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString(CommonKeys.KEY_FUNDS, "funds");
                fragment.setArguments(bundle1);
                replaceFragment(fragment);
            }
        });

    }
}
