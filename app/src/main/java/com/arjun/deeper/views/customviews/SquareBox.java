package com.arjun.deeper.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.arjun.deeper.R;
import com.arjun.deeper.utils.UiUtils;

import carbon.widget.FrameLayout;

public class SquareBox extends FrameLayout {

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

    /**
     * If this layout is supposed to be drawn edge to edge on the display, then we can directly set
     * height to screenwidth (in case of portrait mode) and avoid waiting for onMeasure to be called.
     * Otherwise we need to wait and then set the height.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
        getLayoutParams().width = size;
        getLayoutParams().height = size;
//        if (!fitsScreen) {
//            int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
//                getLayoutParams().width = getMeasuredHeight();
//            else
//                getLayoutParams().height = getMeasuredWidth();
//        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        TODO: Cleanup equal dimensions logic
//        if (fitsScreen) {
//            int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
//                getLayoutParams().width = UiUtils.getScreenHeight();
//            else
//                getLayoutParams().height = UiUtils.getScreenWidth();
//        }
    }
}
