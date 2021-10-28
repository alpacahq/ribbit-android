package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentBrokerageBinding;
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
 * BrokerageFragment
 * This Fragment class represents required brokerage info of user's.
 * Whether user or his family member work for another brokerage. yes/no?
 */
@AndroidEntryPoint
public class BrokerageFragment extends NetworkFragment {
    private FragmentBrokerageBinding mBinding;
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
        return R.layout.fragment_brokerage;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentBrokerageBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);
        getUser();

        mBinding.no.setOnClickListener(view1 -> updateBrokerage("No", mBinding.no));

        mBinding.yes.setOnClickListener(view12 -> updateBrokerage("Yes", mBinding.yes));


    }

    /**
     * update user's info, whether yes?no by passing
     *
     * @param value
     * @param view
     */
    private void updateBrokerage(String value, CircularProgressButton view) {
        view.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("another_brokerage", value);
        body.put("profile_completion", "brokerage");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                view.revertAnimation();
                if (value.equalsIgnoreCase("Yes")) {
                    replaceFragment(new BrokerageInfoFragment());
                } else {
                    replaceFragment(new ReviewInfoFragment());
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
                if (user.publicShareholder.equalsIgnoreCase("Yes")) {
                    handleBackPress(BrokerageFragment.this, new FamilyInfoFragment());
                } else {
                    handleBackPress(BrokerageFragment.this, new FamilyFragment());
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
