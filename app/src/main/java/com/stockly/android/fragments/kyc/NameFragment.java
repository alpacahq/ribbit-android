package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentNameBinding;
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
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * NameFragment
 * This fragment class represent user first name and last name
 * required info for kcy username
 */
@AndroidEntryPoint
public class NameFragment extends NetworkFragment {
    private FragmentNameBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new ReferralCodeFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_name;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentNameBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);
        Validator validator = new Validator();

        getUser();
        mBinding.lastName.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.firstName) && isValid));

        mBinding.firstName.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.lastName) && isValid));


        // The callback can be enabled or disabled here or in handleOnBackPressed()

        mBinding.next.setOnClickListener(view1 -> {

            if (validator.isValidName(requireActivity(), mBinding.firstName) && validator.isValidInput(requireActivity(), mBinding.lastName)) {
                updateProfileName(mBinding.firstName.getEditText().getText().toString(), mBinding.lastName.getEditText().getText().toString());
            }
        });


    }

    /**
     * It will update user's first and last name to server by passing
     *
     * @param first_name
     * @param last_name
     */
    private void updateProfileName(String first_name, String last_name) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("first_name", first_name);
        body.put("last_name", last_name);
        body.put("profile_completion", "name");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
//                ActivityUtils.launchFragment(requireActivity(), PhoneNumberFragment.class);
                replaceFragment(new PhoneNumberFragment());
                Log.d(">>>", "onSuccess: " + user.firstName);

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
     * Update UI from existing user's record
     *
     * @param user
     */
    public void updateUI(User user) {
        mBinding.firstName.getEditText().setText(user.firstName);
        mBinding.lastName.getEditText().setText(user.lastName);
    }
}
