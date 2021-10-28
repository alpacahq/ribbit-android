package com.stockly.android.fragments.wallet;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.stockly.android.R;
import com.stockly.android.adapter.RewardsDialogAdapter;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.databinding.DialogChangeBankBinding;
import com.stockly.android.databinding.DialogRewardBinding;
import com.stockly.android.fragments.NetworkCallFragment;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.listners.DataListener;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Payment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.Success;
import com.stockly.android.utils.PrefUtils;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * DeAttachBankDialogFragment
 * It represents to user to remove attached bank account.
 */

@AndroidEntryPoint
public class DeAttachBankDialogFragment extends DialogFragment implements View.OnClickListener {
    private DialogChangeBankBinding mBinding;
    private BankAccount account;
    private NetworkCallFragment networkCall;
    private DataListener<String> mDataListener;
    @Inject
    BankAccountDao accountDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_bank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DialogChangeBankBinding.bind(view);
        networkCall = (NetworkCallFragment) getChildFragmentManager().findFragmentById(R.id.network_fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            account = bundle.getParcelable("account");
            if (account != null) {
                updateUi(account);
            }
        }
        mBinding.goBack.setOnClickListener(this);
        mBinding.back.setOnClickListener(this);
        mBinding.remove.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            dismiss();
        } else if (v.getId() == R.id.go_back) {
            dismiss();
        } else if (v.getId() == R.id.remove) {
            if (account != null) {
                deAttachBank(account.id);
            }
        }
    }

    public void setDataListener(DataListener listener) {
        mDataListener = listener;
    }

    /**
     * Set data of attached bank account
     *
     * @param account instance.
     */
    public void updateUi(BankAccount account) {
        if (account != null) {
            mBinding.bankName.setText(account.nickname);
            mBinding.accountNo.setText(getString(R.string.account_xxx, account.bankAccountNumber.substring(account.bankAccountNumber.length() - 4)));
        }
    }

    /**
     * Call server to remove attached bank account by passing
     *
     * @param bank_id string id.
     */
    private void deAttachBank(String bank_id) {
        networkCall.enqueue(networkCall.getApi().deAttachBank(bank_id), networkCall.new CallBack<Success>() {
            @Override
            public void onSuccess(Success success) {
                accountDao.deleteAll();
                dismiss();
                PrefUtils.setBoolean(requireActivity(), CommonKeys.KEY_ACCOUNT, false);
                if (mDataListener != null) {
                    mDataListener.onDataListener("deleted");
                }
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }
}
