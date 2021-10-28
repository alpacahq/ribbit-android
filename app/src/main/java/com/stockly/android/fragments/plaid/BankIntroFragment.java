package com.stockly.android.fragments.plaid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.plaid.link.OpenPlaidLink;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidActivityResultContract;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkAccount;
import com.plaid.link.result.LinkAccountSubtype;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkSuccess;
import com.plaid.link.result.LinkSuccessMetadata;
import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.databinding.FragmentBankIntroBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.fragments.wallet.AddFundsFragment;
import com.stockly.android.fragments.wallet.TransactionsFragment;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.LinkToken;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.utils.ActivityUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.OptIn;
import kotlin.Unit;
import kotlin.UseExperimental;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * BankIntroFragment
 * This Fragment represents link to open up plaid account for Bank account.
 */

@AndroidEntryPoint
public class BankIntroFragment extends NetworkFragment {
    private FragmentBankIntroBinding mBinding;
    private String token;
    private String path = "";
    @Inject
    BankAccountDao accountDao;

    @SuppressLint("Experimental")
    private final ActivityResultLauncher<LinkTokenConfiguration> openPlaidLink = registerForActivityResult(new OpenPlaidLink(), linkResult -> {
        if (linkResult instanceof LinkSuccess) {
            String tokenString = ((LinkSuccess) linkResult).getPublicToken();
            Log.d(">>>", "public Token: " + tokenString);
            String accountId = "";
//            String accountName = "";
            LinkSuccessMetadata metadata = ((LinkSuccess) linkResult).getMetadata();
            for (LinkAccount account : metadata.getAccounts()) {
                accountId = account.getId();
//                accountName = account.getName();
//                String accountMask = account.getMask();
//                LinkAccountSubtype accountSubtype = account.getSubtype();
            }
//            String institutionId = metadata.getInstitution().getId();
//            String institutionName = metadata.getInstitution().getName();
            setAccessToken(tokenString, accountId);
        } else if (linkResult instanceof LinkExit) {
            LinkExit linkExit = (LinkExit) linkResult;
            mBinding.next.revertAnimation();
            if (linkExit.getError() != null) {
                Log.d(">>>", "Error: " + linkExit.getError());
            } else {
                Log.d(">>>", "Error: Unknown");
            }
        } else {
            throw new RuntimeException("Got unexpected result:");
        }
    });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // check path and decides next movement
                if (path.equalsIgnoreCase("")) {
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else if (path.equalsIgnoreCase("funds")) {
                    requireActivity().finish();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bank_intro;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentBankIntroBinding.bind(view);
        // status bar color.
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#D170FF"));

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString("path");
        }

        SpannableString s = new SpannableString(getString(R.string.plaid_text));
        s.setSpan(new StyleSpan(Typeface.BOLD), 11, 17, 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 93, 99, 0);
        mBinding.plaidText.setText(s);

        getLinkToken();

        /**
         * Registration
         * Attach bank account
         * or
         * skip to Home page.
         */
        mBinding.next.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(token) && token != null) {
                mBinding.next.startAnimation();
                setOptionalEventListener();
                openLink();
            } else {
                Toast.makeText(requireActivity(), "Token Not Valid! Try Again", Toast.LENGTH_LONG).show();
            }
        });

        mBinding.skip.setOnClickListener(v -> {
            if (path.equalsIgnoreCase("")) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            } else if (path.equalsIgnoreCase("funds")) {
                requireActivity().finish();
            }
        });

    }

    /**
     * Initiate plaid sdk
     */
    private void openLink() {
        openPlaidLink.launch(new LinkTokenConfiguration.Builder()
                .token(token)
                .build());
    }

    /**
     * get token from server that needed to pass to initiate plaid sdk
     */
    private void getLinkToken() {
        enqueue(getApi().getLinkToken(), new NetworkFragment.CallBack<LinkToken>() {
            @Override
            public void onSuccess(LinkToken linkToken) {
                token = linkToken.linkToken;
                Log.d(">>>", "onSuccess: " + token);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * Plaid event listener
     */
    private void setOptionalEventListener() {
        Plaid.setLinkEventListener(linkEvent -> {
            Log.d(">>>", "event: " + linkEvent.toString());
            return Unit.INSTANCE;
        });
    }

    /**
     * Update server after completing plaid integration and bank account attachment.
     *
     * @param public_token string.
     * @param account_id   string.
     */
    private void setAccessToken(String public_token, String account_id) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("public_token", public_token);
        body.put("account_id", account_id);
        enqueue(getApi().setAccessToken(body), new CallBack<BankAccount>() {
            @Override
            public void onSuccess(BankAccount account) {
                mBinding.next.revertAnimation();
                // delete table from local DB before update.
                accountDao.deleteAll();
                // update account with latest changes.
                accountDao.saveAccount(account).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                            }

                            @Override
                            public void onComplete() {
//                                Toast.makeText(requireActivity(), "saved Acc", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.d(">>>", "onError: " + e.getMessage());
                            }
                        });

                // Move to next Path.
                AccountLinkFragment fragment = new AccountLinkFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
//                ActivityUtils.launchFragment(requireActivity(), AccountLinkFragment.class);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }

}
