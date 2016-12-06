package com.junova.customprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.logging.Logger;

/**
 * Created by junova on 2016/12/5 0005.
 */

public class NumberProgressBar extends View {
    private Context mContext;
    private int mMax = 100;// max progress ,default is 100
    private int mProgress = 0;// current progress , default is 0
    private int mReachedBarColor;//progress area bar color
    private int mUnreachedBarColor;//bar unreached area color
    private int mTextColor;// text color
    private float mTextSize;//text size
    private float mReachedBarHeight;//height of reached area
    private float mUnreachedBarHeight;//height of unreached area;

    //default values
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_reached_color = Color.rgb(66, 145, 241);
    private final int default_unreached_color = Color.rgb(204, 204, 204);
    private final float default_progress_text_offset;
    private final float default_text_size;
    private final float default_reached_bar_height;
    private final float default_unreached_bar_height;
    //about text
    private float mDrawTextWidth;//the width of text to be drawn
    private float mDrawTextStart;//the drawn text strat
    private float mDrawTextEnd;//the drawn text end
    private String mCureentDrawText;//the text that to be drawn in onDraw()
    private float mOffset;//the text offset
    //painter
    private Paint mUnreachedBarPaint;// the painter of the unreached area
    private Paint mReachedBarPaint;//the painter of the reached area
    private Paint mTextPaint;//the painter of the progress text

    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);

    private RectF mReachedReactF = new RectF(0, 0, 0, 0);

    private boolean mDrawUnreachedBar = true;
    private boolean mDrawReachedBar = true;

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberProgressBarStyle);

    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        default_progress_text_offset = dp2px(3.0f);
        default_reached_bar_height = dp2px(1.5f);
        default_text_size = sp2px(10);
        default_unreached_bar_height = dp2px(1.5f);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberProgressBar, defStyleAttr, 0);
        mReachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_reached_color, default_reached_color);
        mUnreachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_unreached_color, default_unreached_color);
        mTextColor = attributes.getColor(R.styleable.NumberProgressBar_progress_text_color, default_text_color);
        mTextSize = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_size, default_text_size);
        mReachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_reached_bar_height, default_reached_bar_height);
        mUnreachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_unreached_bar_height, default_unreached_bar_height);
        mOffset = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_offset, default_progress_text_offset);
        setProgress(attributes.getInt(R.styleable.NumberProgressBar_progress, 0));
        setMax(attributes.getInt(R.styleable.NumberProgressBar_max, 100));
        attributes.recycle();
        initPainters();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(meause(widthMeasureSpec, true), meause(heightMeasureSpec, false));
    }

    private int meause(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingBottom() + getPaddingTop();
        //MeasureSpec.EXACTLY 具体值 或match parent
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result = result + padding;
            //MeasureSpec.AT_MOST 对应 wrap parent
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateDrawRectF();

        if (mDrawReachedBar) {
            canvas.drawRect(mReachedReactF, mReachedBarPaint);
        }

        if (mDrawUnreachedBar) {
            canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
        }

        canvas.drawText(mCureentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
    }

    private void calculateDrawRectF() {
        mCureentDrawText = String.format("%d%%", getProgress() * 100 / getMax());


        mDrawTextWidth = mTextPaint.measureText(mCureentDrawText);


        if (getProgress() == 0) {
            mDrawReachedBar = false;
            mDrawTextStart = getPaddingLeft();
        } else {
            mDrawReachedBar = true;
            mReachedReactF.left = getPaddingLeft();
            mReachedReactF.right = (getWidth() - getPaddingRight() - getPaddingLeft()) / (getMax() * 1.0f) * getProgress() - mOffset + getPaddingLeft();
            mReachedReactF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
            mReachedReactF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            mDrawTextStart = mReachedReactF.right + mOffset;
        }
        mDrawTextEnd = ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));


        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - mDrawTextWidth;
            mReachedReactF.right = mDrawTextStart - mOffset;
        }

        float unReachBarStart = mDrawTextStart + mDrawTextWidth + mOffset;
        if (unReachBarStart >= getWidth() - getPaddingRight()) {
            mDrawUnreachedBar = false;
        } else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unReachBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }

    }


    private void initPainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);
        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMax() {
        return mMax;
    }


    //set current value of progress
    public void setProgress(int progress) {
        if (progress < mMax & progress >= 0) {
            mProgress = progress;
            invalidate();
        }
    }

    //set max value of progress
    public void setMax(int max) {
        if (max > 0) {
            mMax = max;
            invalidate();
        }
    }

    public void setProgressTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    public void setProgressTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public void setReachedBarColor(int progressColor) {
        mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(progressColor);
        invalidate();
    }

    public void setmUnreachedBarColor(int barColor) {
        mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(barColor);
        invalidate();
    }

}
