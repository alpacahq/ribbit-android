package com.stockly.android.validation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.stockly.android.R;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.widgets.CustomEditText;

/**
 * Validator
 * Different validation methods defined here.
 * like DOB, address, name validation function check
 * if empty or matches regex then return's true/false.
 */
public class Validator {

    private Context mContext;

    public Validator() {
    }

    public boolean isValidName(Context mContext, CustomEditText name) {
        boolean valid = true;
        String s = name.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            name.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidName(s)) {
            name.setError(mContext.getString(R.string.error_invalid_name));
            valid = false;
        }
        return valid;
    }

    public boolean isValidFullName(Context mContext, CustomEditText name) {
        boolean valid = true;
        String s = name.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            name.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidFullName(s)) {
            name.setError(mContext.getString(R.string.error_invalid_name));
            valid = false;
        }
        return valid;
    }

    public boolean isValidInput(Context mContext, CustomEditText value) {
        boolean valid = true;
        String s = value.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            value.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidInput(s)) {
            value.setError(mContext.getString(R.string.error_enter_valid_value));
            valid = false;
        }
        return valid;
    }

    public boolean isValidEmail(Context mContext, CustomEditText email) {
        boolean valid = true;
        String s = email.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            email.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidEmail(s)) {
            email.setError(mContext.getString(R.string.error_invalid_email));
            valid = false;
        }
        return valid;
    }

    public boolean isValidPassword(Context mContext, CustomEditText password) {
        boolean valid = true;
        String s = password.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            password.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidPassword(s)) {
            password.setError(mContext.getString(R.string.error_invalid_password));
            valid = false;
        }
        return valid;
    }


    public boolean isValidPhone(Context mContext, CustomEditText phone, CustomEditText code) {
        boolean valid = true;
        String s = code.getEditText().getText().toString() + phone.getEditText().getText().toString();
        Log.d(">>>", "isValidPhone: " + s);
        if (CommonUtils.isEmpty(s)) {
            phone.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidPhone(s)) {
            phone.setError(mContext.getString(R.string.error_invalid_phone_number));
            valid = false;
        }
        return valid;
    }

    public boolean isPhoneNumberValid(String phoneNumber, String countryCode) {
        //NOTE: This should probably be a member variable.
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return false;
    }

    public boolean isValidDOB(Context mContext, CustomEditText dob) {
        boolean valid = true;
        String s = dob.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            dob.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidDate(s)) {
            dob.setError(mContext.getString(R.string.invalid_dob));
            valid = false;
        }
        return valid;
    }

    public boolean isValidDOBDate(Context mContext, EditText dob) {
        boolean valid = true;
        String s = dob.getText().toString();
        if (CommonUtils.isEmpty(s)) {
            dob.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidDate(s)) {
            dob.setError(mContext.getString(R.string.invalid_dob));
            valid = false;
        }
        return valid;
    }

    public boolean isValidAddress(Context mContext, CustomEditText add) {
        boolean valid = true;
        String s = add.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            add.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidAddress(s)) {
            add.setError(mContext.getString(R.string.invalid_address));
            valid = false;
        }
        return valid;
    }

    public boolean isValidZipCode(Context mContext, CustomEditText add) {
        boolean valid = true;
        String s = add.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            add.setError(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (!Validation.isValidAddress(s)) {
            add.setError("Invalid Zip Code");
            valid = false;
        }
        return valid;
    }

    public boolean isValidFbUrl(Context mContext, CustomEditText add) {
        boolean valid = true;
        String s = add.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            valid = true;
        } else if (!Validation.isValidUrl(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        } else if (!Validation.isValidFb(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        }
        return valid;
    }

    public boolean isValidInstaUrl(Context mContext, CustomEditText add) {
        boolean valid = true;
        String s = add.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            valid = true;
        } else if (!Validation.isValidUrl(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        } else if (!Validation.isValidInsta(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        }
        return valid;
    }

    public boolean isValidTwitterUrl(Context mContext, CustomEditText add) {
        boolean valid = true;
        String s = add.getEditText().getText().toString();
        if (CommonUtils.isEmpty(s)) {
            valid = true;
        } else if (!Validation.isValidUrl(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        } else if (!Validation.isValidTwitter(s)) {
            add.setError(mContext.getString(R.string.invalid_url));
            valid = false;
        }
        return valid;
    }

    public boolean isValidSSN2(Context mContext, EditText ssn, TextView textView) {
        boolean valid = true;
        String s = ssn.getText().toString();
        if (CommonUtils.isEmpty(s)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (s.length() < 2) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_invalid_ssn));
            valid = false;
        } else {
            textView.setVisibility(View.GONE);
        }

        return valid;
    }

    public boolean isValidSSN3(Context mContext, EditText ssn, TextView textView) {
        boolean valid = true;
        String s = ssn.getText().toString();
        if (CommonUtils.isEmpty(s)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (s.length() < 3) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_invalid_ssn));
            valid = false;
        } else {
            textView.setVisibility(View.GONE);
        }

        return valid;
    }

    public boolean isValidSSN4(Context mContext, EditText ssn, TextView textView) {
        boolean valid = true;
        String s = ssn.getText().toString();
        if (CommonUtils.isEmpty(s)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_non_empty_field));
            valid = false;
        } else if (s.length() < 4) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.error_invalid_ssn));
            valid = false;
        } else {
            textView.setVisibility(View.GONE);
        }

        return valid;
    }

