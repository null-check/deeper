package com.arjun.deeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.arjun.deeper.utils.CommonLib;

import carbon.widget.LinearLayout;

public class SquareBox extends LinearLayout {

    private boolean fitsScreen = false;

    public SquareBox(Context context) {
        super(context);
    }

    public SquareBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SquareBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SquareBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        fetchAttributes(attrs);
    }

    private void fetchAttributes(AttributeSet attrs) {
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.SquareBox);

        if (attributes != null) {
            fitsScreen = attributes.getBoolean(R.styleable.SquareBox_fits_screen, false);
            attributes.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!fitsScreen) {
            getLayoutParams().height = getMeasuredWidth();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (fitsScreen) {
            getLayoutParams().height = CommonLib.getScreenWidth();
        }
    }
}
