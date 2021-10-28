package com.stockly.android.fragments.kyc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentSecurityNumberBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.validation.Validator;
import com.stockly.android.widgets.CustomEditText;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;


/**
 * SecurityNumberFragment
 * This Fragment class represents social security number of user's
 */
@AndroidEntryPoint
public class SecurityNumberFragment extends NetworkFragment {
    FragmentSecurityNumberBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new VerifyIdentityFragment());
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite, null));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_security_number;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentSecurityNumberBinding.bind(view);
        mBinding.toolbarSec.toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.colorPrimary));
        setUpToolBar(mBinding.toolbarSec.toolbar);

        Validator validator = new Validator();

        mBinding.iconEye.setOnClickListener(v -> {
            if (mBinding.pin3.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                mBinding.iconEye.setImageResource(R.drawable.ic_visibility_off_eye);

                //Show Password
                mBinding.pin1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mBinding.pin2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mBinding.pin3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {

                mBinding.iconEye.setImageResource(R.drawable.ic_visibility_eye);
                //Hide Password
                mBinding.pin1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mBinding.pin2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mBinding.pin3.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }

        });

        // check input and validate
        mBinding.pin1.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mBinding.pin1.length() > 2) {
                    Validator validator = new Validator();
                    mBinding.next.setEnabled(validator.isValidSSN3(requireActivity(), mBinding.pin1, mBinding.errorSsn) && validator.isValidSSN2(requireActivity(), mBinding.pin2, mBinding.errorSsn) && validator.isValidSSN4(requireActivity(), mBinding.pin3, mBinding.errorSsn));

                    mBinding.pin1.post(() -> mBinding.pin2.requestFocus());
                }

                mBinding.next.setEnabled(validator.isValidSSN3(requireActivity(), mBinding.pin1, mBinding.errorSsn) && validator.isValidSSN2(requireActivity(), mBinding.pin2, mBinding.errorSsn) && validator.isValidSSN4(requireActivity(), mBinding.pin3, mBinding.errorSsn));

            }
        });

        // check input and validate
        mBinding.pin2.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mBinding.pin2.length() > 1) {

                    mBinding.pin2.post(() -> mBinding.pin3.requestFocus());
                }

                mBinding.next.setEnabled(validator.isValidSSN3(requireActivity(), mBinding.pin1, mBinding.errorSsn) && validator.isValidSSN2(requireActivity(), mBinding.pin2, mBinding.errorSsn) && validator.isValidSSN4(requireActivity(), mBinding.pin3, mBinding.errorSsn));
            }
        });

        // check input and validate
        mBinding.pin3.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mBinding.next.setEnabled(validator.isValidSSN3(requireActivity(), mBinding.pin1, mBinding.errorSsn) && validator.isValidSSN2(requireActivity(), mBinding.pin2, mBinding.errorSsn) && validator.isValidSSN4(requireActivity(), mBinding.pin3, mBinding.errorSsn));

            }
        });

        mBinding.next.setOnClickListener(view1 -> {

            if (validator.isValidSSN3(requireActivity(), mBinding.pin1, mBinding.errorSsn) && validator.isValidSSN2(requireActivity(), mBinding.pin2, mBinding.errorSsn) && validator.isValidSSN4(requireActivity(), mBinding.pin3, mBinding.errorSsn)) {

                updateProfileSecurityNumber(mBinding.pin1.getText().toString() + mBinding.pin2.getText().toString() + mBinding.pin3.getText().toString());
            }
        });
        geUser();

    }

    /**
     * get user from local
     */
    private void geUser() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: ");
                updateUI(user);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * Update user's social security number to server.
     *
     * @param security_number
     */
    private void updateProfileSecurityNumber(String security_number) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("tax_id", security_number);
        //USA_SSN
        body.put("tax_id_type", "USA_SSN");
        body.put("profile_completion", "ssn");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new ExperienceFragment());
                Log.d(">>>", "onSuccess: " + user.taxId);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * Update UI from user's pre-existing record
     *
     * @param user
     */
    public void updateUI(User user) {
        if (!user.taxId.equalsIgnoreCase("") && user.taxId.length() > 8) {
            mBinding.pin1.setText(user.taxId.substring(0, 3));
            mBinding.pin2.setText(user.taxId.substring(3, 5));
            mBinding.pin3.setText(user.taxId.substring(5, 9));
        }
    }
}
