package com.stockly.android.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;


import com.stockly.android.R;
import com.stockly.android.databinding.LayoutEditTextBinding;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.validation.Validation;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A custom EditText class with a parent Frame layout.
 * It represents same as edit text class with attributes
 * are set on initializing it and Data types are defined
 * like email,password to set behaviour of input type.
 * Different validation set to it for validating input.
 */
public class CustomEditText extends FrameLayout {
    private String mDefaultErrorMessage;
    private int mType = 1;
    private LayoutEditTextBinding mBinding;
    @Nullable
    private Observer mObserver;
    private final AtomicBoolean isInputValid = new AtomicBoolean(true);

    public CustomEditText(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public CustomTextInputEditText getEditText() {
        return mBinding.editText;
    }

    public ImageView getIcon() {
        return mBinding.iconPassword;
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomEditText,
                defStyleAttr,
                0
        );
        mDefaultErrorMessage = a.getString(R.styleable.CustomEditText_ErrorMessage);
        if (TextUtils.isEmpty(mDefaultErrorMessage)) {
            mDefaultErrorMessage = context.getString(R.string.error_enter_valid_value);
        }
        String text = a.getString(R.styleable.CustomEditText_android_text);
        String hint = a.getString(R.styleable.CustomEditText_android_hint);
        String label = a.getString(R.styleable.CustomEditText_TitleMessage);
        float textSize = a.getDimensionPixelSize(R.styleable.CustomEditText_android_textSize, 0);
        int intPutType = a.getInt(R.styleable.CustomEditText_android_inputType, InputType.TYPE_CLASS_TEXT);
        int height = a.getDimensionPixelSize(R.styleable.CustomEditText_layout_height, 0);
        int drawableStart = a.getResourceId(R.styleable.CustomEditText_android_drawableStart, 0);
        int drawableEnd = a.getResourceId(R.styleable.CustomEditText_android_drawableEnd, 0);
//        Focus Next
        int down = a.getInt(R.styleable.CustomEditText_android_nextFocusDown, 0);
        int forward = a.getInt(R.styleable.CustomEditText_android_nextFocusForward, 0);
        int right = a.getInt(R.styleable.CustomEditText_android_nextFocusRight, 0);
        int imeOptions = a.getInt(R.styleable.CustomEditText_android_imeOptions, -1);
        Drawable background = a.getDrawable(R.styleable.CustomEditText_background);
        int maxLength = a.getInt(R.styleable.CustomEditText_android_maxLength, 0);
        String digits = a.getString(R.styleable.CustomEditText_android_digits);
        boolean customIcon = a.getBoolean(R.styleable.CustomEditText_customIcon, false);


        mType = a.getInt(R.styleable.CustomEditText_type, 1);
        int textAlignment = a.getInt(R.styleable.CustomEditText_android_textAlignment, 5);
        boolean enabled = a.getBoolean(R.styleable.CustomEditText_android_enabled, true);
        boolean cursorVisibility = a.getBoolean(R.styleable.CustomEditText_android_cursorVisible, true);
        a.recycle();
        mBinding = LayoutEditTextBinding.inflate(LayoutInflater.from(context), this, false);

        if (!TextUtils.isEmpty(hint)) {
            mBinding.editText.setHint(hint);
        }
        if (!TextUtils.isEmpty(text)) {
            mBinding.editText.setText(text);
        }
        if (!TextUtils.isEmpty(label)) {
            mBinding.title.setText(label);
        } else {
            mBinding.title.setVisibility(View.INVISIBLE);
        }

        if (background != null) {
            mBinding.editText.setBackground(background);
        }

        if (textSize != 0) {
            mBinding.editText.setTextSize(textSize);
        }
        if (!TextUtils.isEmpty(digits)) {
            mBinding.editText.setKeyListener(DigitsKeyListener.getInstance(digits));
        }

        if (drawableStart != 0) {
            Log.d("ddd", "init: ");
            mBinding.editText.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0);
        }

