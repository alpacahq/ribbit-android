package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentFundingSourceBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * FundingSourceFragment
 * This Fragment class represents that you or your family member having Earning sources.
 * Employed or family business with multiple selection.
 */
@AndroidEntryPoint
public class FundingSourceFragment extends NetworkFragment {
    private FragmentFundingSourceBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new ExperienceFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_funding_source;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentFundingSourceBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        mBinding.income.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));

        mBinding.investment.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));
        mBinding.inheritance.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));
        mBinding.business.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));
        mBinding.savings.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));

        mBinding.family.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.next.setEnabled(getSelectedCount().size() != 0));


        mBinding.next.setOnClickListener(view1 -> {
            if (getSelectedCount().size() == 0) {
                // no radio buttons are checked
                Toast.makeText(requireActivity(), "Please select One", Toast.LENGTH_SHORT).show();
            } else {

                List<String> newValue = new ArrayList<>();
                for (String val : getSelectedCount()) {
                    newValue.add(getString(val));
                }
                String values = TextUtils.join(",", newValue);

                updateFundingSource(values);

            }
        });

    }

    /**
     * Update funding sources provided by user to server by passing
     *
     * @param fundingSource
     */
    private void updateFundingSource(String fundingSource) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("funding_source", fundingSource);
        body.put("profile_completion", "funding");
        enqueue(getApi().updateProfile(body), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new EmploymentFragment());
                Log.d(">>>", "onSuccess: " + user.fundingSource);
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
     * Update UI from pre-existing user record
     *
     * @param user
     */
    public void updateUI(User user) {
        List<String> items = Arrays.asList(user.fundingSource.split("\\s*,\\s*"));
        for (String funding : items) {
            if (funding.equalsIgnoreCase("employment_income")) {
                mBinding.income.setChecked(true);
            } else if (funding.equalsIgnoreCase("investments")) {
                mBinding.investment.setChecked(true);
            } else if (funding.equalsIgnoreCase("inheritance")) {
                mBinding.inheritance.setChecked(true);
            } else if (funding.equalsIgnoreCase("business_income")) {
                mBinding.business.setChecked(true);
            } else if (funding.equalsIgnoreCase("savings")) {
                mBinding.savings.setChecked(true);
            } else if (funding.equalsIgnoreCase("family")) {
                mBinding.family.setChecked(true);
            }
        }
        mBinding.next.setEnabled(getSelectedCount().size() != 0);
    }

    /**
     * set pre-selected option for user's funding sources.
     *
     * @return
     */
    private List<String> getSelectedCount() {
        List<String> selectedList = new ArrayList<>();
        for (int i = 0; i < mBinding.radioGroup.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) mBinding.radioGroup.getChildAt(i);
            if (checkBox.isChecked()) {
                selectedList.add(checkBox.getText().toString());
            }
        }
        return selectedList;
    }

    /**
     * It will return value by passing key to this for sending it to server.
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String value = "";
        if (key.equalsIgnoreCase(getString(R.string.employment_income))) {
            value = "employment_income";
        } else if (key.equalsIgnoreCase(getString(R.string.investments))) {
            value = "investments";
        } else if (key.equalsIgnoreCase(getString(R.string.inheritance))) {
            value = "inheritance";
        } else if (key.equalsIgnoreCase(getString(R.string.business_income))) {
            value = "business_income";
        } else if (key.equalsIgnoreCase(getString(R.string.savings))) {
            value = "savings";
        } else if (key.equalsIgnoreCase(getString(R.string.family))) {
            value = "family";
        }
        return value;
    }
}
