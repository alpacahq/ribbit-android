package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentReferalCodeBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.ReferralCode;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.validation.Validator;
import com.stockly.android.widgets.CustomEditText;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * ReferralCodeFragment
 * This fragment class represent Required referral code of user that
 * sent an invitation to join app.
 */
@AndroidEntryPoint
public class ReferralCodeFragment extends NetworkFragment {
    private FragmentReferalCodeBinding mBinding;

    @Inject
    UserSession mUserSession;
    @Inject
    UserDao userDao;
    private String verify, otp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            otp = bundle.getString("otp");
            verify = bundle.getString("verify");

        }
        // handles back press
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if (!TextUtils.isEmpty(otp)) {
                    requireActivity().finish();
                } else {
                    replaceFragment(new MainFragment());
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_referal_code;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentReferalCodeBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, false);

        // it will user's first time appearance
        if (!TextUtils.isEmpty(verify)) {
            if (verify.equalsIgnoreCase("new")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.verifiedFrame.animate().alpha(0.0f).setDuration(1000);
                    }
                }, 2000);
            }

        } else {
            mBinding.verifiedFrame.setVisibility(View.GONE);
        }

        getUser();

        mBinding.referral.setObserver((isValid, str) -> mBinding.next.setEnabled(isValid));
        mBinding.next.setOnClickListener(view1 -> {
            Validator validator = new Validator();
            if (validator.isValidInput(requireActivity(), mBinding.referral)) {
                verifyReferralCode(mBinding.referral.getEditText().getText().toString());
            }
        });

        mBinding.skip.setOnClickListener(v -> updateReferralCode("", mBinding.skip));

    }

    /**
     * Update server with referral tag and skip functionality
     *
     * @param referral
     * @param view
     */
    private void updateReferralCode(String referral, CircularProgressButton view) {
        view.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("referred_by", CommonUtils.getValue(referral));
        body.put("profile_completion", "referral");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                view.revertAnimation();
                replaceFragment(new NameFragment());

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                view.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * Verification of referral code provided by user
     *
     * @param referral
     */
    private void verifyReferralCode(String referral) {
        enqueue(getApi().verifyReferralCode(referral), new NetworkFragment.CallBack<ReferralCode>() {
            @Override
            public void onSuccess(ReferralCode code) {
                if (code.referral_code != null) {
                    mBinding.referral.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_outline, 0);
                    updateReferralCode(mBinding.referral.getEditText().getText().toString(), mBinding.next);
                } else {
                    Toast.makeText(requireActivity(), "Your code didn't match! Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                if (!isInternetIssue) {
                    Toast.makeText(requireActivity(), "Referral code didn't match!", Toast.LENGTH_LONG).show();
                }
                return true;

            }
        });
    }

    /**
     * get user from local DB.
     */
    public void getUser() {
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {

            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

}
