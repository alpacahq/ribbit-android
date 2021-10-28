package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.databinding.FragmentKycRejectedBinding;
import com.stockly.android.fragments.BaseFragment;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * KycRejectedFragment
 * This Fragment class represents status of kyc after user successfully submitted his info.
 * So in case the status for kyc is rejected this Fragment will be presented to user that
 * his provided info is incorrect.
 */
@AndroidEntryPoint
public class KycRejectedFragment extends BaseFragment {
    private FragmentKycRejectedBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kyc_rejected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentKycRejectedBinding.bind(view);

        mBinding.underReview.setOnClickListener(view1 -> {
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            replaceFragment(new LoginFragment());
        });

    }
}
