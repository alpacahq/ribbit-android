package com.stockly.android.fragments.kyc;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.stockly.android.R;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentDateOfBirthBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.DateTextWatcher;
import com.stockly.android.utils.DateUtilz;
import com.stockly.android.validation.Validation;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * DateOfBirthFragment
 * This Fragment class represents date of birth required from user.
 * Restricting user to minimum and maximum age.
 */
@AndroidEntryPoint
public class DateOfBirthFragment extends NetworkFragment {
    private FragmentDateOfBirthBinding mBinding;
    private String selectedDate;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPress(this, new PhoneNumberFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_date_of_birth;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentDateOfBirthBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);


        getUser();

        mBinding.dob.setBackground(null);

        mBinding.dob.requestFocus();
        mBinding.dob.addTextChangedListener(new DateTextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                super.onTextChanged(text, start, before, count);
                // check input date is valid
                if (Validation.isValidDate(text.toString())) {
                    // check entered dob is within age limits
                    if (Validation.getAge(requireActivity(), text.toString()).equalsIgnoreCase("true")) {
                        mBinding.error.setVisibility(View.GONE);
                        mBinding.next.setEnabled(true);
                    } else {
                        mBinding.next.setEnabled(false);
                        mBinding.error.setText(Validation.getAge(requireActivity(), text.toString()));
                        mBinding.error.setVisibility(View.VISIBLE);
                    }
                } else {
                    mBinding.next.setEnabled(false);
                    mBinding.error.setText(R.string.invalid_date);
                    mBinding.error.setVisibility(View.VISIBLE);
                }
            }
        });


        mBinding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityUtils.launchFragment(requireActivity(), AddressFragment.class);
                selectedDate = DateUtilz.formatDateServer(DateUtilz.parseDobDate(mBinding.dob.getText().toString()));
                if (selectedDate != null) {
                    updateProfileDOB(selectedDate);
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mBinding.dobDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(requireActivity(), "clicked", Toast.LENGTH_SHORT).show();
////                dateDialog();
//                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
//            }
//        });

    }

    /**
     * update date of birth of user by passing
     *
     * @param dob
     */
    private void updateProfileDOB(String dob) {
        mBinding.next.startAnimation();
        HashMap<String, Object> body = new HashMap<>();
        body.put("dob", dob);
        body.put("profile_completion", "dob");
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(">>>", "onSuccess: " + user.dob);
                updateUser(user);
                mBinding.next.revertAnimation();
//                ActivityUtils.launchFragment(requireActivity(), AddressFragment.class);
                replaceFragment(new AddressFragment());
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.next.revertAnimation();
                return super.onError(error, isInternetIssue);
            }
        });
    }

    /**
     * get User from local DB.
     */
    public void getUser() {
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
//                Log.d(">>>", "onSuccess: ");
                updateUI(user);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }


//    public void dateDialog() {
//
//        MaterialDatePicker.Builder dateBuilder = MaterialDatePicker.Builder.datePicker();
//        dateBuilder.setTheme(R.style.Widget_AppTheme_MaterialDatePicker);
//        dateBuilder.setTitleText("Select Date Of Birth");
//
//        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
//
//        //start
//        Calendar calendarStart = Calendar.getInstance(Locale.getDefault());
//        calendarStart.setTimeZone(TimeZone.getTimeZone("UTC"));
//        calendarStart.set(Calendar.DAY_OF_MONTH, 01);
//        calendarStart.set(Calendar.MONTH, Calendar.JANUARY);
//        calendarStart.set(Calendar.YEAR, 1991);
//        long start = calendarStart.getTimeInMillis();
//        constraints.setStart(start);
//        constraints.setValidator(DateValidatorPointForward.from(start));
//        //end
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        calendar.set(Calendar.DAY_OF_MONTH, 31);
//        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
//        calendar.set(Calendar.YEAR, 2003);
//        long end = calendar.getTimeInMillis();
//        constraints.setEnd(end);
//        constraints.setValidator(DateValidatorPointBackward.before(end));
//
//        if (chekMonth != null) {
//            Log.d(">>>", "dateDialog: " + chekDay + chekMonth + chekYear);
//
//            Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
//            calendar1.setTimeZone(TimeZone.getTimeZone("UTC"));
//            calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(chekDay));
//            calendar1.set(Calendar.MONTH, Integer.parseInt(chekMonth) - 1);
//            calendar1.set(Calendar.YEAR, Integer.parseInt(chekYear));
//            constraints.setOpenAt(calendar1.getTimeInMillis());
//
//            dateBuilder.setSelection(calendar1.getTimeInMillis());
//        }
//
//        dateBuilder.setCalendarConstraints(constraints.build());
//
//        materialDatePicker = dateBuilder.build();
//
//        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//            @Override
//            public void onPositiveButtonClick(Object selection) {
//                try {
//                    Log.d(">>>", "header Date: " + materialDatePicker.getHeaderText());
//                    selectedDate = DateUtilz.formatDateServer(DateUtilz.parseDobDate(materialDatePicker.getHeaderText()));
//                    Log.d(">>>", "sel Date: " + selectedDate);
//                    String date = DateUtilz.formatDateDob(DateUtilz.parseDobDate(materialDatePicker.getHeaderText()));
//                    String[] splitStrings = date.split("\\s+");
//                    String day = splitStrings[0];
//                    chekDay = day;
//                    mBinding.day1.getEditText().setText(day.substring(0, 1));
//                    mBinding.day2.getEditText().setText(day.substring(1, day.length()));
//                    String month = splitStrings[1];
//                    chekMonth = month;
//                    mBinding.month1.getEditText().setText(month.substring(0, 1));
//                    mBinding.month2.getEditText().setText(month.substring(1, month.length()));
//                    String year = splitStrings[2];
//                    chekYear = year;
//
//                    mBinding.year1.getEditText().setText(year.substring(2, 3));
//                    mBinding.year2.getEditText().setText(year.substring(3, year.length()));
//                    Validator validator = new Validator();
//                    validator.isValidDOB(requireActivity(), mBinding.day1, mBinding.errorDob);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }

    /**
     * Update UI with user's info from local DB.
     *
     * @param user
     */
    public void updateUI(User user) {
        if (!TextUtils.isEmpty(user.dob)) {
            try {
                String date = DateUtilz.formatDateDob(DateUtilz.parseServerDate(user.dob));
                String[] splitStrings = date.split("\\s+");
                String month = splitStrings[0];
                String day = splitStrings[1];
                String year = splitStrings[2];
//                mBinding.year.setText(year);
//                mBinding.day.setText(day);
                mBinding.dob.setText(month + day + year);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
