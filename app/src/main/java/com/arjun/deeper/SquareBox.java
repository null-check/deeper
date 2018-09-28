package com.arjun.deeper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.arjun.deeper.utils.CommonLib;

import butterknife.ButterKnife;
import carbon.widget.LinearLayout;

public class Box extends LinearLayout {

    public Box(Context context) {
        super(context);
        init(context);
    }

    public Box(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Box(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Box(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflateView(context);
    }

    private void inflateView(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rootView = inflater.inflate(R.layout.view_box, this, true);
//        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
//        getLayoutParams().height = width;
//        setMeasuredDimension(width, width);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setHeight(CommonLib.getScreenWidth());
        getLayoutParams().height = CommonLib.getScreenWidth();
    }
}
