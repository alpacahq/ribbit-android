package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentPhoneNumberBinding;
import com.stockly.android.fragments.NetworkFragment;
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
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * PhoneNumberFragment
 * This fragment class represent Required phone number from user's.
 */
@AndroidEntryPoint
public class PhoneNumberFragment extends NetworkFragment {

    private FragmentPhoneNumberBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new NameFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone_number;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentPhoneNumberBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        mBinding.phoneNumber.setObserver((isValid, str) -> mBinding.next.setEnabled(isValid));

        mBinding.phoneNumber.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mBinding.next.setOnClickListener(view1 -> {
            Validator validator = new Validator();
            if (validator.isValidPhone(requireActivity(), mBinding.phoneNumber, mBinding.countryCode)) {
                updateProfileNumber(PhoneNumberUtils.formatNumberToE164(mBinding.phoneNumber.getEditText().getText().toString(), "US"));
            }
        });

    }

    /**
     * It updates user's phone number to server by passing
     *
     * @param phoneNumber
     */
    private void updateProfileNumber(String phoneNumber) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("mobile", phoneNumber);
        body.put("profile_completion", "phone");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new DateOfBirthFragment());
//                Log.d(">>>", "onSuccess: " + user.countryCode + user.mobile);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

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
     * It updates UI from user's existing record.
     *
     * @param user
     */
    public void updateUI(User user) {
        if (!user.mobile.equalsIgnoreCase("")) {
            mBinding.phoneNumber.getEditText().setText(user.mobile.substring(2));
        }
    }
}
