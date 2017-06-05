package com.saltedfishcaptain.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Description:
 * <p>
 * Attention:
 * <p>
 * Created by Zhengyu.Xiong ; On 2017-06-05.
 */

public class ShadowLayout extends FrameLayout{
    private int shadowColor; // 阴影颜色
    private float cornerRadius; // 阴影实体边缘的圆角半径
    private float shadowRadius; // 投影半径，对应PS阴影设置中的大小，是阴影渐变区的半径，上下左右都会增加区域，0会导致没有阴影
    private float shadowOffsetX; // 阴影X方向的偏移
    private float shadowOffsetY; // 阴影Y方向的偏移
    private float shadowPaddingLeft; // 阴影区域左侧的缩进
    private float shadowPaddingRight; // 阴影区域右侧的缩进
    private float shadowPaddingTop; // 阴影区域上方的缩进
    private float shadowPaddingBottom; // 阴影区域下方的缩进

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int paddingLeft = (int) (shadowRadius - shadowOffsetX - shadowPaddingLeft);
        int paddingRight = (int) (shadowRadius + shadowOffsetX - shadowPaddingRight);

        int paddingTop = (int) (shadowRadius - shadowOffsetY - shadowPaddingTop);
        int paddingBottom = (int) (shadowRadius + shadowOffsetY - shadowPaddingBottom);

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout,0,0);

        cornerRadius = attr.getDimension(R.styleable.ShadowLayout_corner_radius,
                getResources().getDimension(R.dimen.shadow_layout_default_corner_radius));
        shadowRadius = attr.getDimension(R.styleable.ShadowLayout_shadow_radius,
                getResources().getDimension(R.dimen.shadow_layout_default_shadow_radius));
        shadowColor = attr.getColor(R.styleable.ShadowLayout_shadow_color,
                getResources().getColor(R.color.shadow_layout_default_shadow_color));
        shadowOffsetX = attr.getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0);
        shadowOffsetY = attr.getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0);
        shadowPaddingLeft = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_left, 0);
        shadowPaddingRight = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_right, 0);
        shadowPaddingTop = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_top, 0);
        shadowPaddingBottom = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_bottom, 0);

        attr.recycle();
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    /*-------------------------------------------------*/

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        // Cause paint.setShadowLayer() can not be previewed.
        if (isInEditMode()) {
            setBackgroundColor(shadowColor);
            return;
        }

        Bitmap bitmap = createShadowBitmap(w, h, cornerRadius, shadowRadius, shadowColor);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight,
                                      float cornerRadius, float shadowRadius, int shadowColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.TRANSPARENT);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        return output;
    }

    /*--------------------------------------------------------------------------------------------*/

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }
}
