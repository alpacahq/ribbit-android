package com.stockly.android.fragments.kyc;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentVerifyOtpBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.models.UserAuth;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.aabhasjindal.otptextview.OTPListener;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * VerifyOTPFragment
 * This Fragment represents some Verification of OTP code that was
 * sent via email at time of sign up or forgot password.
 */

@AndroidEntryPoint
public class VerifyOTPFragment extends NetworkFragment {
    private FragmentVerifyOtpBinding mBinding;
    private String email, keyOTP;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
            keyOTP = bundle.getString("otp");
        }
        if (bundle == null)
            throw new IllegalArgumentException("Null bundle");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verify_otp;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentVerifyOtpBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        mBinding.textInstruction.setText(email);

        mBinding.openEmail.setPaintFlags(mBinding.openEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mBinding.openEmail.setOnClickListener(view1 -> {
//                Intent intent = requireActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
//                startActivity(intent);
//                Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
            /**
             * Open up default email app
             */
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivityFromFragment(VerifyOTPFragment.this, Intent.createChooser(intent, "Email"), 11);

        });

        mBinding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
                if (mBinding.otpView.getOTP().length() < 6) {
                    mBinding.next.setEnabled(false);
                }

            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                mBinding.next.setEnabled(true);
            }

        });

        mBinding.next.setOnClickListener(v -> {
            if (keyOTP.equalsIgnoreCase("forgot")) {
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("otp", mBinding.otpView.getOTP());
                ActivityUtils.launchFragment(requireActivity(), ResetPasswordFragment.class, bundle);
            } else {
                verifyOTP(mBinding.otpView.getOTP());
            }
        });

        mBinding.sendCode.setOnClickListener(v -> resendEmail(email));

    }

    /**
     * Send Otp code to server for verification.
     *
     * @param otp code.
     */
    private void verifyOTP(String otp) {
        mBinding.next.startAnimation();
        enqueue(getApi().verifyOTP(otp), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                mBinding.next.revertAnimation();
                getUser();

//                ReferralCodeFragment fragment = new ReferralCodeFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("email", email);
//                bundle.putString("otp", keyOTP);
//                bundle.putString("verify", "new");
//                fragment.setArguments(bundle);
//                replaceFragment(fragment);
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
     * Resend email if didn't received a code.
     *
     * @param email
     */
    private void resendEmail(String email) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        enqueue(getApi().forgotPass(body), new NetworkFragment.CallBack<UserAuth>() {
            @Override
            public void onSuccess(UserAuth user) {
                mBinding.next.revertAnimation();
                Toast.makeText(requireActivity(), "OTP sent successfully.", Toast.LENGTH_SHORT).show();
                Log.d(">>>", "onSuccess: ");
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
     * It checks if user is active allow to continue and check profile completion tag
     * and move to that particular stage where he left
     * else
     * start from beginning.
     *
     * @param user
     */
    public void moveNext(User user) {
        Log.d(">>>", "moveNext: " + user.profileCompletion);
        if (user.profileCompletion.equalsIgnoreCase("referral")) {
            replaceFragment(new NameFragment());
        } else if (user.profileCompletion.equalsIgnoreCase("name")) {
            replaceFragment(new PhoneNumberFragment());
        } else if (user.profileCompletion.equalsIgnoreCase("phone")) {
            replaceFragment(new DateOfBirthFragment());
        } else if (user.profileCompletion.equalsIgnoreCase("dob_lay")) {
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
            bundle.putString("otp", keyOTP);
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }

    }

    public void getUser() {
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                moveNext(user);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

}
