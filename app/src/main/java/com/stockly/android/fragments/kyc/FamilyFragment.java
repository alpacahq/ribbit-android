package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentFamilyBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * FamilyFragment
 * This Fragment class represents that you or your family member 10% shareholder.
 * If 10% shareholder at any publicly traded company. yes/no?
 */
@AndroidEntryPoint
public class FamilyFragment extends NetworkFragment {
    private FragmentFamilyBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_family;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentFamilyBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);
        getUser();

        mBinding.no.setOnClickListener(view1 -> updateFamilyShareHolder("No", mBinding.no));

        mBinding.yes.setOnClickListener(view12 -> updateFamilyShareHolder("Yes", mBinding.yes));

    }

    /**
     * Update whether 10% shareholder to server by passing yes/no
     *
     * @param value
     * @param view
     */
    private void updateFamilyShareHolder(String value, CircularProgressButton view) {
        view.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("public_shareholder", value);
        body.put("profile_completion", "shareholder");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                view.revertAnimation();
                if (value.equalsIgnoreCase("Yes")) {
                    replaceFragment(new FamilyInfoFragment());
                } else {
                    replaceFragment(new BrokerageFragment());
                }
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                view.revertAnimation();
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
                //check if employed go back employment info screen
                // else employment screen
                if (user.employmentStatus.equalsIgnoreCase("employed")) {
                    handleBackPress(FamilyFragment.this, new EmploymentInfoFragment());
                } else {
                    handleBackPress(FamilyFragment.this, new EmploymentFragment());
                }

            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }
}
