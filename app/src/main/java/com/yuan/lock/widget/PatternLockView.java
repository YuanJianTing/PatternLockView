package com.yuan.lock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yuan.lock.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PatternLockView extends View {

    public static final String TAG = "PatternLockView";

    private Context context;

    /**
     * 圆圈 画笔
     */
    private Paint mPaint;
    /**
     * 连接线画笔
     */
    private Paint linePaint;
    /**
     * 文字画笔
     */
    private TextPaint textPaint;
    /**
     * 实心圆画笔
     */
    private Paint solidCirclePaint;

    /**
     * 圆 半径
     */
    private float radius = 70;
    /**
     * 圆圈 选中颜色
     */
    private int selectedColor;
    /**
     * 圆圈默认颜色
     */
    private int color;
    /**
     * 连接线颜色
     */
    private int lineColor;
    /**
     * 连接线的粗细
     */
    private float strokeWidth = 2;
    /**
     * 提示信息
     */
    private String tips;
    private String defaultTips;
    /**
     * 文字颜色
     */
    private int textColor;
    /**
     * 文本大小
     */
    private float textSize;
    private List<Integer> values;
    private LinkedList<CircleView> circleViews;
    private LinkedList<PointF> lines;
    /**
     * 手指实时位置
     */
    private float x, y;

    /**
     * 缩略图 起始坐标
     * startY 距离顶端 90
     */
    private float startX, startY=90;


    public PatternLockView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public PatternLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public PatternLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        values = new LinkedList<>();
        circleViews = new LinkedList<>();
        lines = new LinkedList<>();

        if (attrs == null) {
            selectedColor = R.color.lock_view_selected_color;
            color = R.color.lock_view_default_color;
            lineColor = R.color.lock_view_selected_color;
            textColor = R.color.lock_view_text_color;
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.PatternLockView);

            radius = typedArray.getDimension(R.styleable.PatternLockView_radius, 70);
            selectedColor = typedArray.getColor(R.styleable.PatternLockView_selectedColor, getColor(R.color.lock_view_selected_color));
            color = typedArray.getColor(R.styleable.PatternLockView_color, getColor(R.color.lock_view_default_color));
            lineColor = typedArray.getColor(R.styleable.PatternLockView_lineColor, getColor(R.color.lock_view_selected_color));
            strokeWidth = typedArray.getDimension(R.styleable.PatternLockView_strokeWidth, 2);
            tips = typedArray.getString(R.styleable.PatternLockView_tips);
            defaultTips = tips;
            textColor = typedArray.getColor(R.styleable.PatternLockView_textColor, getColor(R.color.lock_view_text_color));
            textSize = typedArray.getDimension(R.styleable.PatternLockView_textSize, 20f);
            typedArray.recycle();
        }


        mPaint = new Paint();
        // 设置画笔为抗锯齿
        mPaint.setAntiAlias(true);
        // 设置颜色为红色
        mPaint.setColor(color);
        /**
         * 画笔样式分三种： 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充
         * 3.Paint.Style.FILL：填充
         */
        mPaint.setStyle(Paint.Style.STROKE);
        /**
         * 设置描边的粗细，单位：像素px 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
         */
        mPaint.setStrokeWidth(strokeWidth);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        //textPaint.setTextAlign(Paint.Align.CENTER);

        solidCirclePaint = new Paint();
        solidCirclePaint.setAntiAlias(true);
        solidCirclePaint.setColor(color);
        solidCirclePaint.setStrokeWidth(1);
        solidCirclePaint.setStyle(Paint.Style.FILL);

        for (int i=1;i<=9;i++){
            circleViews.add(new CircleView(i));
        }

        initCircle();
    }


    private void initCircle() {
        int w = getWidth();
        if (w == 0)
            return;
        /**
         *缩略图 x 坐标
         */
        startX = w / 2 - 20;

    }

    private Handler handler = new Handler();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * startY 为距离顶端 90
         */
        startY = 90;

        //绘制缩略图
        drawTumbnail(canvas);

        //绘制提示信息
        if (!TextUtils.isEmpty(tips)) {
            /**
             * 提示信息距离 缩略图 50
             */
            startY+=  50;

            float stringWidth = textPaint.measureText(tips);
            float x = getWidth() / 2 - stringWidth / 2;

            canvas.drawText(tips, x, startY, textPaint);
        }

        int index = 0;
        /**
         * 圆圈距离 提示信息 70
         */
        startY+=70;
        float startX=getWidth()/2-radius*4;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                CircleView item=circleViews.get(index);
                item.updateBounds(startX,startY);
                item.drawCircle(canvas);
                index++;
                startX+=radius*3;
            }
            startY+=radius*3;
            startX=getWidth()/2-radius*4;
        }

        //画链接线
        for (int i = 1; i < lines.size(); i++) {
            PointF firstNode = lines.get(i - 1);
            PointF secondNode = lines.get(i);
            canvas.drawLine(firstNode.x, firstNode.y, secondNode.x, secondNode.y, linePaint);
        }
        if (lines.size() > 0) {
            //画手指移动的线
            PointF last = lines.getLast();
            canvas.drawLine(last.x, last.y, x, y, linePaint);
        }


    }

    /**
     * 绘制缩略图
     *
     * @param canvas
     */
    private void drawTumbnail(Canvas canvas) {

        int i = 0;
        for (int r = 1; r <= 3; r++) {
            startX = getWidth() / 2 - 20;
            for (int c = 1; c <= 3; c++) {
                //判断是否选中
                CircleView view =circleViews.get(i);
                if (view.isSelected) {
                    solidCirclePaint.setColor(selectedColor);
                    solidCirclePaint.setStyle(Paint.Style.FILL);
                } else {
                    solidCirclePaint.setColor(color);
                    solidCirclePaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawCircle(startX, startY, 6, solidCirclePaint);
                startX += 25;
                i++;
            }
            startY += 25;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                Log.e(TAG, "onTouchEvent: 按下");
                tips = defaultTips;
                this.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                //抬起
                //Log.e(TAG, "onTouchEvent: 抬起" );
                if (values.size() == 0)
                    return false;

                if (patternLockViewListener != null) {
                    int[] arr = new int[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        arr[i] = values.get(i);
                    }
                    patternLockViewListener.onLockEnd(arr);
                }
                handler.postDelayed(() -> {
                    for (CircleView item : circleViews) {
                        item.isSelected = false;
                    }
                    values.clear();
                    lines.clear();
                    this.invalidate();
                }, 500);

                return true;
            case MotionEvent.ACTION_MOVE:
                //移动
                x = event.getX();
                y = event.getY();
                //Log.e(TAG, "onTouchEvent: 移动" );
                for (CircleView item : circleViews) {
                    if (!item.isSelected && item.bounds.contains(x, y)) {
                        item.isSelected = true;
                        lines.add(new PointF(item.bounds.left + radius, item.bounds.top + radius));
                        values.add(item.value);
                        break;
                    }
                }
                this.invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initCircle();
        this.invalidate();
    }


    private int getColor(@ColorRes int colorId) {
        return context.getResources().getColor(colorId);
    }

    /**
     * 设置提示信息
     *
     * @param tips
     */
    public void setTips(String tips) {
        this.tips = tips;
        this.invalidate();
    }

    private PatternLockViewListener patternLockViewListener;

    public void setPatternLockViewListener(PatternLockViewListener patternLockViewListener) {
        this.patternLockViewListener = patternLockViewListener;
    }


    class CircleView {

        CircleView(int value) {
            bounds = new RectF();
            this.value = value;
        }

        int value;
        RectF bounds;
        boolean isSelected;

        void updateBounds(float x,float y){
            bounds.top=y;
            bounds.left=x;
            bounds.bottom=y+radius*2;
            bounds.right=x+radius*2;
        }

        /**
         * 画圆
         *
         * @param canvas
         */
        void drawCircle(Canvas canvas) {
            if (isSelected)
                mPaint.setColor(selectedColor);
            canvas.drawCircle(bounds.left + radius, bounds.top + radius, radius, mPaint);
            if (isSelected) {
                //画实心圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(bounds.left + radius, bounds.top + radius, radius / 2.5f, mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(color);
            }
//        String text=String.format("x=%s,y=%s,value=%s",circleView.bounds.left + radius,circleView.bounds.top + radius,circleView.value);
//        canvas.drawText(text,circleView.bounds.left,circleView.bounds.top + radius,mPaint);
        }

    }

    public interface PatternLockViewListener {
        void onLockEnd(int[] values);
    }

}
