package com.king.view.slidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


/**
 * A~Z快速索引
 *
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class SlideBar extends View {

    /**
     * 画笔
     */
    private Paint paint = new Paint();

    /**
     * 选中的字母索引
     */
    private int index = -1;

    /**
     * 字母默认颜色
     */
    private int defaultColor = Color.BLACK;

    /**
     * 字母选中颜色
     */
    private int chooseColor = Color.MAGENTA;

    /**
     * 选中背景颜色
     */
    private int chooseBackgroundColor = Color.LTGRAY;
    private final RectF chooseBackgroundRectF = new RectF();

    /**
     * 是否触摸
     */
    private boolean isTouch;

    /**
     * 字母字体大小
     */
    private int textSize;
    private final Rect textSizeRect = new Rect();

    private int padding;

    private Rect rectBound;

    /**
     * 字母改变监听
     */
    private OnTouchLetterChangeListenner onTouchLetterChangeListenner;

    /**
     * 字母数组
     */
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "#"};


    private STYLE chooseStyle = STYLE.DEFAULT;

    /**
     * 选中时背景风格
     */
    public enum STYLE {
        /**
         * 默认：椭圆全背景
         */
        DEFAULT,
        /**
         * 无
         */
        NONE,
        /**
         * 圆形
         */
        CIRCLE,
        /**
         * 拉伸
         */
        STRETCH

    }

    public SlideBar(Context context) {
        super(context);
        init(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        padding = (int) (14 * getResources().getDisplayMetrics().density);

        rectBound = new Rect();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideBar);

        textSize = a.getDimensionPixelSize(R.styleable.SlideBar_android_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        defaultColor = a.getColor(R.styleable.SlideBar_android_textColor, Color.BLACK);
        chooseColor = a.getColor(R.styleable.SlideBar_slideBar_chooseTextColor, Color.MAGENTA);
        chooseBackgroundColor = a.getColor(R.styleable.SlideBar_slideBar_chooseBackgroundColor, Color.LTGRAY);
        chooseStyle = STYLE.values()[a.getInt(R.styleable.SlideBar_slideBar_style, 0)];
        a.recycle();
    }

    /**
     * 设置字母默认色
     *
     * @param color
     */
    public void setDefaultColor(int color) {
        this.defaultColor = color;
    }

    /**
     * 设置字母选中色
     *
     * @param color
     */
    public void setChooseColor(int color) {
        this.chooseColor = color;

    }

    /**
     * 设置选中时控件的背景色
     *
     * @param color
     */
    public void setChooseBacegroundColor(int color) {
        this.chooseBackgroundColor = color;

    }

    /**
     * 设置选中时控件的风格
     *
     * @param style
     */
    public void setChooseStyle(STYLE style) {
        this.chooseStyle = style;
    }

    /**
     * 文本字体大小  单位：dp
     *
     * @param size
     */
    public void setTextSize(int size) {
        this.textSize = size;
    }


    public String[] getLetters() {
        return letters;
    }

    /**
     * 设置字母数据
     *
     * @param letters
     */
    public void setLetters(String[] letters) {
        this.letters = letters;
    }

    /**
     * 设置字母改变回调监听
     *
     * @param onTouchLetterChangeListenner
     */
    public void setOnTouchLetterChangeListenner(OnTouchLetterChangeListenner onTouchLetterChangeListenner) {
        this.onTouchLetterChangeListenner = onTouchLetterChangeListenner;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        paint.setTextSize(textSize);
        paint.getTextBounds("#", 0, 1, rectBound);
        int w = rectBound.width() + padding;
        int h = rectBound.height() + padding;

        int defaultWidth = getPaddingLeft() + w + getPaddingRight();
        int defaultHeight = getPaddingTop() + h * letters.length + getPaddingBottom();

        int width = measureHandler(widthMeasureSpec, defaultWidth);
        int height = measureHandler(heightMeasureSpec, defaultHeight);

        setMeasuredDimension(width, height);

    }

    private int measureHandler(int measureSpec, int defaultSize) {

        int result = defaultSize;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize;
        } else if (measureMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, measureSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //字母的个数
        int len = letters.length;
        //单个字母的高度
        int singleHeight = height / len;

        if (index >= 0 && index < len && chooseBackgroundColor != Color.TRANSPARENT && chooseStyle != STYLE.NONE) { //触摸时画出背景色
            paint.setAntiAlias(true);
            paint.setColor(chooseBackgroundColor);
            if (chooseStyle == STYLE.CIRCLE) {//选中 圆形背景效果
                canvas.drawArc(chooseBackgroundRectF, 0, 360, true, paint);
            } else if (chooseStyle == STYLE.STRETCH) {//选中背景拉伸效果
                canvas.drawArc(new RectF(0, 0, width, singleHeight * index), 0, 360, true, paint);
            } else {//默认：全椭圆背景效果
                canvas.drawArc(new RectF(0, 0, width, singleHeight), 180, 180, true, paint);
                canvas.drawRect(new RectF(0, singleHeight / 2, width, height - singleHeight / 2), paint);
                canvas.drawArc(new RectF(0, height - singleHeight, width, height), 0, 180, true, paint);
            }
        }

        //重置画笔
        paint.reset();
        //画字母
        // 设置字体格式
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Align.CENTER);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 设置字体大小
        paint.setTextSize(textSize);
        if (isTouch) {//触摸时设为粗体字
            paint.setFakeBoldText(true);
        } else {
            paint.setFakeBoldText(false);
        }
        float singleHeight2 = singleHeight / 2f;
        float x = width / 2f;
        paint.getFontMetrics();
        for (int i = 0; i < len; i++) {

            if (i == index) {//选中时的画笔颜色
                paint.setColor(chooseColor);
            } else {//未选中时的画笔颜色
                paint.setColor(defaultColor);
            }
            //要画的字母的x,y坐标
            paint.getTextBounds(letters[i], 0, 1, textSizeRect);
            float y = singleHeight * i + singleHeight2 + (textSizeRect.bottom - textSizeRect.top) / 2f;
            //画字母
            canvas.drawText(letters[i], x, y, paint);
//            canvas.drawLine(0f, singleHeight * i, width, singleHeight * i, paint);

        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //当前选中字母的索引
        final int index = (int) (event.getY() / getHeight() * letters.length);
        //老的索引
        int oldIndex = this.index;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                isTouch = true;
                if (index != oldIndex && index >= 0 && index < letters.length) {
                    this.index = index;
                    resetChooseBackgroundRectF();
                    if (onTouchLetterChangeListenner != null) {//监听回调
                        onTouchLetterChangeListenner.onTouchLetterChange(isTouch, letters[index]);
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:

                isTouch = false;
                if (index >= 0 && index < letters.length) {
                    if (onTouchLetterChangeListenner != null) {//监听回调
                        onTouchLetterChangeListenner.onTouchLetterChange(isTouch, letters[index]);
                    }
                }
//                this.index = -1;
                invalidate();

                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetChooseBackgroundRectF();
    }

    private void resetChooseBackgroundRectF() {
        //单个字母的高度
        float r = textSize / 2f;
        int singleHeight = getHeight() / letters.length;
        r = Math.max(r, Math.min(getWidth(), singleHeight) / 2f);
        chooseBackgroundRectF.left = getWidth() / 2f - r;
        chooseBackgroundRectF.right = getWidth() / 2f + r;
        chooseBackgroundRectF.top = singleHeight * index + singleHeight / 2f - r;
        chooseBackgroundRectF.bottom = singleHeight * index + singleHeight / 2f + r;
    }

    /**
     * 字母改变监听接口
     */
    public interface OnTouchLetterChangeListenner {

        void onTouchLetterChange(boolean isTouch, String letter);
    }

}

