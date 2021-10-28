package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.databinding.FragmentSignUpBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.UserAuth;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CryptoUtil;
import com.stockly.android.validation.Validator;
import com.stockly.android.widgets.CustomEditText;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 */

/**
 * SignUpFragment
 * This Fragment represent user option to create account to get access to application
 */
@AndroidEntryPoint
public class SignUpFragment extends NetworkFragment {
    private FragmentSignUpBinding mBinding;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentSignUpBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        Validator validator = new Validator();

        mBinding.email.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidPassword(requireActivity(), mBinding.password) && validator.isValidPassword(requireActivity(), mBinding.confirmPassword) && isValid));


        mBinding.password.setObserver((isValid, str) -> {
            String pass = mBinding.password.getEditText().getText().toString();
            String confirmPassword = mBinding.confirmPassword.getEditText().getText().toString();
            if (validator.isValidEmail(requireActivity(), mBinding.email) && validator.isValidPassword(requireActivity(), mBinding.confirmPassword) && isValid) {
                if (pass.equals(confirmPassword)) {
                    mBinding.next.setEnabled(true);
                } else {
                    mBinding.confirmPassword.setError("Password didn't match");
                    mBinding.confirmPassword.showError();
                    mBinding.next.setEnabled(false);
                }
            } else {
                mBinding.next.setEnabled(false);
            }
        });

        mBinding.confirmPassword.setObserver((isValid, str) -> {
            String pass = mBinding.password.getEditText().getText().toString();
            String confirmPassword = mBinding.confirmPassword.getEditText().getText().toString();
            if (validator.isValidEmail(requireActivity(), mBinding.email) && validator.isValidPassword(requireActivity(), mBinding.password) && isValid) {
                if (pass.equals(confirmPassword)) {
                    mBinding.next.setEnabled(true);
                } else {
                    mBinding.confirmPassword.setError("Password didn't match");
                    mBinding.confirmPassword.showError();
                    mBinding.next.setEnabled(false);
                }
            } else {
                mBinding.next.setEnabled(false);
            }

        });

        mBinding.login.setOnClickListener(view12 -> {
            replaceFragment(new LoginFragment());
//                ActivityUtils.launchFragment(requireActivity(), LoginFragment.class);
        });
        mBinding.next.setOnClickListener(view1 -> {
            if (validator.isValidEmail(requireActivity(), mBinding.email) && validator.isValidPassword(requireActivity(), mBinding.password)) {
                signUp(mBinding.email.getEditText().getText().toString(), mBinding.password.getEditText().getText().toString());
            }
        });

    }

    /**
     * Send request to server for account creation.
     *
     * @param email
     * @param password
     */
    private void signUp(String email, String password) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
//        body.put("password", password);
        // password encrypted for security
        try {
            body.put("password", CryptoUtil.encrypt(password));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        enqueue(getApi().signUp(body), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                addUser(user.user);
                mUserSession.setUserID(user.user.id);
                mUserSession.setToken(user.token);
                mBinding.next.revertAnimation();
//              replaceFragment(new ReferralCodeFragment());
                Bundle bundle = new Bundle();
                bundle.putString("email", mBinding.email.getEditText().getText().toString());
                bundle.putString("otp", "signUp");
                ActivityUtils.launchFragment(requireActivity(), VerifyOTPFragment.class, bundle);
                requireActivity().finish();
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
