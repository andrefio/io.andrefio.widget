package io.andref.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AnimatedCheckBox extends RelativeLayout
{
    private static final String TAG = "AnimatedCheckBox";

    private static final String SELECTED = "selected";
    private static final String SUPER = "super";

    private static final int DEFAULT_CLICK_1_DURATION = 150;
    private static final int DEFAULT_CLICK_2_DURATION = 200;

    /** The animations used to flip the compound view */
    private AnimatorSet click1FlipAnimationSet;
    private AnimatorSet click2FlipAnimationSet;

    private FrameLayout frameLayout1;
    private FrameLayout frameLayout2;

    private ImageView imageView;
    private ShapedImageView shapedImageView1;
    private ShapedImageView shapedImageView2;

    /** Styleable attributes */
    private boolean animated = true;
    private int fillColor = Color.GRAY;
    private int iconHeight = 24;
    private int iconResource = R.drawable.ic_check_black_24dp;
    private int iconTint = Color.WHITE;
    private int iconWidth = 24;
    private int imageResource;
    private int shape;
    private int strokeColor = Color.DKGRAY;
    private int strokeSize;
    private String text;
    private int textColor = Color.WHITE;
    private int textSize = 24;
    private Typeface typeface;

    public AnimatedCheckBox(Context context)
    {
        this(context, null);
    }

    public AnimatedCheckBox(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AnimatedCheckBox(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initializeViews(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public AnimatedCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initializeViews(context, attrs, defStyleAttr, defStyleRes);
    }

    // region Lifecycle Methods

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();

        bundle.putBoolean(SELECTED, this.isSelected());
        bundle.putParcelable(SUPER, super.onSaveInstanceState());

        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle)state;

            setSelected(bundle.getBoolean(SELECTED));
            super.onRestoreInstanceState(bundle.getParcelable(SUPER));
        }
        else
        {
            super.onRestoreInstanceState(state);
        }
    }

    // endregion

    private void initializeAnimations()
    {
        Animator click1FrameLayout1Animator = ObjectAnimator.ofFloat(frameLayout1, View.ROTATION_Y, 0, 90);
        click1FrameLayout1Animator.setDuration(DEFAULT_CLICK_1_DURATION);

        Animator click1FrameLayout2Animator = ObjectAnimator.ofFloat(frameLayout2, View.ROTATION_Y, 90, 0);
        click1FrameLayout2Animator.setDuration(DEFAULT_CLICK_1_DURATION);

        AnimatorSet click1ImageViewAnimatorSet = new AnimatorSet();
        click1ImageViewAnimatorSet.setDuration(200);
        click1ImageViewAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0, 1),
                ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0, 1),
                ObjectAnimator.ofFloat(imageView, View.ALPHA, 0, 1)
        );

        AnimatorSet click1FrameLayout2AnimatorSet = new AnimatorSet();
        click1FrameLayout2AnimatorSet.playTogether(
                click1FrameLayout2Animator,
                click1ImageViewAnimatorSet
        );

        click1FlipAnimationSet = new AnimatorSet();
        click1FlipAnimationSet.setInterpolator(new AccelerateInterpolator());
        click1FlipAnimationSet.playSequentially(
                click1FrameLayout1Animator,
                click1FrameLayout2AnimatorSet
        );


        click2FlipAnimationSet = new AnimatorSet();
        click2FlipAnimationSet.setDuration(DEFAULT_CLICK_2_DURATION);
        click2FlipAnimationSet.setInterpolator(new AccelerateInterpolator());
        click2FlipAnimationSet.playSequentially(
                ObjectAnimator.ofFloat(frameLayout2, View.ROTATION_Y, 0, 90),
                ObjectAnimator.ofFloat(frameLayout1, View.ROTATION_Y, 90, 0)
        );
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        // Convert the default DP/SP size to real pixels.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        iconHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iconHeight, displayMetrics);
        iconWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iconWidth, displayMetrics);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, displayMetrics);

        // Now fetch the styleable attributes from the layout and stash them.
        final TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.AnimatedCheckBox, defStyleAttr, defStyleRes);

        try
        {
            animated = a.getBoolean(R.styleable.AnimatedCheckBox_acb_animate, animated);
            fillColor = a.getColor(R.styleable.AnimatedCheckBox_acb_fillColor, fillColor);
            iconHeight = a.getDimensionPixelSize(R.styleable.AnimatedCheckBox_acb_iconHeight, iconHeight);
            iconResource = a.getResourceId(R.styleable.AnimatedCheckBox_acb_iconResource, iconResource);
            iconTint = a.getInt(R.styleable.AnimatedCheckBox_acb_iconTint, iconTint);
            iconWidth = a.getDimensionPixelSize(R.styleable.AnimatedCheckBox_acb_iconWidth, iconWidth);
            imageResource = a.getResourceId(R.styleable.AnimatedCheckBox_acb_imageResource, imageResource);
            shape = a.getInt(R.styleable.AnimatedCheckBox_acb_shape, shape);
            strokeColor = a.getColor(R.styleable.AnimatedCheckBox_acb_strokeColor, strokeColor);
            strokeSize = a.getDimensionPixelSize(R.styleable.AnimatedCheckBox_acb_strokeSize, strokeSize);
            text = a.getString(R.styleable.AnimatedCheckBox_acb_text);
            textColor = a.getColor(R.styleable.AnimatedCheckBox_acb_textColor, textColor);
            textSize = a.getDimensionPixelSize(R.styleable.AnimatedCheckBox_acb_textSize, textSize);
        }
        finally
        {
            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_check_view, this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        frameLayout1 = (FrameLayout) findViewById(R.id.frame_layout_1);
        frameLayout1.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AnimatedCheckBox.this.setSelected(true);

                if (animated)
                {
                    click1FlipAnimationSet.start();
                }
            }
        });

        frameLayout2 = (FrameLayout) findViewById(R.id.frame_layout_2);
        frameLayout2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AnimatedCheckBox.this.setSelected(false);

                if (animated)
                {
                    click2FlipAnimationSet.start();
                }
            }
        });

        // Image contained inside the back side of the compound view.
        imageView = (ImageView) findViewById(R.id.image_view);

        shapedImageView1 = (ShapedImageView) findViewById(R.id.shaped_image_view_1);
        shapedImageView2 = (ShapedImageView) findViewById(R.id.shaped_image_view_2);


        // Set styleable attributes.
        setAnimated(animated);
        setFillColor(fillColor);
        setIconHeight(iconHeight);
        setIconResource(iconResource);
        setIconTint(iconTint);
        setIconWidth(iconWidth);
        setImageResource(imageResource);
        setShape(shape);
        setStrokeColor(strokeColor);
        setStrokeSize(strokeSize);
        setText(text);
        setTextColor(textColor);
        setTextSize(textSize);


        if (animated)
        {
            initializeAnimations();
        }
    }

    // region Getters/Setters

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params)
    {

        frameLayout1.getLayoutParams().height = params.height;
        frameLayout1.getLayoutParams().width = params.width;

        frameLayout2.getLayoutParams().height = params.height;
        frameLayout2.getLayoutParams().width = params.width;

        super.setLayoutParams(params);
    }

    public boolean isAnimated()
    {
        return animated;
    }

    public void setAnimated(boolean animated)
    {
        this.animated = animated;
        invalidate();
    }

    public int getFillColor()
    {
        return fillColor;
    }

    public void setFillColor(int fillColor)
    {
        this.fillColor = fillColor;

        shapedImageView2.setFillColor(fillColor);
        invalidate();
    }

    public int getIconHeight()
    {
        return iconHeight;
    }

    public void setIconHeight(int iconHeight)
    {
        this.iconHeight = iconHeight;

        imageView.getLayoutParams().height = iconHeight;
        invalidate();
    }

    public int getIconResource()
    {
        return iconResource;
    }

    public void setIconResource(int iconResource)
    {
        this.iconResource = iconResource;

        imageView.setImageResource(iconResource);
        invalidate();
    }

    public int getIconTint()
    {
        return iconTint;
    }

    public void setIconTint(int iconTint)
    {
        this.iconTint = iconTint;

        imageView.setColorFilter(iconTint);
        invalidate();
    }

    public int getIconWidth()
    {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth)
    {
        this.iconWidth = iconWidth;

        imageView.getLayoutParams().width = iconWidth;
        invalidate();
    }

    public int getImageResource()
    {
        return imageResource;
    }

    public void setImageResource(int imageResource)
    {
        this.imageResource = imageResource;

        shapedImageView1.setImageResource(imageResource);
        invalidate();
    }

    public int getShape()
    {
        return shape;
    }

    public void setShape(int shape)
    {
        this.shape = shape;

        shapedImageView1.setShape(shape);
        shapedImageView2.setShape(shape);
        invalidate();
    }

    public int getStrokeColor()
    {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor)
    {
        this.strokeColor = strokeColor;

        shapedImageView1.setStrokeColor(strokeColor);
        invalidate();
    }

    public int getStrokeSize()
    {
        return strokeSize;
    }

    public void setStrokeSize(int strokeSize)
    {
        this.strokeSize = strokeSize;

        shapedImageView1.setStrokeSize(strokeSize);
        invalidate();
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;

        shapedImageView1.setText(text);
        invalidate();
    }

    public int getTextColor()
    {
        return textColor;
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;

        shapedImageView1.setTextColor(textColor);
        invalidate();
    }

    public int getTextSize()
    {
        return textSize;
    }

    public void setTextSize(int textSize)
    {
        this.textSize = textSize;

        shapedImageView1.setTextSize(textSize);
        invalidate();
    }

    public Typeface getTypeface()
    {
        return typeface;
    }

    public void setTypeface(Typeface typeface)
    {
        this.typeface = typeface;

        shapedImageView1.setTypeface(typeface);
        invalidate();
    }

    // endregion
}