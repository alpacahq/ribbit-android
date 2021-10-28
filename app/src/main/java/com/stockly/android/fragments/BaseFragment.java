package com.stockly.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.stockly.android.AppController;
import com.stockly.android.BaseActivity;
import com.stockly.android.KycActivity;
import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.LayoutWhiteToolbarBinding;
import com.stockly.android.databinding.MessageDialogBinding;
import com.stockly.android.fragments.kyc.DateOfBirthFragment;

import com.stockly.android.models.Positions;
import com.stockly.android.models.User;
import com.stockly.android.models.UserAuth;
import com.stockly.android.utils.PrefUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.security.auth.callback.Callback;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;
import dagger.hilt.EntryPoint;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * BaseFragment
 * This is parent fragment to all the fragments.
 * Function are defined in are being used in child fragments.
 */
@AndroidEntryPoint
public abstract class BaseFragment extends Fragment {

    @Inject
    UserDao userDao;
    @Inject
    BankAccountDao accountDao;
    @Inject
    TradingProfileDao profileDao;

    /**
     * To set toolbar for fragments
     *
     * @param toolBar toolbar instance.
     */
    protected void setUpToolBar(Toolbar toolBar) {
        BaseActivity activity = (BaseActivity) requireActivity();
        activity.setUpToolBar(toolBar, true);
    }

    protected void setUpToolBar(Toolbar toolBar, boolean isHomeUp) {
        BaseActivity activity = (BaseActivity) requireActivity();
        activity.setUpToolBar(toolBar, isHomeUp);
    }

    /**
     * To replace fragments using baseActivity
     *
     * @param fragment instance.
     */
    public void replaceFragment(Fragment fragment) {
        BaseActivity activity = (BaseActivity) requireActivity();
        activity.replaceFragment(fragment, false);
    }

    /**
     * To add fragments to baseActivity
     *
     * @param fragment instance.
     */
    protected void addFragment(Fragment fragment) {
        BaseActivity activity = (BaseActivity) requireActivity();
        activity.replaceFragment(fragment, true);
    }

    /**
     * Handle back navigation of fragments by replacing fragments
     *
     * @param owner    lifecycle
     * @param fragment class
     */
    protected void handleBackPress(LifecycleOwner owner, Fragment fragment) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
//                Toast.makeText(requireActivity(), "coming", Toast.LENGTH_SHORT).show();
                replaceFragment(fragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(owner, callback);
    }

    /**
     * Handles back navigation of Activities
     *
     * @param owner    lifecycle
     * @param activity class
     */
    protected void handleBackPressActivity(LifecycleOwner owner, Class<?> activity) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Intent intent = new Intent(requireActivity(), activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(owner, callback);
    }

    /**
     * This function is used to save user to local database.
     *
     * @param user instance.
     */
    protected void addUser(User user) {
        NetworkFragment.requestCompletable(userDao.saveUser(user), new NetworkFragment.CallBackCompletable() {
            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: error");
            }

            @Override
            public void onComplete() {
                Log.d(">>>", "onComplete: Added");
            }

        });

    }

    /**
     * This function is used to update user to local database each time gets called.
     *
     * @param user instance.
     */
    protected void updateUser(User user) {
        NetworkFragment.requestCompletable(userDao.updateUsers(user), new NetworkFragment.CallBackCompletable() {
            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: error");
            }

            @Override
            public void onComplete() {
                Log.d(">>>", "onComplete: updated");
            }

        });
    }

    /**
     * It will take name Initial and create image.
     *
     * @param imageView nameInitials.
     * @param positions class.
     */
    public void updateImage(NameInitialsCircleImageView imageView, Positions positions) {
//        String[] str = positions.name.split("\\s+");
//        String str1 = str[0];
//        String str2 = str[1];
//        String finalStr;
//        if (!TextUtils.isEmpty(str2)) {
//            finalStr = "" + str1.charAt(0) + str2.charAt(0);
//        } else {
//            finalStr = "" + str1.charAt(0);
//        }
        NameInitialsCircleImageView.ImageInfo imageInfo = new NameInitialsCircleImageView.ImageInfo
                .Builder("" + positions.symbol.charAt(0))
                .setTextColor(R.color.colorWhite)
                .setTextFont(R.font.inter_semibold)
//                .setImageUrl(imageUrl)
//                .setCircleBackgroundColorRes(randomAndroidColor)
                .build();
        imageView.setImageInfo(imageInfo);

    }


    /**
     * Show error for authorization flow at bottom of screen.
     *
     * @param view    messageDialog.
     * @param message string.
     */
    public void showErrorMessage(MessageDialogBinding view, String message) {
        view.textError.setText(message);
        view.loginError.setVisibility(View.VISIBLE);
        view.loginError.setAlpha(1.0f);
        view.loginError.animate().alpha(0.5f).setDuration(5000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.loginError.setVisibility(View.GONE);
            }
        }, 4000);
    }

    /**
     * Restart App and remove everything from local DB etc.
     *
     * @param context class.
     */
    public void restartApp(Activity context) {
        flushDB();
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        context.startActivity(new Intent(context, KycActivity.class));
        context.finishAffinity();
    }

    /**
     * Delete DataBase tables and Preferences of App.
     */
    public void flushDB() {
        PrefUtils.clear(requireActivity());
        userDao.deleteAll();
        accountDao.deleteAll();
        profileDao.deleteAll();
    }


}
