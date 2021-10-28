package com.stockly.android.fragments.kyc;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentVerifyIdentityBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * VerifyIdentityFragment
 * This Fragment represents some Verification rules that are defined for this app.
 */
@AndroidEntryPoint
public class VerifyIdentityFragment extends NetworkFragment {
    private FragmentVerifyIdentityBinding mBinding;
    private String taxIdType;
    @Inject
    UserDao userDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        handleBackPress(this, new CitizenshipFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verify_identity;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentVerifyIdentityBinding.bind(view);
        mBinding.toolbar.toolbar.setBackgroundColor(requireActivity().getResources().getColor(R.color.colorPrimary));
        mBinding.toolbar.toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.colorWhite));
        setUpToolBar(mBinding.toolbar.toolbar);


        Bundle bundle = getArguments();
        if (bundle != null) {
            taxIdType = bundle.getString("taxIdType");
        }

        mBinding.next.setOnClickListener(view1 -> updateProfileVerification());

    }

    /**
     * Update verification tag to server.
     */
    private void updateProfileVerification() {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("profile_completion", "verifyidentity");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                SecurityNumberFragment fragment = new SecurityNumberFragment();
                Bundle bundle = new Bundle();
                bundle.putString("taxIdType", taxIdType);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                Log.d(">>>", "onSuccess: " + user.verified);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

}
