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
import com.stockly.android.databinding.FragmentExperienceBinding;
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
 * ExperienceFragment
 * This Fragment class represents investing experience user's have .
 * If experienced or beginner etc.
 */
@AndroidEntryPoint
public class ExperienceFragment extends NetworkFragment {
    private FragmentExperienceBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new SecurityNumberFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_experience;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentExperienceBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        mBinding.radioG.setOnCheckedChangeListener((group, checkedId) -> {
            mBinding.next.setEnabled(group.getCheckedRadioButtonId() != -1);
        });


        mBinding.next.setOnClickListener(view1 -> {
            if (mBinding.radioG.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                Toast.makeText(requireActivity(), "Please select One", Toast.LENGTH_SHORT).show();
            } else {
                // one of the radio buttons is checked
                int id = mBinding.radioG.getCheckedRadioButtonId();
                RadioButton button = (RadioButton) requireActivity().findViewById(id);
                updateInvestingExperience(button.getText().toString());
            }

        });

    }

    /**
     * Update Investing experience of user's to server by passing
     *
     * @param experience
     */
    private void updateInvestingExperience(String experience) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("investing_experience", experience);
        body.put("profile_completion", "investingexperience");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new FundingSourceFragment());
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
     * Update UI from user's pre-existing record
     *
     * @param user
     */
    public void updateUI(User user) {
        if (user.investingExperience.equalsIgnoreCase(getString(R.string.none))) {
            mBinding.experienceNone.setChecked(true);
        } else if (user.investingExperience.equalsIgnoreCase(getString(R.string.not_much))) {
            mBinding.notMuch.setChecked(true);
        } else if (user.investingExperience.equalsIgnoreCase(getString(R.string.i_know_what_i_m_doing))) {
            mBinding.iKnow.setChecked(true);
        } else if (user.investingExperience.equalsIgnoreCase(getString(R.string.i_m_an_expert))) {
            mBinding.expert.setChecked(true);
        }
    }
}
