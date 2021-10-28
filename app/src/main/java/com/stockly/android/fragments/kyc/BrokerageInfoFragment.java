package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentBrokerageInfoBinding;
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
 * BrokerageInfoFragment
 * This Fragment class represents required brokerage info of user's if he has selected yes.
 * Whether user or his family member work for another brokerage. yes.
 * Provide following info required.
 */
@AndroidEntryPoint
public class BrokerageInfoFragment extends NetworkFragment {
    private FragmentBrokerageInfoBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new BrokerageFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_brokerage_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentBrokerageInfoBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        // validator for input validations required
        Validator validator = new Validator();

        mBinding.companyName.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidFullName(requireActivity(), mBinding.personName) && validator.isValidInput(requireActivity(), mBinding.relation) && isValid));

        mBinding.personName.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.companyName) && validator.isValidInput(requireActivity(), mBinding.relation) && isValid));

        mBinding.relation.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.companyName) && validator.isValidFullName(requireActivity(), mBinding.personName) && isValid));

        mBinding.next.setOnClickListener(view1 -> {
            if (validator.isValidInput(requireActivity(), mBinding.companyName) && validator.isValidFullName(requireActivity(), mBinding.personName) && validator.isValidInput(requireActivity(), mBinding.relation)) {
                updateBrokerageInfo(mBinding.companyName.getEditText().getText().toString(), mBinding.personName.getEditText().getText().toString(),
                        mBinding.relation.getEditText().getText().toString());
            }
        });

    }

    /**
     * update brokerage info in case yes, He or his family member belong to
     * another brokerage by passing params
     *
     * @param firmName
     * @param empName
     * @param relation
     */
    private void updateBrokerageInfo(String firmName, String empName, String relation) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("brokerage_firm_name", firmName);
        body.put("brokerage_firm_employee_name", empName);
        body.put("brokerage_firm_employee_relationship", relation);
        body.put("profile_completion", "brokerageInfo");
        enqueue(getApi().updateProfile(body), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new ReviewInfoFragment());

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * get User from local DB.
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
     * update user's info by passing
     *
     * @param user
     */
    public void updateUI(User user) {
        mBinding.companyName.getEditText().setText(user.brokerageFirmName);
        mBinding.personName.getEditText().setText(user.brokerageEmployeeName);
        mBinding.relation.getEditText().setText(user.brokerageEmployeeRelationship);
    }

    @Override
    public void onResume() {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onResume();
    }

    @Override
    public void onPause() {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onPause();
    }
}
