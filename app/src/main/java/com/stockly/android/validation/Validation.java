package com.stockly.android.validation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.stockly.android.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation
 * Different validation methods defined here.
 */
public class Validation {

//    public static Boolean isValidPhone(String string) {
//        return !TextUtils.isEmpty(string) && string.length() > 8 && string.matches(
//                RegExpression.PHONE
//        );
//    }

    /**
     * isValidPhone
     * PhoneNumberUtil a library provided by google
     * <p>
     * validate phone number and return true/false if format matches.
     */
    public static boolean isValidPhone(String phoneNumber) {
        //NOTE: This should probably be a member variable.
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "US");
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e);
        }

        return false;
    }

    /**
     * isValidName
     * <p>
     * validate user's first and last name.
     */
    public static Boolean isValidName(String string) {
        return !TextUtils.isEmpty(string) && string.length() >= 1 && string.matches(
                RegExpression.NAME
        );
    }

    Boolean isValidUserName(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 4 && string.matches(
                RegExpression.USER_NAME
        );
    }

    /**
     * isValidFullName
     * <p>
     * validate full name of user.
     */
    public static Boolean isValidFullName(String string) {
        return !TextUtils.isEmpty(string) && string.trim().length() > 2 && string.matches(
                RegExpression.FULL_NAME
        );
    }

    /**
     * isValidAddress
     * <p>
     * validate address of user to a length and matching regex.
     */
    public static Boolean isValidAddress(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 4 &&
                string.matches(RegExpression.ADDRESS);
    }


    /**
     * isValidDate
     * <p>
     * validate dob date entered by user is valid or not.
     */
    public static boolean isValidDate(String dateStr) {
        boolean isDate;
        if (dateStr.length() > 9) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            try {
                sdf.parse(dateStr);
                isDate = true;
                Log.d(">>>", "isValidDate: " + sdf.getTimeZone());
            } catch (ParseException e) {
                Log.d(">>>", "isValidDate: " + e.getMessage());
                isDate = false;
            }
        } else {
            isDate = false;
        }
        return isDate;
    }

    /**
     * getAge
     * <p>
     * validate age for dob date entered by user is valid or not.
     * if user's age not matches criteria it will show error message.
     */
    public static String getAge(Context context, String dateStr) {
        String message = "";
        long selectedMilli = Date.parse(dateStr);
        Date dateOfBirth = new Date(selectedMilli);
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        if (age < 18) {
            message = context.getString(R.string.below_18_age);
            //do something
        } else if (age > 100) {
            message = context.getString(R.string.above_100_age);
        } else if (age > 17 && age < 100) {
            message = "true";
        }

        Log.d("age", ": Age in year= " + age + " " + message);
        return message;
    }

    public static boolean IsDateBetween(String dd) {
        boolean isbtw = false;
        long from = Date.parse("01/01/1991");  // From some date

        long to = Date.parse("12/12/2003");     // To Some Date

        long check = Date.parse(dd);

        int x = 0;

        if ((check - from) > 0 && (to - check) > 0) {
            x = 1;
            isbtw = true;
        }

        System.out.println("From Date is greater Than  ToDate : " + x);
        return isbtw;
    }

    public static Boolean isValidInput(String string) {
        return !TextUtils.isEmpty(string) && string.trim().length() > 1;
    }

    public static boolean isValidUrl(String urlString) {
        return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
    }

    public static Boolean isValidFb(String string) {
        boolean isValid;
        if (string.contains("facebook")) {
            Log.d("fb", "isValidFb: " + "true");
            isValid = true;
        } else {
            isValid = false;
            Log.d("fb", "isValidFb: " + "false");
        }
        return isValid;

    }

    public static Boolean isValidInsta(String string) {
        boolean isValid;
        isValid = string.contains("instagram");
        return isValid;
    }

    public static Boolean isValidTwitter(String string) {
        boolean isValid;
        isValid = string.contains("twitter");
        return isValid;
    }


    public static Boolean isValidLongInput(String string) {
//        Pattern p = Pattern.compile(RegExpression.LONG_MESSAGE, Pattern.MULTILINE);

        return !TextUtils.isEmpty(string) && string.matches(RegExpression.LONG_MESSAGE);
//                p.matcher(string).find();
    }

    Boolean isValidInputPollValue(String string) {
        return !TextUtils.isEmpty(string) && string.matches(RegExpression.POLL);
    }

    Boolean isValidMessage(String string) {

        return !TextUtils.isEmpty(string) && string.length() > 4;
    }

    Boolean isValidPin(String string) {
        try {
            return !TextUtils.isEmpty(string) && string.length() == 1 && string.length() > -1;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static Boolean isValidPassword(String password) {

        return (!TextUtils.isEmpty(password)
                && password.matches(RegExpression.PASSWORD));
    }

    Boolean isValidSubscriptionPrice(String string) {

        return !TextUtils.isEmpty(string) && string.length() >= 5;

    }

    public static Boolean isValidAmount(String value) {

        try {
            float temp = Float.parseFloat(value);
            return !TextUtils.isEmpty(value) && temp > 0;
        } catch (NumberFormatException e) {
            return false;
        }

    }
//
//    Boolean isValidPostLockDays(String days) {
//
//        try {
//            int temp = Integer.parseInt(days);
//            return temp in 5. .30
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }

//    Boolean isValidPostLockMonths(String days){
//
//
//        return try {
//            val temp = days.toInt()
//            temp in 1. .12
//        } catch (e:NumberFormatException){
//            false
//        }
//    }

    // TODO: 12/11/2020 Date of birth dd-mm-yyyy
    public static Boolean isValidDob(String dob) {
        return !TextUtils.isEmpty(dob);
    }
}
