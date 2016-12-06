package com.junova.customprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by junova on 2016/12/6 0006.
 */

public class RendarView extends View {
    private int count;
    //  private Map<String, Double> keyAndValues;//标题以及所对应的值
    private float mRadius;//半径
    private float angle;
    private float degAngle;
    private int mTextColor;
    private int mCoverColor;
    private int mPointColor;
    private int mLineColor;
    private float mTextSize;
    private String[] tilte;
    private Double[] values;

    private final float defaultTextSize;
    private final float defaultRadius;
    private final int defaultTextColor = Color.rgb(255, 0, 0);
    private final int defaultLineColor = Color.rgb(255, 48, 48);
    private final int defaultPointColor = Color.rgb(31, 219, 3);
    private final int defaultCoverColor = Color.rgb(13, 224, 112);

    private Paint linePainter;//线条画笔
    private Paint pointPainter;//点位画笔
    private Paint textPainter;//文字画笔
    private Paint coverPainter;//覆盖区域画笔

    private Rect rect;

    public RendarView(Context context) {
        this(context, null);
    }

    public RendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultTextSize = sp2px(20.0f);

        defaultRadius = dp2px(100.0f);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.randarChart, defStyleAttr, 0);
        mCoverColor = typedArray.getColor(R.styleable.randarChart_cover_color, defaultCoverColor);
        mLineColor = typedArray.getColor(R.styleable.randarChart_line_color, defaultLineColor);
        mPointColor = typedArray.getColor(R.styleable.randarChart_point_color, defaultPointColor);
        mTextColor = typedArray.getColor(R.styleable.randarChart_text_color, defaultTextColor);
        mTextSize = typedArray.getDimension(R.styleable.randarChart_text_size, defaultTextSize);
        mRadius = typedArray.getDimension(R.styleable.randarChart_radius, defaultRadius);

        initPainters();
        angle = (float) (Math.PI * 2 / count);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(measure(true, widthMeasureSpec), measure(false, heightMeasureSpec));
    }

    private int measure(boolean isWidth, int measureSpec) {

        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingBottom() + getPaddingTop();
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = (int) (isWidth ? 2 * mRadius + mTextSize * 2 + padding : mRadius * Math.sin(angle) * 2 + mTextSize * 2 + padding);
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.GRAY);
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float gap = mRadius / (count - 1);

        Path path = new Path();
        com.orhanobut.logger.Logger.d("count " + count);
        //画多边形
        for (int i = 1; i < count; i++) {
            path.reset();

            float currentRadius = mRadius - gap * (i - 1);
            com.orhanobut.logger.Logger.d("间隔 " + gap + "当前半径 " + currentRadius);
            for (int j = 0; j < count; j++) {

                if (j == 0) {
                    path.moveTo(centerX + currentRadius, centerY);
                } else {
                    float x = (float) (centerX + Math.cos(angle * j) * currentRadius);
                    float y = (float) (centerY + Math.sin(angle * j) * currentRadius);

                    path.lineTo(x, y);
                }
            }

            path.close();
            canvas.drawPath(path, linePainter);

        }
        //画对角线线
        if (count % 2 == 0) {
            for (int i = 0; i < count / 2; i++) {

                if (i == 0) {
                    float x1 = centerX + mRadius;
                    float y1 = centerY;
                    float x2 = centerX - mRadius;
                    float y2 = centerY;
                    canvas.drawLine(x1, y1, x2, y2, linePainter);
                } else {
                    float x1 = (float) (centerX + Math.cos(angle * i) * mRadius);
                    float y1 = (float) (centerY + Math.sin(angle * i) * mRadius);
                    float x2 = (float) (centerX + Math.cos(angle * (count / 2 + i)) * mRadius);
                    float y2 = (float) (centerY + Math.sin(angle * (count / 2 + i)) * mRadius);
                    canvas.drawLine(x1, y1, x2, y2, linePainter);
                }

            }
        }
        //画point


        Path pointPath = new Path();
        for (int i = 0; i < tilte.length; i++) {

            float x1;
            float y1;
            Double value = values[i];
            if (i == 0) {
                x1 = (float) (centerX + mRadius / 100 * value);
                y1 = centerY;
                pointPath.moveTo(x1, y1);

            } else {
                x1 = (float) (centerX + Math.cos(angle * i) * mRadius / 100 * value);
                y1 = (float) (centerY + Math.sin(angle * i) * mRadius / 100 * value);
                Logger.d(x1 + "   " + y1);
                pointPath.lineTo(x1, y1);
            }
            canvas.drawCircle(x1, y1, 5, pointPainter);
            //  pointPath.close();


        }
        canvas.drawPath(pointPath, pointPainter);

    }

    public void setTilte(String[] tilte) {
        this.tilte = tilte;
        count = tilte.length;
        angle = (float) (Math.PI * 2 / count);
        invalidate();
    }

    public void setValues(Double[] values) {
        this.values = values;
        invalidate();
    }


    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }


    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        textPainter.setTextSize(mTextSize);
        invalidate();
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
        linePainter.setColor(mLineColor);
        invalidate();
    }

    public int getPointColor() {
        return mPointColor;
    }

    public void setPointColor(int mPointColor) {
        this.mPointColor = mPointColor;
        pointPainter.setColor(mPointColor);
        invalidate();
    }

    public int getCoverColor() {
        return mCoverColor;
    }

    public void setCoverColor(int mCoverColor) {
        this.mCoverColor = mCoverColor;

    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    private void initPainters() {
        textPainter = new Paint();
        textPainter.setAntiAlias(true);
        textPainter.setColor(mTextColor);

        linePainter = new Paint();
        linePainter.setAntiAlias(true);
        linePainter.setColor(Color.RED);
        linePainter.setStrokeWidth(5);
        linePainter.setStyle(Paint.Style.STROKE);

        pointPainter = new Paint();
        pointPainter.setAntiAlias(true);
        pointPainter.setColor(mPointColor);

        coverPainter = new Paint();
        coverPainter.setAntiAlias(true);
        coverPainter.setColor(mCoverColor);


    }


    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
