package com.stockly.android.listners;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Wrapper class extends Text Watcher class to
 * minify it's overriding all functions each times and can use required one.
 */
public class WrapperTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