        if (drawableEnd != 0) {
            Log.d("ddd", "init: ");
            mBinding.editText.setCompoundDrawablePadding(12);
            mBinding.editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableEnd, 0);
        }

        if (drawableStart != 0 && drawableEnd != 0) {
            Log.d("ddd", "init: ");
            mBinding.editText.setCompoundDrawablePadding(12);
            mBinding.editText.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, drawableEnd, 0);
        }

        if (mType == Type.Password) {
            mBinding.editText.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            mBinding.editText.setInputType(intPutType);
        }
        mBinding.editText.setEnabled(enabled);
        mBinding.editText.setCursorVisible(cursorVisibility);
        mBinding.editText.setTextAlignment(textAlignment);

        mBinding.errorMessage.setText(mDefaultErrorMessage);

        mBinding.editText.addTextChangedListener(new WrapperTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
//                Log.d(">>>", "onTextChanged: ");
                boolean empty = TextUtils.isEmpty(mBinding.editText.getText());
                if (!empty) {
                    checkValidation(s.toString());
                    handleInput(s.toString());

                } else {
                    mBinding.editText.enableCustomError(false);
                    mBinding.errorMessage.setVisibility(INVISIBLE);
                }

            }
        });

        if (down != 0)
            mBinding.editText.setNextFocusDownId(down);
        if (right != 0)
            mBinding.editText.setNextFocusRightId(right);
        if (forward != 0)
            mBinding.editText.setNextFocusForwardId(forward);
        if (imeOptions != -1)
            mBinding.editText.setImeOptions(imeOptions);
        if (maxLength != 0)
            mBinding.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        if (height != 0) {
            final float scale = getResources().getDisplayMetrics().density;
            int dpHeightInPx = (int) (height * scale);
            Log.d(">>>", "init: " + dpHeightInPx + "  " + height);
            mBinding.editText.setHeight(dpHeightInPx);
        }

        mBinding.editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean empty = TextUtils.isEmpty(mBinding.editText.getText());
                if (empty) {
                    handleInput(mBinding.editText.getText());
                }

                if (b || !empty) {
                    mBinding.title.setVisibility(View.VISIBLE);
                } else {
                    mBinding.title.setVisibility(View.INVISIBLE);
                }
            }
        });


        if (mType == Type.Password) {
            if (!customIcon) {
                mBinding.iconPassword.setVisibility(VISIBLE);
                mBinding.iconPassword.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean isShown = mBinding.iconPassword.isSelected();
                        mBinding.iconPassword.setSelected(!isShown);
                        if (isShown) {
                            mBinding.iconPassword.setImageResource(R.drawable.ic_visibility_off_eye);
                            mBinding.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            mBinding.iconPassword.setImageResource(R.drawable.ic_visibility_eye);
                            mBinding.editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                        }
                        mBinding.editText.setSelection(mBinding.editText.getText().length());
                    }

                });
            }
        }


        addView(mBinding.getRoot());
    }

    public void setObserver(Observer observer) {
        mObserver = observer;
    }

    private void checkValidation(CharSequence s) {
        mBinding.errorMessage.setVisibility(INVISIBLE);
        mBinding.editText.enableCustomError(false);
        boolean valid = isValid(s);
        if (mObserver != null) {
            mObserver.onObserver(valid, s);
        }
        isInputValid.set(valid);
    }

    private boolean isValid(CharSequence s) {
        boolean isValid = false;
        switch (mType) {
            case Type.Phone:
                isValid = Validation.isValidPhone(s.toString());
                return isValid;
            case Type.NAME:
                isValid = Validation.isValidName(s.toString());
                return isValid;
            case Type.FULL_NAME:
                isValid = Validation.isValidFullName(s.toString());
                return isValid;
            case Type.Text:
                isValid = Validation.isValidInput(s.toString());
                return isValid;
            case Type.LONG_TEXT:
                isValid = Validation.isValidLongInput(s.toString());
                return isValid;
            case Type.Email:
                isValid = Validation.isValidEmail(s.toString());
                return isValid;
            case Type.Password:
                isValid = Validation.isValidPassword(s.toString());
                return isValid;
            case Type.ADDRESS:
                isValid = Validation.isValidAddress(s.toString());
                return isValid;
            case Type.DOB:
                isValid = Validation.isValidDate(s.toString());
                return isValid;
            case Type.Number:
                isValid = Validation.isValidAmount(s.toString());
                return isValid;
            default:
                Log.d(">>>", "isValid: ");
                isValid = true;
                return isValid;

        }
    }

    private void handleInput(CharSequence s) {

        if (TextUtils.isEmpty(s)) {
            mBinding.errorMessage.setVisibility(View.INVISIBLE);
            mBinding.editText.enableCustomError(false);
            return;
        }

        switch (mType) {
            case Type.Phone:
                if (Validation.isValidPhone(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_invalid_phone_number);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.NAME:
                if (Validation.isValidName(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_invalid_name);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(VISIBLE);
                    mBinding.editText.enableCustomError(true);

                }
                break;
            case Type.FULL_NAME:
                if (Validation.isValidFullName(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_invalid_name);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.Text:
                if (Validation.isValidInput(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage =
                                getContext().getString(R.string.error_enter_valid_value);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.LONG_TEXT:
                if (Validation.isValidLongInput(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_enter_valid_value);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.Email:
                if (Validation.isValidEmail(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_invalid_email);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                    Log.d(">>>", "email: " + s);
                }
                break;
            case Type.Password:
                if (Validation.isValidPassword(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_invalid_password);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.ADDRESS:
                if (Validation.isValidAddress(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.invalid_address);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.Number:
                if (Validation.isValidAmount(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.error_enter_valid_value);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            case Type.DOB:
                if (Validation.isValidDate(s.toString())) {
                    mBinding.errorMessage.setVisibility(View.INVISIBLE);
                    mBinding.editText.enableCustomError(false);
                } else {
                    if (TextUtils.isEmpty(mDefaultErrorMessage)) {
                        mDefaultErrorMessage = getContext().getString(R.string.invalid_dob);
                    }
                    mBinding.errorMessage.setText(mDefaultErrorMessage);
                    mBinding.errorMessage.setVisibility(View.VISIBLE);
                    mBinding.editText.enableCustomError(true);
                }
                break;
            default:
                Log.d(">>>", "isValid: com");
                mBinding.errorMessage.setVisibility(View.INVISIBLE);
                mBinding.editText.enableCustomError(false);
                break;
        }

        boolean valid = isValid(s);
        isInputValid.set(valid);
        if (mObserver != null) {
            mObserver.onObserver(valid, s);
        }
    }


    private interface Type {

        int Text = 1;
        int Phone = 2;
        int Email = 3;
        int Password = 4;
        int NAME = 5;
        int FULL_NAME = 6;
        int DOB = 7;
        int LONG_TEXT = 8;
        int ADDRESS = 9;
        int Number = 10;

    }

    public void setError(@StringRes int error) {
        mBinding.errorMessage.setText(error);
        mBinding.errorMessage.setVisibility(View.VISIBLE);
    }

    public void setError(String error) {
        mBinding.errorMessage.setText(error);
        mBinding.errorMessage.setVisibility(View.VISIBLE);
    }

    public void showError() {
        mBinding.errorMessage.setVisibility(VISIBLE);
        mBinding.editText.enableCustomError(true);
    }

    public void setText(String text) {
        mBinding.editText.setText(text);
    }

    public void setText(CharSequence text) {
        mBinding.editText.setText(text);
    }

    public void setIcon(int text) {
        Log.d(">>>", "setIcon: ");
        mBinding.iconPassword.setImageResource(text);
    }

    Boolean isValid() {
        Editable pin = mBinding.editText.getText();
        boolean isValid = (!TextUtils.isEmpty(pin)) && (isValid(pin.toString()));
        if (!isValid)
            showError();
        return isValid;
    }


    public interface Observer {
        void onObserver(Boolean isValid, CharSequence str);
    }

    public int getDpToPix(int sdp) {
        Resources resources = getResources();
        return resources.getDimensionPixelSize(sdp);
    }
}