//    Boolean validate() {
//
//
//        boolean allValid = true;
//        if (firstName != null) {
//            if (CommonUtils.isEmpty(firstName.toString())) {
//                firstName.setError(mContext.getString(R.string.invalid_first_name));
//                allValid = false;
//            } else if (!Validation.isValidName(CommonUtils.getValue(firstName))) {
//                firstName !!.error = mContext.getString(R.string.error_invalid_value)
//                allValid = false
//            }
//        }
//
//        if (lastName != null) {
//            if (CommonUtils.isEmpty(lastName !!)){
//                lastName !!.error = mContext.getString(R.string.error_last_name_required)
//                allValid = false
//            } else if (!Validation.isValidName(
//                    CommonUtils.getValue(
//                            lastName !!
//                    )
//                )
//            ){
//                lastName !!.error = mContext.getString(R.string.error_invalid_value)
//                allValid = false
//            }
//        }
//
//        if (phone != null) {
//            if (CommonUtils.isEmpty(phone !!)){
//                phone !!.error = mContext.getString(R.string.error_phone_number_required)
//                allValid = false
//            } else if (!Validation.isValidPhone(
//                    CommonUtils.getValue(
//                            phone !!
//                    )
//                )
//            ){
//                phone !!.error = mContext.getString(R.string.error_invalid_phone_number)
//                allValid = false
//            }
//        }
//
//        if (email != null) {
//            if (CommonUtils.isEmpty(email !!)){
//                email !!.error = mContext.getString(R.string.error_email_required)
//                allValid = false
//            } else if (!Validation.isValidPhone(
//                    CommonUtils.getValue(
//                            email !!
//                    )
//                )
//            ){
//                email !!.error = mContext.getString(R.string.error_invalid_email)
//                allValid = false
//            }
//        }
//
//        if (deliverAddress != null) {
//            if (CommonUtils.isEmpty(deliverAddress !!)){
//                deliverAddress !!.error =
//                        mContext.getString(R.string.error_address_required)
//                allValid = false
//            } else if (!Validation.isValidInput(
//                    CommonUtils.getValue(
//                            deliverAddress !!
//                    )
//                )
//            ){
//                deliverAddress !!.error = mContext.getString(R.string.error_invalid_address)
//                allValid = false
//            }
//        }
//        return allValid
//    }
}
