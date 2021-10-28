package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentResetPassBinding;
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
 * <p>
 * ResetPasswordFragment
 * Password reset request by user's.
 */
@AndroidEntryPoint
public class ResetPasswordFragment extends NetworkFragment {
    private FragmentResetPassBinding mBinding;

    @Inject
    UserSession mUserSession;
    @Inject
    UserDao userDao;
    private String email, OTP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
            OTP = bundle.getString("otp");
        }
        if (bundle == null)
            throw new IllegalArgumentException("Null bundle");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_pass;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentResetPassBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        Validator validator = new Validator();


        mBinding.password.setObserver((isValid, str) -> {
            String pass = mBinding.password.getEditText().getText().toString();
            String confirmPassword = mBinding.confirmPassword.getEditText().getText().toString();
            if (validator.isValidPassword(requireActivity(), mBinding.confirmPassword) && isValid) {
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

        // observes input change and password matches
        mBinding.confirmPassword.setObserver((isValid, str) -> {
            String pass = mBinding.password.getEditText().getText().toString();

            String confirmPassword = mBinding.confirmPassword.getEditText().getText().toString();
            if (validator.isValidPassword(requireActivity(), mBinding.password) && isValid) {
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

        mBinding.next.setOnClickListener(view1 -> {
            if (validator.isValidPassword(requireActivity(), mBinding.password) && validator.isValidPassword(requireActivity(), mBinding.confirmPassword)) {
                resetPassword(email, OTP, mBinding.password.getEditText().getText().toString(), mBinding.confirmPassword.getEditText().getText().toString());
            }
        });

    }

    /**
     * Sends request to server for updating password by passing
     *
     * @param email
     * @param otp
     * @param password
     * @param cnfrmPassword
     */
    private void resetPassword(String email, String otp, String password, String cnfrmPassword) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);
//        body.put("password", password);
//        body.put("confrim_password", cnfrmPassword);

        // password encryption for security
        try {
            body.put("password", CryptoUtil.encrypt(password));
            body.put("confrim_password", CryptoUtil.encrypt(cnfrmPassword));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        enqueue(getApi().resetPass(body), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                Toast.makeText(requireActivity(), "Reset Password Successfully", Toast.LENGTH_SHORT).show();
                ActivityUtils.launchFragment(requireActivity(), LoginFragment.class);
                requireActivity().finishAffinity();
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
