package com.nikhilpanju.fabfilter.utils.ArcAnimator;

import android.view.View;

public class ArcUtils {

    public static float sin(double degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    public static float cos(double degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    public static float asin(double value) {
        return (float) Math.toDegrees(Math.asin(value));
    }

    static float acos(double value) {
        return (float) Math.toDegrees(Math.acos(value));
    }

    static float centerX(View view) {
        return view.getX() + view.getWidth() / 2f;
    }

    static float centerY(View view) {
        return view.getY() + view.getHeight() / 2f;
    }
}
