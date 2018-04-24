package com.demo.charcolee.slideunlockview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.demo.charcolee.slideunlockview.R;


/**
 * Created 18/4/20 21:03
 * Author:charcolee
 * Version:V1.0
 * ----------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------
 */

public class SlideUnlockView extends View {

    private Paint mPaint;//绘制背景
    private Paint mTextPaint;//绘制文字
    private Paint mUnablePaint;//绘制不可滑动的朦层
    private Bitmap mIconBitmap;
    private int mWidth;
    private int mHeight;
    private int mPadding;
    private String content = ">>向右滑动结束行程";
    private Rect mTextRect;
    private float mDrawX;
    private boolean mCanMove;
    private OnUnlockListener mListener;
    private int mBackgroudColor;
    private int mTextColor;
    private int mIconId;
    private boolean unSlide;

    public SlideUnlockView(Context context) {
        this(context,null);
    }

    public SlideUnlockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideUnlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SlideUnlockView);
        content = ta.getString(R.styleable.SlideUnlockView_content);
        mBackgroudColor = ta.getColor(R.styleable.SlideUnlockView_backgroundColor,getResources().getColor(R.color.background_color));
        mTextColor = ta.getColor(R.styleable.SlideUnlockView_textColor,getResources().getColor(R.color.text_color));
        mIconId = ta.getResourceId(R.styleable.SlideUnlockView_icon, R.drawable.icon_slide);
        unSlide = ta.getBoolean(R.styleable.SlideUnlockView_unSlide,false);
        ta.recycle();
    }

    //设置为不可滑动
    public void setUnSlide(boolean unable){
        this.unSlide = unable;
    }

    private void init() {

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(getContext(),16));
        mTextRect = new Rect();
        mTextPaint.getTextBounds(content,0,content.length(),mTextRect);

        mIconBitmap = BitmapFactory.decodeResource(getResources(), mIconId);
        mPadding  = dp2px(getContext(),5);


        mHeight = mIconBitmap.getHeight()+mPadding*2;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mBackgroudColor);
        mPaint.setStrokeWidth(mHeight);

        //默认滑动图标跟文字间的间隔
        int middle = dp2px(getContext(), 20);
        mWidth = mPadding + mHeight*2 + mTextRect.width() + middle *2;
        int unableColor = getResources().getColor(R.color.white_translucent);

        mUnablePaint = new Paint(mPaint);
        mUnablePaint.setColor(unableColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        //如果设定的宽度比控件默认需求的大，则修改，否则就用默认宽度
       if (wSpecMode == MeasureSpec.EXACTLY){
            if (wSpecSize>mWidth){
                mWidth = wSpecSize;
            }
        }

        setMeasuredDimension(mWidth,mHeight);

    }

    public void setUnlockListener(OnUnlockListener listener){
        mListener = listener;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawX < mPadding ){
            mDrawX = mPadding;
        }else if (mDrawX > mWidth - mIconBitmap.getWidth()-mPadding){
            mDrawX = mWidth - mIconBitmap.getWidth()-mPadding;
        }
        //画背景颜色，需要给圆形笔锋预留半个高度的位置
        canvas.drawLine(mHeight/2,mHeight/2,mWidth-mHeight/2,mHeight/2,mPaint);
        int textX = mWidth / 2 - mTextRect.width()/2;
        //绘制文字到中间位置
        canvas.drawText(content,textX,mHeight/2+mTextRect.height()/2,mTextPaint);
        //根据滑动位置绘制图标
        canvas.drawBitmap(mIconBitmap,mDrawX,mPadding,mPaint);

        //绘制不可滑动的朦层
        if (unSlide){
            canvas.drawLine(mHeight/2,mHeight/2,mWidth-mHeight/2,mHeight/2,mUnablePaint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //如果不可滑动，立即返回
                if (unSlide)return false;
                if (x <= mIconBitmap.getWidth()+mPadding){
                    mCanMove = true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                //如果是可移动状态才继续绘制
                if (mCanMove){
                    mDrawX = x;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                //如果是可移动状态才继续绘制
                if (mCanMove){
                    reset();
                    mCanMove = false;
                    //判断是否在指定位置抬起手指
                    if (mDrawX >= mWidth - mIconBitmap.getWidth()-mPadding){
                        if (mListener!=null)
                            mListener.onUnlock();
                        Log.d("charco","解锁成功");
                    }
                }
                break;

        }
        return super.onTouchEvent(event);
    }

    //用属性动画将图标位置重置
    private void reset(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"mDrawX",mDrawX,mPadding);
        animator.setDuration(200);
        animator.start();
    }

    private float getMDrawX() {
        return mDrawX;
    }

    private void setMDrawX(float mDrawX) {
        this.mDrawX = mDrawX;
        invalidate();
    }

    public interface OnUnlockListener{
        void onUnlock();
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * sp转px
     *
     * @param context 上下文
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
