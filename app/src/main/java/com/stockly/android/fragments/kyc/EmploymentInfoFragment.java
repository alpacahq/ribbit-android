package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentEmploymentInfoBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.validation.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * EmploymentInfoFragment
 * This Fragment class represents employment info required from user.
 * If employed, Provide info represented by this.
 */
@AndroidEntryPoint
public class EmploymentInfoFragment extends NetworkFragment {
    private FragmentEmploymentInfoBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new EmploymentFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_employment_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentEmploymentInfoBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        Validator validator = new Validator();

        mBinding.employerName.setObserver((isValid, str) -> {
            mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.occupation) && isValid);
        });

        mBinding.occupation.setObserver((isValid, str) -> {
            mBinding.next.setEnabled(validator.isValidFullName(requireActivity(), mBinding.employerName) && isValid);
        });

        // The callback can be enabled or disabled here or in handleOnBackPressed()

        mBinding.next.setOnClickListener(view1 -> {
            Validator validator1 = new Validator();
            if (validator1.isValidFullName(requireActivity(), mBinding.employerName) && validator1.isValidInput(requireActivity(), mBinding.occupation)) {

                updateEmploymentInfo(mBinding.employerName.getEditText().getText().toString(), mBinding.occupation.getEditText().getText().toString());
            }
        });

    }

    /**
     * Update employment info of user's required information
     *
     * @param employer_name
     * @param occupation
     */
    private void updateEmploymentInfo(String employer_name, String occupation) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("employer_name", employer_name);
        body.put("occupation", occupation);
        body.put("profile_completion", "employmentInfo");
        enqueue(getApi().updateProfile(body), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new FamilyFragment());
//                Log.d(">>>", "onSuccess: " + user.employerName);

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
     * Update UI from pre-existing user's data
     *
     * @param user
     */
    public void updateUI(User user) {
        mBinding.employerName.getEditText().setText(user.employerName);
        mBinding.occupation.getEditText().setText(user.occupation);
    }
}
