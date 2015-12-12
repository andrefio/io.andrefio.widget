package io.andref.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

public class ShapedImageView extends ImageView
{
    private static final String TAG = "ShapedImageView";

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final int CIRCLE = 0;
    private static final int SQUARE = 1;

    private Bitmap bitmap;
    private BitmapShader bitmapShader;
    private int bitmapHeight;
    private int bitmapWidth;

    private Paint bitmapPaint;
    private Paint fillPaint;
    private Paint strokePaint;
    private TextPaint textPaint;

    private final Matrix shaderMatrix = new Matrix();
    private final RectF borderRect = new RectF();
    private final RectF drawableRect = new RectF();

    private float drawableRadius;
    private float strokeRadius;

    private boolean isReady;
    private boolean isInitializationPending;

    /** Attributes */
    private boolean borderOverlay;
    private int fillColor;
    private int shape;
    private int strokeColor;
    private int strokeSize;
    private String text;
    private int textColor = Color.WHITE;
    private int textSize = 24;

    public ShapedImageView(Context context)
    {
        this(context, null);
    }

    public ShapedImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ShapedImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initializeViews(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ShapedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initializeViews(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        // Convert the default DP/SP size to real pixels.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, displayMetrics);

        final TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ShapedImageView, defStyleAttr, defStyleRes);

        try
        {
            fillColor = a.getColor(R.styleable.ShapedImageView_siv_fillColor, fillColor);
            shape = a.getInteger(R.styleable.ShapedImageView_siv_shape, 0);
            strokeColor = a.getColor(R.styleable.ShapedImageView_siv_strokeColor, strokeColor);
            strokeSize = a.getDimensionPixelSize(R.styleable.ShapedImageView_siv_strokeSize, strokeSize);
            text = a.getString(R.styleable.ShapedImageView_siv_text);
            textColor = a.getColor(R.styleable.ShapedImageView_siv_textColor, textColor);
            textSize = a.getDimensionPixelSize(R.styleable.ShapedImageView_siv_textSize, textSize);
        }
        finally
        {
            a.recycle();
        }

        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        fillPaint = new Paint();
        strokePaint = new Paint();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.density = getResources().getDisplayMetrics().density;
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setLinearText(true);

        setFillColor(fillColor);
        setShape(shape);
        setStrokeColor(strokeColor);
        setStrokeSize(strokeSize);
        setText(text);
        setTextColor(textColor);
        setTextSize(textSize);


        setScaleType(SCALE_TYPE);
        isReady = true;

        if (isInitializationPending)
        {
            initialize();
            isInitializationPending = false;
        }
    }

    private void initialize()
    {
        if (!isReady)
        {
            isInitializationPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0)
        {
            return;
        }

//
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeSize);
//
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(fillColor);
//
        borderRect.set(0, 0, getWidth(), getHeight());
        strokeRadius = Math.min((borderRect.height() - strokeSize) / 2.0f, (borderRect.width() - strokeSize) / 2.0f);

        drawableRect.set(borderRect);
        if (!borderOverlay)
        {
            drawableRect.inset(strokeSize, strokeSize);
        }

        drawableRadius = Math.min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f);

        if (bitmap != null)
        {
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            bitmapPaint.setAntiAlias(true);
            bitmapPaint.setShader(bitmapShader);

            bitmapHeight = bitmap.getHeight();
            bitmapWidth = bitmap.getWidth();

            updateShaderMatrix();
        }

        invalidate();
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds)
    {
        if (adjustViewBounds)
        {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        switch (shape)
        {
            case SQUARE:

                canvas.drawRect(drawableRect, fillPaint);

                if (bitmap != null)
                {
                    canvas.drawRect(drawableRect, bitmapPaint);

                    if (strokeSize > 0)
                    {
                        canvas.drawRect(drawableRect, strokePaint);
                    }
                }

                break;

            case CIRCLE:
            default:

                canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, drawableRadius, fillPaint);

                if (bitmap != null)
                {
                    canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, drawableRadius, bitmapPaint);

                    if (strokeSize > 0)
                    {
                        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, strokeRadius, strokePaint);
                    }
                }

                break;
        }

        if (text != null)
        {
            float x = drawableRect.centerX();
            float y = (drawableRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2);

            canvas.drawText(text, x, y, textPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        initialize();
    }

    private void updateShaderMatrix()
    {
        float scale;
        float dx = 0;
        float dy = 0;

        shaderMatrix.set(null);

        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight)
        {
            scale = drawableRect.height() / (float) bitmapHeight;
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f;
        }
        else
        {
            scale = drawableRect.width() / (float) bitmapWidth;
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f;
        }

        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate((int) (dx + 0.5f) + drawableRect.left, (int) (dy + 0.5f) + drawableRect.top);

        bitmapShader.setLocalMatrix(shaderMatrix);
    }


    // region Getters/Setters

    private Bitmap getBitmap(Drawable drawable)
    {
        if (drawable != null)
        {

            if (drawable instanceof BitmapDrawable)
            {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            try
            {
                Bitmap bitmap;

                if (drawable instanceof ColorDrawable)
                {
                    bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
                }
                else
                {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
                }

                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                return bitmap;
            }
            catch (Exception e)
            {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    public int getFillColor()
    {
        return fillColor;
    }

    public void setFillColor(int fillColor)
    {
        this.fillColor = fillColor;
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);

        bitmap = bm;
        initialize();
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);

        bitmap = getBitmap(drawable);
        initialize();
    }

    @Override
    public void setImageResource(@DrawableRes int resId)
    {
        super.setImageResource(resId);

        bitmap = getBitmap(getDrawable());
        initialize();
    }

    @Override
    public void setImageURI(Uri uri)
    {
        super.setImageURI(uri);

        bitmap = uri != null ? getBitmap(getDrawable()) : null;
        initialize();
    }

    @Override
    public ScaleType getScaleType()
    {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType)
    {
        if (scaleType != SCALE_TYPE)
        {
            throw new IllegalArgumentException(String.format("scaleType %s not supported.", scaleType));
        }
    }

    public int getShape()
    {
        return shape;
    }

    public void setShape(int shape)
    {
        this.shape = shape;
        invalidate();
    }

    public int getStrokeColor()
    {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor)
    {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public int getStrokeSize()
    {
        return strokeSize;
    }

    public void setStrokeSize(int strokeSize)
    {
        this.strokeSize = strokeSize;
        invalidate();
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
        invalidate();
    }

    public int getTextColor()
    {
        return textPaint.getColor();
    }

    public void setTextColor(int color)
    {
        textPaint.setColor(color);
        invalidate();
    }

    public float getTextSize()
    {
        return textPaint.getTextSize();
    }

    public void setTextSize(float size)
    {
        textPaint.setTextSize(size);
        invalidate();
    }

    public Typeface getTypeface() {
        return textPaint.getTypeface();
    }

    public void setTypeface(Typeface typeface)
    {
        textPaint.setTypeface(typeface);
        invalidate();
    }

    // endregion
}