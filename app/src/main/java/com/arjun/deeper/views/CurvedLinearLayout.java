package com.arjun.deeper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CurvedLinearLayout extends FrameLayout {

    private final int CURVE_CIRCLE_RADIUS = 100;

    private Path mPath;
    private Paint mPaint;

    public CurvedLinearLayout(Context context) {
        super(context);
        init();
    }
    public CurvedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CurvedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.WHITE);
        setBackgroundColor(Color.TRANSPARENT);
    } @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int containerWidth = getWidth();
        int containerHeight = getHeight();

        Point firstPoint = new Point();
        Point mFirstCurveStartPoint = new Point();
        Point mFirstCurveEndPoint = new Point();
        Point mSecondCurveStartPoint = new Point();
        Point mSecondCurveEndPoint = new Point();
        Point mFirstCurveControlPoint1 = new Point();
        Point mFirstCurveControlPoint2 = new Point();
        Point mSecondCurveControlPoint1 = new Point();
        Point mSecondCurveControlPoint2 = new Point();

        firstPoint.set(0, 0);
        mFirstCurveStartPoint.set((containerWidth / 2) - (CURVE_CIRCLE_RADIUS * 2) - (CURVE_CIRCLE_RADIUS / 3), 0);
        // the coordinates (x,y) of the end point after curve
        mFirstCurveEndPoint.set(containerWidth / 2, CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4));
        // same thing for the second curve
        mSecondCurveStartPoint = mFirstCurveEndPoint;
        mSecondCurveEndPoint.set((containerWidth / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3), 0);

        // the coordinates (x,y)  of the 1st control point on a cubic curve
        mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4), mFirstCurveStartPoint.y);
        // the coordinates (x,y)  of the 2nd control point on a cubic curve
        mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2) + CURVE_CIRCLE_RADIUS, mFirstCurveEndPoint.y);

        mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2) - CURVE_CIRCLE_RADIUS, mSecondCurveStartPoint.y);
        mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4)), mSecondCurveEndPoint.y);
        //point calculation

        mPath.reset();
        mPath.moveTo(firstPoint.x, firstPoint.y);
        mPath.lineTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y);

        mPath.cubicTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y,
                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
                mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);

        mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
                mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y,
                mSecondCurveEndPoint.x, mSecondCurveEndPoint.y);

        mPath.lineTo(containerWidth, 0);
        mPath.lineTo(containerWidth, containerHeight);
        mPath.lineTo(0, containerHeight);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}