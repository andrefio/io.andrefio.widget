package io.andref.example;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import io.andref.widget.AnimatedCheckBox;

public class CircleViewActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_view);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/PassionOne.ttf");

        AnimatedCheckBox animatedCheckBox = (AnimatedCheckBox) findViewById(R.id.animated_check_box);
        animatedCheckBox.setTypeface(typeface);
    }
}