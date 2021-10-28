package com.stockly.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.stockly.android.FrameActivity;
import com.stockly.android.KycActivity;
import com.stockly.android.SplashActivity;
import com.stockly.android.constants.CommonKeys;

/**
 * Activity Util class with different static function that
 * can be used in any other classes like to launch activity/fragment etc.
 */

public class ActivityUtils implements CommonKeys {

    public static void launchActivity(FragmentActivity context, Class<?> clazz) {
        context.startActivity(new Intent(context, clazz));
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void launchFragment(FragmentActivity context, Class<?> clazz) {
        launchFragment(context, clazz, null);
    }

    public static void launchFragment(FragmentActivity context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, FrameActivity.class);
        intent.putExtra(KEY_FRAGMENT, clazz.getName());
        if (bundle != null) {
            intent.putExtra(KEY_DATA, bundle);
        }
        context.startActivity(intent);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void launchFragmentForResult(Fragment context, Class<?> clazz, Bundle bundle, int code) {
        Intent intent = new Intent(context.getContext(), FrameActivity.class);
        intent.putExtra(KEY_FRAGMENT, clazz.getName());
        if (bundle != null) {
            intent.putExtra(KEY_DATA, bundle);
        }
        context.startActivityForResult(intent, code);
        context.requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void launchFragmentForResult(Fragment context, Class<?> clazz, int code) {
        launchFragmentForResult(context, clazz, null, code);
    }


}
