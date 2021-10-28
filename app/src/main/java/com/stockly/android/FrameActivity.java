package com.stockly.android;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import dagger.hilt.android.AndroidEntryPoint;

import static com.stockly.android.constants.CommonKeys.KEY_DATA;
import static com.stockly.android.constants.CommonKeys.KEY_FRAGMENT;

/**
 * Activity for replacing fragments.
 */
@AndroidEntryPoint
public class FrameActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        String fragmentName = getIntent().getStringExtra(KEY_FRAGMENT);
        if (TextUtils.isEmpty(fragmentName)) {
            throw new IllegalArgumentException("Fragment Name must not be null");
        }
        Bundle bundle = getIntent().getBundleExtra(KEY_DATA);

        if (savedInstanceState == null) {
            Fragment fragment = Fragment.instantiate(this, fragmentName);
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            replaceFragment(fragment, false);
        }
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (addToStack) {
            fragmentTransaction.add(R.id.container, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        } else {
            while (supportFragmentManager.getBackStackEntryCount() > 0) {
                supportFragmentManager.popBackStackImmediate();
            }
            fragmentTransaction.replace(R.id.container, fragment);
        }
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.commit();
    }


}