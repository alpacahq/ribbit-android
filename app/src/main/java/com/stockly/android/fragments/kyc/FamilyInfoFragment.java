package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentFamilyInfoBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;


/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * FamilyInfoFragment
 * This Fragment class represents that you or your family member 10% shareholder.
 * Yes 10% shareholder at any publicly traded company.
 * Provide further info related to company/org etc.
 */
@AndroidEntryPoint
public class FamilyInfoFragment extends NetworkFragment {
    private FragmentFamilyInfoBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;
    private ArrayAdapter<Positions> adapter;
    boolean isSelected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new FamilyFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_family_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentFamilyInfoBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();
//        mBinding.companyName.setObserver((isValid, str) -> {
//            if (!TextUtils.isEmpty(mBinding.symbol.getText().toString()) && isValid) {
//                mBinding.next.setEnabled(true);
//            } else {
//                mBinding.next.setEnabled(false);
//            }
//        });

        mBinding.symbol.setThreshold(1);

        mBinding.symbol.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    getAssetsList(charSequence.toString());
                }
                mBinding.next.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
            }
        });


        mBinding.next.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(mBinding.symbol.getText().toString())) {
                updateFamilyShareholderInfo(mBinding.symbol.getText().toString());
            }
        });

        mBinding.symbol.setOnItemClickListener((parent, view12, position, id) -> {
            isSelected = true;
            mBinding.symbol.dismissDropDown();
        });

    }

    /**
     * Update user 10% share holder company info to server by passing
     *
     * @param symbol
     */
    private void updateFamilyShareholderInfo(String symbol) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
//        body.put("shareholder_company_name", company_name);
        body.put("stock_symbol", symbol);
        body.put("profile_completion", "shareholderInfo");
        enqueue(getApi().updateProfile(body), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new BrokerageFragment());

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
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: " + user.referralCode);
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
     * Update from pre-existing record
     *
     * @param user
     */
    public void updateUI(User user) {
//        if (user.shareholderCompanyName != null) {
//            mBinding.companyName.getEditText().setText(user.shareholderCompanyName);
//        }
        mBinding.symbol.setText(user.stockSymbol);
    }

    /**
     * Search List of Stock ticker/assets from server and set it to drop down list.
     *
     * @param q
     */
    private void getAssetsList(String q) {
        enqueue(getApi().getAssetsList(q), new NetworkFragment.CallBack<List<Positions>>() {
            @Override
            public void onSuccess(List<Positions> list) {
                if (list != null && list.size() != 0) {
                    adapter = new ArrayAdapter<Positions>(getActivity(), android.R.layout.simple_dropdown_item_1line, list);
                    mBinding.symbol.setAdapter(adapter);

                    mBinding.symbol.post(new Runnable() {
                        @Override
                        public void run() {

                            if (!isSelected) {
                                mBinding.symbol.showDropDown();
                            } else {
                                isSelected = false;
                            }
                        }
                    });

                }

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }

    @Override
    public void onResume() {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onResume();
    }

    @Override
    public void onPause() {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onPause();
    }
}
