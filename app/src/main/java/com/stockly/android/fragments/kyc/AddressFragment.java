package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentAddressBinding;
import com.stockly.android.fragments.NetworkFragment;

import com.stockly.android.models.Country;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.State;
import com.stockly.android.models.User;

import com.stockly.android.session.UserSession;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.validation.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;


/**
 * AddressFragment
 * This Fragment class represents Address info required from users.
 */
@AndroidEntryPoint
public class AddressFragment extends NetworkFragment {
    private FragmentAddressBinding mBinding;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;
    private User mUser;
    private Country country;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new DateOfBirthFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_address;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentAddressBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);

        getUser();
        Validator validator = new Validator();

        // checks user's input for apartment.
        mBinding.apartment.setOnFocusChangeListener((view1, b) -> {
            boolean empty = TextUtils.isEmpty(mBinding.apartment.getText());

            if (b || !empty) {
                mBinding.textApt.setVisibility(View.VISIBLE);
            } else {
                mBinding.textApt.setVisibility(View.INVISIBLE);
            }
        });

        // observe input changes to address field
        mBinding.address.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidInput(requireActivity(), mBinding.city) && validator.isValidZipCode(requireActivity(), mBinding.zipCode) && isValid));

//        mBinding.apartment.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidAddress(requireActivity(), mBinding.address) && validator.isValidInput(requireActivity(), mBinding.city) &&
//                validator.isValidAddress(requireActivity(), mBinding.zipCode) && isValid));
        // observe input changes to city field
        mBinding.city.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidAddress(requireActivity(), mBinding.address) && validator.isValidZipCode(requireActivity(), mBinding.zipCode) && isValid));
        // observe input changes to zipCode field
        mBinding.zipCode.setObserver((isValid, str) -> mBinding.next.setEnabled(validator.isValidAddress(requireActivity(), mBinding.address) && validator.isValidInput(requireActivity(), mBinding.city) && isValid));

        getCountries();

        mBinding.next.setOnClickListener(view12 -> {
            Validator validator1 = new Validator();
            if (validator1.isValidAddress(requireActivity(), mBinding.address) && validator1.isValidInput(requireActivity(), mBinding.city) && validator1.isValidAddress(requireActivity(), mBinding.zipCode)) {
                State state = (State) mBinding.state.getSelectedItem();
//                    City city = (City) mBinding.city.getSelectedItem();
                if (state != null) {
                    if (!state.name.equalsIgnoreCase("Choose State")) {
                        updateProfileAddress(mBinding.address.getEditText().getText().toString(), CommonUtils.getValue(mBinding.apartment.getText().toString()), state.short_code, mBinding.city.getEditText().getText().toString(), mBinding.zipCode.getEditText().getText().toString());
                    } else {
                        Toast.makeText(requireActivity(), "Please choose state", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // get state item selected
        mBinding.state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                State state = (State) mBinding.state.getSelectedItem();
//                if (country != null)
//                    getCities(country.short_code, state.short_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.next.setEnabled(false);
            }
        });


    }

    /**
     * get User for local DB.
     */
    public void getUser() {
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: ");
                mUser = user;
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
     * get Countries List from Server.
     * pass short code of US only to get states list for now.
     */
    private void getCountries() {
        enqueue(getApi().getCountries(), new NetworkFragment.CallBack<List<Country>>() {
            @Override
            public void onSuccess(List<Country> countries) {
                country = countries.get(0);
                getStates(countries.get(0).short_code);
                Log.d(">>>", "onSuccess: " + countries.get(0).name);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * get states list from server by passing
     *
     * @param countryCode
     */
    private void getStates(String countryCode) {
        enqueue(getApi().getStates(countryCode), new NetworkFragment.CallBack<List<State>>() {
            @Override
            public void onSuccess(List<State> states) {
                State state = new State();
                state.name = "Choose State";
                states.add(0, state);
                ArrayAdapter<State> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, states);
                mBinding.state.setAdapter(spinnerArrayAdapter);
                if (mUser != null) {
                    setSelectedState(mUser, states);
                }

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                return super.onError(error, isInternetIssue);
            }
        });
    }

//    // get country states
//
//    private void getCities(String countryCode, String stateCode) {
//        enqueue(getApi().getCities(countryCode, stateCode), new NetworkFragment.CallBack<List<City>>() {
//            @Override
//            public void onSuccess(List<City> cities) {
//                ArrayAdapter<City> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cities);
//                mBinding.city.setAdapter(spinnerArrayAdapter);
//                if (mUser != null) {
//                    setCity(mUser, cities);
//                }
//
//            }
//
//            @Override
//            public boolean onError(RetrofitError error, boolean isInternetIssue) {
//                return super.onError(error, isInternetIssue);
//            }
//        });
//    }

    /**
     * update user's address info to server by passing
     *
     * @param address
     * @param unitApt
     * @param state
     * @param city
     * @param zip_code
     */
    private void updateProfileAddress(String address, String unitApt, String state, String city, String zip_code) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("address", address);
        body.put("unit_apt", unitApt);
        body.put("state", state);
        body.put("city", city);
        body.put("zip_code", zip_code);
        body.put("profile_completion", "address");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.next.revertAnimation();
                replaceFragment(new CitizenshipFragment());
                Log.d(">>>", "onSuccess: " + user.address);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);

            }
        });
    }


    /**
     * get pre-selected state of user by passing
     *
     * @param user
     * @param states
     */
    public void setSelectedState(User user, List<State> states) {
        if (states != null && states.size() != 0) {
            for (int i = 0; i < states.size(); i++) {
//                Log.d("<<<", "state: " + states.get(i).short_code + "   " + user.state);
                if (user.state.equalsIgnoreCase(states.get(i).short_code)) {
                    mBinding.state.setSelection(i);
//                    Log.d("<<<", "state: " + states.get(i).name);
                }
            }
        }
    }
//
//    public void setCity(User user, List<City> cities) {
//        if (cities != null && cities.size() != 0) {
//            for (int i = 0; i < cities.size(); i++) {
//                Log.d("<<<", "City: " + cities.get(i).name + "   " + user.city);
//                if (user.city.equalsIgnoreCase(cities.get(i).name)) {
//                    mBinding.city.setSelection(i);
//                    Log.d("<<<", "City: " + cities.get(i).name);
//                }
//            }
//        }
//    }

    /**
     * Update user's info if available by passing
     *
     * @param user
     */
    public void updateUI(User user) {
        mBinding.address.getEditText().setText(user.address);
        mBinding.apartment.setText(user.unitApt);
        mBinding.city.getEditText().setText(user.city);
//        mBinding.state.getEditText().setText(user.state);
        mBinding.zipCode.getEditText().setText(user.zipCode);
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
