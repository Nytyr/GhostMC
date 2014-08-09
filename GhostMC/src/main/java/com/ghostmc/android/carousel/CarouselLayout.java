package com.ghostmc.android.carousel;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ghostmc.android.MainActivity;

public class CarouselLayout extends LinearLayout {
    private float scale = CarouselPagerAdapter.BIG_SCALE;

    public CarouselLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarouselLayout(Context context) {
        super(context);
    }

    public void setScaleBoth(float scale)
    {
        this.scale = scale;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Scale animation
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.scale(scale, scale, w/2, h/2);

        super.onDraw(canvas);
    }
}