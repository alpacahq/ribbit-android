package com.stockly.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.models.Positions;
import com.stockly.android.validation.RegExpression;
import com.stockly.android.widgets.CustomEditText;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;

/**
 * Common Utils class with different static function that
 * can be used in any other classes like to launch activity/fragment etc.
 */
public class CommonUtils implements RegExpression {

    private static final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz-";

    public static boolean isEmpty(@Nullable String string) {
        return string == null || TextUtils.isEmpty(string);
    }

    public static boolean isValidInput(String string) {
        return !TextUtils.isEmpty(string) && string.length() >= 1;
    }

    public static boolean isValidPhone(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 8;
    }

    public static boolean isValidPassword(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 1 && string.matches(PASSWORD);
    }

    public static boolean isValidName(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 1 && string.matches(NAME);
    }

    public static boolean isValidFullName(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 5 && string.matches(FULL_NAME);
    }

    public static boolean isValidNumber(String string) {
        return !TextUtils.isEmpty(string) && string.length() > 0 && string.matches(NUMBER);
    }


    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @NonNull
    public static String getValue(@NonNull TextInputLayout inputLayout) {
        if (inputLayout.getEditText() == null || inputLayout.getEditText().getText() == null) {
            return "";
        }
        return inputLayout.getEditText().getText().toString();

    }

    @NonNull
    public static String getValue(@NonNull AppCompatAutoCompleteTextView inputLayout) {
        if (inputLayout.getText() == null || inputLayout.getText() == null) {
            return "";
        }
        return inputLayout.getText().toString();

    }

    @NonNull
    public static String getValue(@Nullable Editable inputLayout) {
        if (inputLayout == null || inputLayout.length() == 0) {
            return "";
        }
        return inputLayout.toString();

    }

    public static float getFloatValue(@Nullable Editable inputLayout) {
        if (inputLayout == null || inputLayout.length() == 0) {
            return 0;
        }
        try {
            String value = inputLayout.toString();
            value = value.replace(",", ".");
            return Float.valueOf(value);
        } catch (Exception e) {
            return 0;
        }

    }

    public static float getFloatValue(@Nullable String inputLayout) {
        if (inputLayout == null || inputLayout.length() == 0) {
            return 0;
        }
        try {
            inputLayout = inputLayout.replace(",", ".");
            return Float.valueOf(inputLayout);
        } catch (Exception e) {
            return 0;
        }

    }

    @NonNull
    public static String getValue(@NonNull String string) {
        if (TextUtils.isEmpty(string) || string == null) {
            return "";
        }
        return string;

    }


    @NonNull
    public static List<String> getListValue(@NonNull List<String> list) {
        if (list == null && list.isEmpty()) {
            return Collections.singletonList("");
        }
        return list;

    }

    public static boolean isValidFloat(Editable charSequence) {
        if (charSequence == null || charSequence.length() == 0) return false;
        try {
            Float.parseFloat(charSequence.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidFloat(String charSequence) {
        try {
            Float.parseFloat(charSequence);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidLong(String charSequence) {
        try {
            Long.parseLong(charSequence);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getRandomFourDigit() {
        Random rand = new Random();
        return rand.nextInt((9999 - 1000) + 1) + 1000;
    }

    public static int getRandomSixDigit() {
        Random rand = new Random();
        return rand.nextInt((999999 - 100000) + 1) + 100000;
    }

    public static long getUniqueID() {
        int random = getRandomSixDigit();
        return Long.parseLong(System.currentTimeMillis() + "" + random);
    }

    public static String getRandomString() {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getIntOrFloat(float value) {
        if (Math.round(value) == value) {
            return String.format(Locale.getDefault(), "%d", (int) value);
        }
        return String.format(Locale.getDefault(), "%2f", value);
    }

    /**
     * Round value to decimal places and return.
     *
     * @param value  to be converted.
     * @param places to number of decimal places.
     * @return rounded value.
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static InputFilter[] getFilter() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,4}(\\.(\\d{1,4}(\\.(\\d{1,4}(\\.(\\d{1,4})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (String split : splits) {
                            if (Integer.parseInt(split) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        return filters;
    }

    /**
     * This function return amount in the format of K,M,B in case thousand, million etc
     *
     * @param numValue price to converted.
     * @return amount.
     */
    public static String amountConversion(long numValue) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    /**
     * Calculates price of stock asset based of quantity
     *
     * @param value is quantity.
     */
    public static void calculatePrice(String value, Positions asset, EditText edtPrice) {
        if (value.length() > 0) {
            double qty = Double.parseDouble(value);
            double price = 0;
            if (asset.marketValue != null) {
                price = Double.parseDouble(asset.marketValue);
            } else if (asset.ticker != null) {
                price = Double.parseDouble(String.valueOf(asset.ticker.latestTrade.p));
            }
            double total = qty * price;
            edtPrice.setText(String.format("%s", CommonUtils.round(total, 2)));
        } else {
            edtPrice.setText("0");
        }
    }

    /**
     * Calculates Quantity on the basis of amount of stock Asset.
     *
     * @param value price.
     */
    public static void calculateQuantity(String value, Positions asset, EditText edtQty) {
        if (value.length() > 0) {
            double totalPrice = Double.parseDouble(value);
            double price = 0;
            if (asset.marketValue != null) {
                price = Double.parseDouble(asset.marketValue);
            } else if (asset.ticker != null) {
                price = Double.parseDouble(String.valueOf(asset.ticker.latestTrade.p));
            }

            double qty = totalPrice / price;

            edtQty.setText(String.format("%s", CommonUtils.round(qty, 2)));
        } else {
            edtQty.setText("0");
        }
    }

    /**
     * Format Date and return it n milli seconds
     *
     * @param givenDateString provided date
     * @param format          which format date to.
     * @return time in millis.
     */
    public static long getDateInMilliSeconds(String givenDateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (in != null) {
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

}
