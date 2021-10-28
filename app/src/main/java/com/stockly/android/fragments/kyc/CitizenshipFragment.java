package com.stockly.android.fragments.kyc;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentCitizenshipBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.models.Country;
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
 * CitizenshipFragment
 * This Fragment class represents Country required to be selected by user.
 * For now its US only recommended.
 */
@AndroidEntryPoint
public class CitizenshipFragment extends NetworkFragment {
    private FragmentCitizenshipBinding mBinding;
    @Inject
    UserDao userDao;
    User mUser;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite,null));
        handleBackPress(this, new AddressFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_citizenship;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentCitizenshipBinding.bind(view);
        mBinding.toolbar.toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.colorPrimary));
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();

        // countries
        getCountries();

        mBinding.country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) mBinding.country.getSelectedItem();
//                Log.d(">>>", "onItemSelected: " + country.short_code);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.next.setEnabled(false);
            }
        });


        mBinding.next.setOnClickListener(view1 -> {
            Country country = (Country) mBinding.country.getSelectedItem();
            if (!country.name.equalsIgnoreCase("Choose Country")) {
                updateCitizenship(country);
            } else {
                Toast.makeText(requireActivity(), "Please choose country", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * fetch countries list from server.
     */
    private void getCountries() {
        enqueue(getApi().getCountries(), new NetworkFragment.CallBack<List<Country>>() {
            @Override
            public void onSuccess(List<Country> countries) {

                Country country = new Country();
                country.name = "Choose Country";
                countries.add(0, country);
                ArrayAdapter<Country> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countries);
                mBinding.country.setAdapter(spinnerArrayAdapter);

                if (mUser != null) {
                    setSelectedCountry(mUser, countries);
                }
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * get User from local DB.
     */
    public void getUser() {
        // user DB
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: ");
                mUser = user;
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * update selected citizenship of user.
     *
     * @param country
     */
    private void updateCitizenship(Country country) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("country", country.name);
        body.put("country_code", country.short_code);
        body.put("profile_completion", "citizenship");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                VerifyIdentityFragment fragment = new VerifyIdentityFragment();
                Bundle bundle = new Bundle();
                bundle.putString("taxIdType", country.short_code);
                fragment.setArguments(bundle);
                replaceFragment(fragment);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }


    /**
     * set pre selected country of user's.
     *
     * @param user
     * @param countries
     */
    public void setSelectedCountry(User user, List<Country> countries) {
        for (int i = 0; i < countries.size(); i++) {
            if (user.country.equalsIgnoreCase(countries.get(i).name)) {
                mBinding.country.setSelection(i);
                break;
//                Log.d("<<<", "setCountry2: " + countries.get(i).name);
            }
        }
    }


}
