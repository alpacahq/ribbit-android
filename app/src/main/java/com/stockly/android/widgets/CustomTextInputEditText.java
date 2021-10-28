package com.stockly.android.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.stockly.android.R;

import java.util.ArrayList;


/**
 * Custom input edit text with custom error attribute
 * and drawable background etc.
 */
public class CustomTextInputEditText extends TextInputEditText {
    private final static int[] STATE_ERROR = {R.attr.state_error};
    private boolean mIsError = false;


    public CustomTextInputEditText(Context context) {
        super(context);
        init(context);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        addTextChangedListener(new TextWatcherWrapper() {
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(charSequence.length()==0){
//                    setTypeface(FontUtils.getLightFont(context));
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
//                }else{
//                    setTypeface(FontUtils.getMediumFont(context));
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
//                }
//            }
//        });
    }

    @Override
    public void setError(CharSequence error) {
        mIsError = error != null;
        super.setError(error);
        refreshDrawableState();
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        mIsError = error != null;
        super.setError(error, icon);
        refreshDrawableState();
    }


    public void enableCustomError(Boolean state) {
        mIsError = state;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsError) {
            mergeDrawableStates(drawableState, STATE_ERROR);
        }
        return drawableState;
    }

}
