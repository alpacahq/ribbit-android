package com.stockly.android.fragments.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.adapter.TransactionsAdapter;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentTransactionsBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.listners.DataListener;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Payment;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * TransactionsFragment
 * It represents Transaction History of user like how much
 * purchases or transfers has done.
 */
@AndroidEntryPoint
public class TransactionsFragment extends NetworkFragment implements DataListener<String> {
    private FragmentTransactionsBinding mBinding;
    private TransactionsAdapter mAdapter;
    private BankAccount accounts;
    @Inject
    TradingProfileDao profileDao;
    @Inject
    BankAccountDao accountDao;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        handleBackPressActivity(this, MainActivity.class);
        // status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transactions;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentTransactionsBinding.bind(view);
        mBinding.toolbar.save.setVisibility(View.GONE);
        mBinding.toolbar.title.setText(R.string.transactions);

        setUpToolBar(mBinding.toolbar.toolbar, true);

        // setup adapter
        mBinding.addFund.setOnClickListener(v -> checkAccounts());
        mBinding.transactionItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new TransactionsAdapter(requireActivity(), (payment, position) -> {
//                FundsAddedFragment fragment = new FundsAddedFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("payment", payment);
            bundle.putString(CommonKeys.KEY_FUNDS, "payment");
//                fragment.setArguments(bundle);
            ActivityUtils.launchFragment(requireActivity(), FundsAddedFragment.class, bundle);
//                replaceFragment(fragment);
        });
        mBinding.transactionItems.setAdapter(mAdapter);

        // remove already attached bank account
        mBinding.change.setOnClickListener(v -> {
            if (accounts != null) {
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("changeBankDialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                DeAttachBankDialogFragment fragment = new DeAttachBankDialogFragment();
                fragment.setDataListener(TransactionsFragment.this);
//                DialogFragment fragment = fragmentT;
                fragment.setCancelable(false);

                ft.addToBackStack(fragment.getClass().getName());
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", accounts);
                fragment.setArguments(bundle);
                fragment.show(ft, "changeBankDialog");
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
                if (user.account_status.equalsIgnoreCase("APPROVED")) {
                    mBinding.addFund.setEnabled(true);
                    mBinding.change.setEnabled(true);
                    mBinding.change.setClickable(true);
                    getTransferHistory();
                } else {
                    mBinding.addFund.setEnabled(false);
                    mBinding.addFund.setTextColor(getResources().getColor(R.color.colorDots, null));
                    mBinding.change.setEnabled(false);
                    mBinding.change.setClickable(false);
                    mBinding.noRecord.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    private void getTransferHistory() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getTransfersHistory(), new NetworkFragment.CallBack<List<Payment>>() {
            @Override
            public void onSuccess(List<Payment> payments) {
                mAdapter.setData(payments, true);
                if (payments != null) {
                    if (payments.size() == 0) {
                        mBinding.transactionItems.setVisibility(View.GONE);
                        mBinding.noRecord.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.noRecord.setVisibility(View.GONE);
                        mBinding.transactionItems.setVisibility(View.VISIBLE);
                    }
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                mBinding.noRecord.setVisibility(View.VISIBLE);
                return super.onError(error, isInternetIssue);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
        getBankAccounts();
        getTradingProfile();

    }

    private void checkAccounts() {
        Bundle bundle = new Bundle();
        if (accounts != null) {
            bundle.putString(CommonKeys.KEY_FUNDS, "transaction");
            ActivityUtils.launchFragment(requireActivity(), AddFundsFragment.class, bundle);
        } else {
            bundle.putString("path", "funds");
            ActivityUtils.launchFragment(requireActivity(), BankIntroFragment.class, bundle);
        }
    }

    public void getBankAccounts() {
        requestSingle(accountDao.getBankAccount(), new CallBackSingle<BankAccount>() {
            @Override
            public void onSuccess(@NotNull BankAccount account) {

                if (account != null) {
                    showUI();
                    updateUi(account);
                    accounts = account;
                } else {
                    accounts = null;
                    hideUI();
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                hideUI();
                accounts = null;
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    public void getTradingProfile() {
        requestSingle(profileDao.getTradingProfile(), new CallBackSingle<TradingProfile>() {
            @Override
            public void onSuccess(@NotNull TradingProfile profile) {
                updateBalance(profile);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });

    }

    public void hideUI() {
        mBinding.accountNo.setVisibility(View.GONE);
        mBinding.bankName.setVisibility(View.GONE);
        mBinding.change.setEnabled(false);
    }

    public void showUI() {
        mBinding.change.setEnabled(true);
        mBinding.accountNo.setVisibility(View.VISIBLE);
        mBinding.bankName.setVisibility(View.VISIBLE);
    }

    public void updateBalance(TradingProfile profile) {
        if (profile != null) {
            mBinding.balance.setText(String.format("$%s", CommonUtils.round(Double.parseDouble(profile.cash), 4)));
        }
    }

    public void updateUi(BankAccount account) {
        if (account != null) {
            mBinding.bankName.setText(account.nickname);
            mBinding.accountNo.setText(getString(R.string.account_xxx, account.bankAccountNumber.substring(account.bankAccountNumber.length() - 4)));
        }
    }

    @Override
    public void onDataListener(String s) {
        if (s.equalsIgnoreCase("deleted")) {
            accounts = null;
            mBinding.change.setEnabled(false);
            getBankAccounts();
        }

    }
}
