package com.stockly.android;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stockly.android.databinding.ActivityMainBinding;

import com.stockly.android.fragments.BuySellFragment;
import com.stockly.android.fragments.HomeFragment;
import com.stockly.android.fragments.PortfolioFragment;
import com.stockly.android.fragments.ProfileFragment;
import com.stockly.android.fragments.RewardsFragment;
import com.stockly.android.fragments.SearchTickerFragment;
import com.stockly.android.fragments.wallet.TransactionsFragment;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity Holds Fragment for Home screen.
 * Home, Profile, Portfolio, Transaction fragments etc
 * using bottom navigation view.
 */
@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), "7f55e907-df4e-47c7-b0c1-e49b63dde630",
                Analytics.class, Crashes.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        replaceFragment(new HomeFragment(), false);

        mBinding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SearchTickerFragment(), false);
                mBinding.bottomNavigation.setSelectedItemId(R.id.disable);
            }
        });

        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new HomeFragment(), false);
                        return true;
                    case R.id.portfolio:
                        replaceFragment(new PortfolioFragment(), false);
                        return true;
                    case R.id.transactions:
                        replaceFragment(new TransactionsFragment(), false);
                        return true;
                    case R.id.profile:
                        replaceFragment(new ProfileFragment(), false);
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToStack) {
            fragmentTransaction.add(R.id.activity_main_container, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        } else {
            fragmentTransaction.replace(R.id.activity_main_container, fragment);
        }
        fragmentTransaction.commit();
    }


}