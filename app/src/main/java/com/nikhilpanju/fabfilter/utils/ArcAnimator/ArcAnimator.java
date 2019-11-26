package com.nikhilpanju.fabfilter.utils.ArcAnimator;

import android.animation.ValueAnimator;
import android.view.View;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ArcAnimator extends ValueAnimator {

    ArcMetric mArcMetric;
    private View mTarget;
    //    private ValueAnimator mAnimator;
    private float mValue;

    private ArcAnimator(ArcMetric arcmetric, View target) {
        mArcMetric = arcmetric;
        mTarget = (target);

        ofFloat(arcmetric.getStartDegree(), arcmetric.getEndDegree());
        addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setDegree((Float) animation.getAnimatedValue());
            }
        });
    }

    public static ArcAnimator createArcAnimator(View clipView, View nestView,
                                                float degree, Side side) {

        return createArcAnimator(clipView, ArcUtils.centerX(nestView),
                ArcUtils.centerY(nestView),
                degree, side);
    }

    public static ArcAnimator createArcAnimator(View clipView, float endX,
                                                float endY,
                                                float degree, Side side) {

        ArcMetric arcMetric = ArcMetric.evaluate(ArcUtils.centerX(clipView),
                ArcUtils.centerY(clipView),
                endX, endY, degree, side);
        return new ArcAnimator(arcMetric, clipView);
    }

    public static ValueAnimator createArcAnimator(View clipView, float startX
            , float startY, float endX, float endY,
                                                  float degree, Side side) {

        ArcMetric arcMetric = ArcMetric.evaluate(startX, startY, endX, endY,
                degree, side);
        return getAnimator(arcMetric, clipView);
    }

    private static ValueAnimator getAnimator(final ArcMetric arcMetric,
                                             final View target) {
        ValueAnimator valueAnimator =
                ValueAnimator.ofFloat(arcMetric.getStartDegree(),
                        arcMetric.getEndDegree());
        valueAnimator.addUpdateListener(animation -> {
            float degree = (Float) animation.getAnimatedValue();
//                mValue = degree;
            float x =
                    arcMetric.getAxisPoint().x + arcMetric.mRadius * ArcUtils.cos(degree);
            float y =
                    arcMetric.getAxisPoint().y - arcMetric.mRadius * ArcUtils.sin(degree);
            target.setX(x - target.getWidth() / 2);
            target.setY(y - target.getHeight() / 2);
        });
        return valueAnimator;
    }

    float getDegree() {
        return mValue;
    }

    private void setDegree(float degree) {
        mValue = degree;
        View clipView = mTarget;
        float x =
                mArcMetric.getAxisPoint().x + mArcMetric.mRadius * ArcUtils.cos(degree);
        float y =
                mArcMetric.getAxisPoint().y - mArcMetric.mRadius * ArcUtils.sin(degree);
        clipView.setX(x - clipView.getWidth() / 2);
        clipView.setY(y - clipView.getHeight() / 2);
    }

    @Override
    public String toString() {
        return mArcMetric.toString();
    }
}
