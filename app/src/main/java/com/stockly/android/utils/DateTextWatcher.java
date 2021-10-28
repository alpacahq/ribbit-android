package com.stockly.android.utils;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Locale;

/**
 * A utility class that provides masking for
 * Date input in edit text on its  text change function
 * and return formatted date.
 */
public class DateTextWatcher implements TextWatcher {

    public static final int MAX_FORMAT_LENGTH = 8;
    public static final int MIN_FORMAT_LENGTH = 3;

    private String updatedText;
    private boolean editing;


    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (text.toString().equals(updatedText) || editing) return;

        String digitsOnly = text.toString().replaceAll("\\D", "");
        int digitLen = digitsOnly.length();

        if (digitLen < MIN_FORMAT_LENGTH || digitLen > MAX_FORMAT_LENGTH) {
            updatedText = digitsOnly;
            return;
        }

        if (digitLen <= 4) {
            String month = digitsOnly.substring(0, 2);
            String day = digitsOnly.substring(2);

            updatedText = String.format(Locale.US, "%s/%s", month, day);
        } else {
            String month = digitsOnly.substring(0, 2);
            String day = digitsOnly.substring(2, 4);
            String year = digitsOnly.substring(4);

            updatedText = String.format(Locale.US, "%s/%s/%s", month, day, year);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editing) return;

        editing = true;

        editable.clear();
        editable.insert(0, updatedText);

        editing = false;
    }
}