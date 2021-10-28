package com.stockly.android.fragments.wallet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stockly.android.R;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.databinding.FragmentAddFundsBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.listners.DataListener;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Payment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DecimalDigitsInputFilter;
import com.stockly.android.validation.Validation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * AddFundsFragment
 * It represents a user to add funds to there account if Bank is attached.
 */
@AndroidEntryPoint
public class AddFundsFragment extends NetworkFragment implements DataListener<String> {
    private FragmentAddFundsBinding mBinding;
    private BankAccount accounts;
    private String key;
    @Inject
    BankAccountDao accountDao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            key = bundle.getString(CommonKeys.KEY_FUNDS);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                if (key != null) {
//                    if (key.equalsIgnoreCase("funds")) {
//                        replaceFragment(new TransactionsFragment());
//                    } else {
                requireActivity().finish();
//                    }
//                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            float valueToAdd = CommonUtils.getFloatValue(mBinding.edtFunds.getEditText().getText().toString());

            if (id == R.id.twenty_five) {
                valueToAdd += 25;
            } else if (id == R.id.fifty) {
                valueToAdd += 50;
            } else if (id == R.id.hundred) {
                valueToAdd += 100;
            } else if (id == R.id.five_hundred) {
                valueToAdd += 500;
            }
            mBinding.edtFunds.getEditText().setText(String.valueOf(valueToAdd));
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_funds;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentAddFundsBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);


        getBankAccounts();
        getTradingProfile();

        mBinding.edtFunds.getEditText().setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        mBinding.addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.isValidAmount(mBinding.edtFunds.getEditText().getText().toString())) {
                    if (accounts != null) {
                        addFunds(accounts.id);
                    } else {
                        Toast.makeText(requireActivity(), R.string.no_account, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBinding.twentyFive.setOnClickListener(onClickListener);
        mBinding.fifty.setOnClickListener(onClickListener);
        mBinding.hundred.setOnClickListener(onClickListener);
        mBinding.fiveHundred.setOnClickListener(onClickListener);

        // allow you remove Bank account and and new one.
        mBinding.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accounts != null) {
                    FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                    Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("changeBankDialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    DeAttachBankDialogFragment fragment = new DeAttachBankDialogFragment();
                    fragment.setDataListener(AddFundsFragment.this);
//                DialogFragment fragment = fragmentT;
                    fragment.setCancelable(false);

                    ft.addToBackStack(fragment.getClass().getName());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("account", accounts);
                    fragment.setArguments(bundle);
                    fragment.show(ft, "changeBankDialog");
                } else {
                    Toast.makeText(requireActivity(), R.string.no_account_attached, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * Send request to server for adding funds to account of user.
     *
     * @param bank_id string id.
     */
    private void addFunds(String bank_id) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().addFunds(bank_id, mBinding.edtFunds.getEditText().getText().toString()), new CallBack<Payment>() {
            @Override
            public void onSuccess(Payment payment) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), R.string.successfully_done, Toast.LENGTH_SHORT).show();

                FundsAddedFragment fragment = new FundsAddedFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("payment", payment);
                if (key != null) {
                    bundle.putString(CommonKeys.KEY_FUNDS, key);
                }
                fragment.setArguments(bundle);
                replaceFragment(fragment);
//                ActivityUtils.launchFragment(requireActivity(), FundsAddedFragment.class, bundle);
                mBinding.edtFunds.getEditText().setText("");
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * get attached banks from server.
     * if empty then hide remove bank option
     */
    public void getBankAccounts() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        requestSingle(accountDao.getBankAccount(), new CallBackSingle<BankAccount>() {
            @Override
            public void onSuccess(@NotNull BankAccount account) {
                if (account != null) {
                    updateUi(account);
                    accounts = account;
                } else {
                    hideUI();
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                hideUI();
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                super.onError(e);
            }
        });
    }

    /**
     * Call Trading Profile from server
     * which has user's account info, balance to update to UI.
     */
    public void getTradingProfile() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getTradingProfile(), new NetworkFragment.CallBack<TradingProfile>() {
            @Override
            public void onSuccess(TradingProfile accounts) {
                updateBalance(accounts);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * Update balance coming from trade profile api of user's
     *
     * @param profile instance.
     */
    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.availableAmount.setText(String.format("Available $%s", CommonUtils.round(Double.parseDouble(profile.cash), 4)));
        }
    }

    /**
     * Hide UI if no bank account found from api from server.
     */
    public void hideUI() {
        mBinding.bankName.setVisibility(View.GONE);
        mBinding.accountNo.setVisibility(View.GONE);
        mBinding.change.setVisibility(View.GONE);
        if (key != null) {
//            if (key.equalsIgnoreCase("funds")) {
//                replaceFragment(new TransactionsFragment());
//            } else {
            requireActivity().finish();
//            }
        }
    }

    /**
     * Update UI show data if Bank account is attached.
     *
     * @param account instance.
     */
    public void updateUi(BankAccount account) {
        if (account != null) {
            mBinding.bankName.setText(account.nickname);
            mBinding.accountNo.setText(String.format("Account ****%s", account.bankAccountNumber.substring(account.bankAccountNumber.length() - 4)));
        }
    }

    @Override
    public void onDataListener(String s) {
        getBankAccounts();
    }

}
