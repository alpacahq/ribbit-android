package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentEmailSentBinding;
import com.stockly.android.databinding.FragmentForgotPassBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.PrefUtils;
import com.stockly.android.validation.Validator;
import com.stockly.android.widgets.CustomEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * EmailSentFragment
 * email sent for verification
 */
@AndroidEntryPoint
public class EmailSentFragment extends NetworkFragment {
    private FragmentEmailSentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        handleBackPress(this, new MainFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_email_sent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentEmailSentBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mBinding.textEmail.setText(bundle.getString("email"));
        }

    }

}
