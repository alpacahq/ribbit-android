package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentReviewInfoBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DateUtilz;
import com.stockly.android.utils.PrefUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

import static com.stockly.android.utils.DateUtilz.formatDateServer;
import static com.stockly.android.utils.DateUtilz.parseDobDate;
import static com.stockly.android.utils.DateUtilz.parseServerDate;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * ReviewInfoFragment
 * This fragment class represents Overall Info entered by user through out
 * kyc process
 */
@AndroidEntryPoint
public class ReviewInfoFragment extends NetworkFragment {
    private FragmentReviewInfoBinding mBinding;
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
        return R.layout.fragment_review_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentReviewInfoBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);
        getUser();

        mBinding.submit.setOnClickListener(view1 -> updateProfileReview());

    }

    /**
     * Update server with profile completion tag.
     */
    private void updateProfileReview() {
        mBinding.submit.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("profile_completion", "submitted");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.submit.revertAnimation();
                replaceFragment(new TermsFragment());
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.submit.revertAnimation();
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
                if (user.anotherBrokerage.equalsIgnoreCase("Yes")) {
                    handleBackPress(ReviewInfoFragment.this, new BrokerageInfoFragment());
                } else {
                    handleBackPress(ReviewInfoFragment.this, new BrokerageFragment());
                }
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
     * Update all entered record of kyc by user to UI.
     *
     * @param user
     */
    public void updateUI(User user) {
        if (user != null) {
            mBinding.name.setText(user.firstName + " " + user.lastName);
            mBinding.email.setText(user.email);
            mBinding.phone.setText(PhoneNumberUtils.formatNumber(user.mobile.substring(2), Locale.getDefault().getCountry()));

            updateDob(user);

            if (!TextUtils.isEmpty(user.unitApt)) {
                mBinding.address.setText(user.address + "\n" + user.unitApt + "\n" + user.city + ", " + user.state + " " + CommonUtils.getValue(user.zipCode));
            } else {
                mBinding.address.setText(user.address + "\n" + user.unitApt + " " + user.city + ", " + user.state + " " + CommonUtils.getValue(user.zipCode));
            }
            mBinding.citizenship.setText(user.country);
            mBinding.ssn.setText(user.taxId.substring(0, 3) + "-" + user.taxId.substring(3, 5) + "-" + user.taxId.substring(5, 9));
            mBinding.experience.setText(user.investingExperience);
            mBinding.employment.setText(user.employmentStatus);
            if (user.employmentStatus.equalsIgnoreCase("employed")) {
                mBinding.textEmployerName.setVisibility(View.VISIBLE);
                mBinding.employerName.setVisibility(View.VISIBLE);
                mBinding.textOccupation.setVisibility(View.VISIBLE);
                mBinding.occupation.setVisibility(View.VISIBLE);
                mBinding.employerName.setText(user.employerName);
                mBinding.occupation.setText(user.occupation);
            }
            mBinding.family.setText(user.publicShareholder);
            if (user.publicShareholder.equalsIgnoreCase("Yes")) {
//                mBinding.textCompanyName.setVisibility(View.VISIBLE);
//                mBinding.companyName.setVisibility(View.VISIBLE);
                mBinding.textSymbol.setVisibility(View.VISIBLE);
                mBinding.symbol.setVisibility(View.VISIBLE);
//                mBinding.companyName.setText(user.shareholderCompanyName);
                mBinding.symbol.setText(user.stockSymbol);
            }
            mBinding.brokerage.setText(user.anotherBrokerage);
            if (user.anotherBrokerage.equalsIgnoreCase("Yes")) {
                mBinding.textCompanyNameBrok.setVisibility(View.VISIBLE);
                mBinding.companyNameBrok.setVisibility(View.VISIBLE);
                mBinding.textPersonNameBrok.setVisibility(View.VISIBLE);
                mBinding.personNameBrok.setVisibility(View.VISIBLE);
                mBinding.textRelation.setVisibility(View.VISIBLE);
                mBinding.relation.setVisibility(View.VISIBLE);
                mBinding.companyNameBrok.setText(user.brokerageFirmName);
                mBinding.personNameBrok.setText(user.brokerageEmployeeName);
                mBinding.relation.setText(user.brokerageEmployeeRelationship);
            }
        }

    }

    /**
     * Format Date of DOB.
     *
     * @param user
     */
    public void updateDob(User user) {
        if (!TextUtils.isEmpty(user.dob)) {
            try {
                String date = DateUtilz.formatDateDob(DateUtilz.parseServerDate(user.dob));
                String[] splitStrings = date.split("\\s+");
                String month = splitStrings[0];
                String day = splitStrings[1];
                String year = splitStrings[2];
                mBinding.date.setText(month + "/" + day + "/" + year);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
