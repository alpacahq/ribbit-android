package com.stockly.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.CryptoUtil;
import com.stockly.android.utils.PrefUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * Shows app logo in activity at the time of launching application.
 */

@AndroidEntryPoint
public class SplashActivity extends BaseActivity {

    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = this::moveToNext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flushDB();
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    private void moveToNext() {
        startActivity(new Intent(this, KycActivity.class));
//        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }


}
