package com.stockly.android.fragments.kyc;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentLoginBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.models.UserAuth;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CryptoUtil;
import com.stockly.android.utils.PrefUtils;
import com.stockly.android.validation.Validator;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * LoginFragment
 * This Fragment class represent user's a login process for application.
 */
@AndroidEntryPoint
public class LoginFragment extends NetworkFragment {
    private FragmentLoginBinding mBinding;

    @Inject
    UserSession mUserSession;
    @Inject
    UserDao userDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new MainFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentLoginBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        mBinding.forgotPass.setPaintFlags(mBinding.forgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mBinding.email.setObserver((isValid, str) -> {
            Validator validator = new Validator();
            mBinding.next.setEnabled(validator.isValidPassword(requireActivity(), mBinding.password) && isValid);
        });

        mBinding.password.setObserver((isValid, str) -> {
            Validator validator = new Validator();
            mBinding.next.setEnabled(validator.isValidEmail(requireActivity(), mBinding.email) && isValid);
        });


        mBinding.forgotPass.setOnClickListener(v -> ActivityUtils.launchFragment(requireActivity(), ForgotPasswordFragment.class));

        mBinding.signUp.setOnClickListener(v -> ActivityUtils.launchFragment(requireActivity(), SignUpFragment.class));
        mBinding.next.setOnClickListener(view1 -> {
            Validator validator = new Validator();
            if (validator.isValidEmail(requireActivity(), mBinding.email)) {
                getUsers();
                PrefUtils.setBoolean(requireActivity(), CommonKeys.KEY_ACCOUNT, false);
                login(mBinding.email.getEditText().getText().toString(), mBinding.password.getEditText().getText().toString());
            }
        });

    }

    /**
     * It will send credential to server by passing params
     *
     * @param email
     * @param password
     */
    private void login(String email, String password) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
//        body.put("password", password);

        // encrypt password for security
        try {
            body.put("password", CryptoUtil.encrypt(password));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        enqueue(getApi().login(body), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                addUser(user.user);
                mUserSession.setUserID(user.user.id);
                mUserSession.setToken(user.token);
                mBinding.next.revertAnimation();
//              ActivityUtils.launchFragment(requireActivity(), PhoneNumberFragment.class);
                moveNext(user.user);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                if (!isInternetIssue) {
                    showErrorMessage(mBinding.message, error.message);
                    return true;
                }
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * get user from local DB.
     */
    public void getUsers() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: ");
                deleteUser(user);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * It allow user table to delete from DB.
     *
     * @param user
     */
    public void deleteUser(User user) {
        userDao.deleteUsers(user);
    }


    /**
     * It checks if user is active allow to continue and check profile completion tag
     * and move to that particular stage where he left
     * else
     * start from beginning.
     *
     * @param user
     */
    public void moveNext(User user) {
//        Log.d(">>>", "moveNext: " + user.profileCompletion);
        if (user.active) {
            if (user.profileCompletion.equalsIgnoreCase("referral")) {
                replaceFragment(new NameFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("name")) {
                replaceFragment(new PhoneNumberFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("phone")) {
                replaceFragment(new DateOfBirthFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("dob")) {
                replaceFragment(new AddressFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("address")) {
                replaceFragment(new CitizenshipFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("citizenship")) {
                replaceFragment(new VerifyIdentityFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("verifyidentity")) {
                replaceFragment(new SecurityNumberFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("ssn")) {
                replaceFragment(new ExperienceFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("investingexperience")) {
                replaceFragment(new FundingSourceFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("funding")) {
                replaceFragment(new EmploymentFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("employed")) {
                if (user.employmentStatus.equalsIgnoreCase("employed")) {
                    replaceFragment(new EmploymentInfoFragment());
                } else {
                    replaceFragment(new FamilyFragment());
                }
            } else if (user.profileCompletion.equalsIgnoreCase("employmentInfo")) {
                replaceFragment(new FamilyFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("shareholder")) {
                if (user.publicShareholder.equalsIgnoreCase("Yes")) {
                    replaceFragment(new FamilyInfoFragment());
                } else {
                    replaceFragment(new BrokerageFragment());
                }
            } else if (user.profileCompletion.equalsIgnoreCase("shareholderInfo")) {
                replaceFragment(new BrokerageFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("brokerage")) {
                if (user.anotherBrokerage.equalsIgnoreCase("Yes")) {
                    replaceFragment(new BrokerageInfoFragment());
                } else {
                    replaceFragment(new ReviewInfoFragment());
                }
            } else if (user.profileCompletion.equalsIgnoreCase("brokerageInfo")) {
                replaceFragment(new ReviewInfoFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("submitted")) {
                replaceFragment(new TermsFragment());
            } else if (user.profileCompletion.equalsIgnoreCase("complete")) {
                if (!TextUtils.isEmpty(user.account_status)) {
                    if (user.account_status.equalsIgnoreCase("APPROVED")) {
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } else if (user.account_status.equalsIgnoreCase("REJECTED")) {
                        replaceFragment(new KycRejectedFragment());
                    } else {
                        replaceFragment(new UnderReviewFragment());
                    }
                } else {
                    replaceFragment(new TermsFragment());
                }
            } else {
                ReferralCodeFragment fragment = new ReferralCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("verify", "new");
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("email", mBinding.email.getEditText().getText().toString());
            bundle.putString("otp", "login");
            ActivityUtils.launchFragment(requireActivity(), VerifyOTPFragment.class, bundle);
        }
    }

}
