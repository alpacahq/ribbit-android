package com.stockly.android;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stockly.android.databinding.ActivityKycBinding;
import com.stockly.android.databinding.ActivityMainBinding;
import com.stockly.android.fragments.kyc.MainFragment;
import com.stockly.android.fragments.plaid.BankIntroFragment;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Kyc activity that hold/replaces kyc fragments
 * for theme and styling process of kyc.
 */
@AndroidEntryPoint
public class KycActivity extends BaseActivity {
    ActivityKycBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_kyc);
        replaceFragment(new MainFragment(), false);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToStack) {
            fragmentTransaction.add(R.id.container, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        } else {
            fragmentTransaction.replace(R.id.container, fragment);
        }
        fragmentTransaction.commit();
    }
}