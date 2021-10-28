package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;

import com.stockly.android.databinding.FragmentForgotPassBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.UserAuth;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.validation.Validator;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * ForgotPasswordFragment
 * This Fragment class represents email verification for reset user's password.
 */
@AndroidEntryPoint
public class ForgotPasswordFragment extends NetworkFragment {
    private FragmentForgotPassBinding mBinding;
    @Inject
    UserSession mUserSession;
    @Inject
    UserDao userDao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        handleBackPress(this, new MainFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_pass;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentForgotPassBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        mBinding.email.setObserver((isValid, str) -> {
            mBinding.next.setEnabled(isValid);

        });


        mBinding.next.setOnClickListener(view1 -> {
            Validator validator = new Validator();
            if (validator.isValidEmail(requireActivity(), mBinding.email)) {

                forgotPassword(mBinding.email.getEditText().getText().toString());

            }
        });

    }

    /**
     * Send request to server for password recovery by passing
     *
     * @param email which will send email with otp in return.
     */
    private void forgotPassword(String email) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        enqueue(getApi().forgotPass(body), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                mBinding.next.revertAnimation();
                Bundle bundle = new Bundle();
                bundle.putString("email", mBinding.email.getEditText().getText().toString());
                bundle.putString("otp", "forgot");
                ActivityUtils.launchFragment(requireActivity(), VerifyOTPFragment.class, bundle);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                if (!isInternetIssue) {
                    showErrorMessage(mBinding.message, error.message);
                    return true;
                }
                return super.onError(error, isInternetIssue);

            }
        });
    }

}
