package com.stockly.android.fragments.kyc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentReviewInfoBinding;
import com.stockly.android.databinding.FragmentUnderReviewBinding;
import com.stockly.android.fragments.BaseFragment;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;

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
 * UnderReviewFragment
 * This Fragment class represents status of kyc after user successfully submitted his info.
 * So in case the status for kyc is submitted this Fragment will be presented to user.
 * User's info is under process for verification so he might have to wait for particular time.
 */
@AndroidEntryPoint
public class UnderReviewFragment extends NetworkFragment {
    FragmentUnderReviewBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;
    private User mUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_under_review;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentUnderReviewBinding.bind(view);
        getUser();
        mBinding.underReview.setOnClickListener(view1 -> {

            /**
             * Checks user status.
             * If approved
             * then let user to attach bank to his account using plaid.
             * else
             * take user to home screen.
             */
            if (mUser != null) {
                if (mUser.account_status.equalsIgnoreCase("APPROVED")) {
                    replaceFragment(new BankIntroFragment());
                } else {
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
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
                mUser = user;

            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }


}
