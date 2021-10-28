package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentEmploymentBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;


/**
 * EmploymentFragment
 * This Fragment class represents employment info required from user.
 * If employed or not, what is current status? etc.
 */
@AndroidEntryPoint
public class EmploymentFragment extends NetworkFragment {
    private FragmentEmploymentBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new FundingSourceFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_employment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentEmploymentBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        mBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mBinding.next.setEnabled(group.getCheckedRadioButtonId() != -1);
        });

        mBinding.next.setOnClickListener(view1 -> {
            if (mBinding.radioGroup.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                Toast.makeText(requireActivity(), "Please select One", Toast.LENGTH_SHORT).show();
            } else {
                int id = mBinding.radioGroup.getCheckedRadioButtonId();
                RadioButton button = (RadioButton) requireActivity().findViewById(id);
                updateEmploymentStatus(getString(button.getText().toString()));
            }
        });


    }

    /**
     * update user's employment status by passing
     *
     * @param employment
     */
    private void updateEmploymentStatus(String employment) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("employment_status", employment);
        body.put("profile_completion", "employed");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                if (employment.equalsIgnoreCase("employed")) {
                    replaceFragment(new EmploymentInfoFragment());
                } else {
                    replaceFragment(new FamilyFragment());
                }
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
        // DB User
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
     * update UI from user existing records
     *
     * @param user
     */
    public void updateUI(User user) {
        if (user.employmentStatus.equalsIgnoreCase("employed")) {
            mBinding.employed.setChecked(true);
        } else if (user.employmentStatus.equalsIgnoreCase("unemployed")) {
            mBinding.unEmployed.setChecked(true);
        } else if (user.employmentStatus.equalsIgnoreCase("retired")) {
            mBinding.retired.setChecked(true);
        } else if (user.employmentStatus.equalsIgnoreCase("student")) {
            mBinding.student.setChecked(true);
        }
    }

    /**
     * It will return value to send to server by passing key to it.
     *
     * @param key
     * @return value
     */
    public String getString(String key) {
        String value = "";
        if (key.equalsIgnoreCase("Employed")) {
            value = "employed";
        } else if (key.equalsIgnoreCase("Unemployed")) {
            value = "unemployed";
        } else if (key.equalsIgnoreCase("Retired")) {
            value = "retired";
        } else if (key.equalsIgnoreCase("Student")) {
            value = "student";
        }
        return value;
    }
}
