///*
// * Copyright (C) 2014 Pixplicity
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.nikhilpanju.fabfilter.legacy;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Point;
//import androidx.viewpager.widget.ViewPager;
//import android.util.AttributeSet;
//import android.view.View;
//
//import com.nikhilpanju.fabfilter.R;
//import com.nikhilpanju.fabfilter.utils.ViewUtilsKt;
//
///**
// * From https://github.com/Pixplicity/MultiViewPager
// */
//public class MultiViewPager extends ViewPager {
//
//    /**
//     * Maximum size.
//     */
//    private int mMaxWidth = -1;
//    /**
//     * Maximum size.
//     */
//    private int mMaxHeight = -1;
//    /**
//     * Child view inside a page to match the page size against.
//     */
//    private int mMatchWidthChildResId;
//
//    /**
//     * Internal state to schedule a new measurement pass.
//     */
//    private boolean mNeedsMeasurePage;
//    private final Point size;
//    private final Point maxSize;
//
//    private static void constrainTo(Point size, Point maxSize) {
//        if (maxSize.x >= 0) {
//            if (size.x > maxSize.x) {
//                size.x = maxSize.x;
//            }
//        }
//        if (maxSize.y >= 0) {
//            if (size.y > maxSize.y) {
//                size.y = maxSize.y;
//            }
//        }
//    }
//
//    public MultiViewPager(Context context) {
//        super(context);
//        size = new Point();
//        maxSize = new Point();
//    }
//
//    public MultiViewPager(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//        size = new Point();
//        maxSize = new Point();
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        setClipChildren(false);
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable
//        .MultiViewPager);
//        setMaxWidth(ta.getDimensionPixelSize(R.styleable
//        .MultiViewPager_android_maxWidth, -1));
//        setMaxHeight(ta.getDimensionPixelSize(R.styleable
//        .MultiViewPager_android_maxHeight, -1));
//        setMatchChildWidth(ta.getResourceId(R.styleable
//        .MultiViewPager_matchChildWidth, 0));
//        ta.recycle();
//    }
//
//    @Override
//    protected boolean canScroll(View v, boolean checkV, int dx, int x, int
//    y) {
//        return false;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        size.set(MeasureSpec.getSize(widthMeasureSpec),
//                MeasureSpec.getSize(heightMeasureSpec));
//        if (mMaxWidth >= 0 || mMaxHeight >= 0) {
//            maxSize.set(mMaxWidth, mMaxHeight);
//            constrainTo(size, maxSize);
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
//                    size.x,
//                    MeasureSpec.EXACTLY);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
//                    size.y,
//                    MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        onMeasurePage(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    protected void onMeasurePage(int widthMeasureSpec, int
//    heightMeasureSpec) {
//        // Only measure if a measurement pass was scheduled
//        if (!mNeedsMeasurePage) {
//            return;
//        }
//        if (mMatchWidthChildResId == 0) {
//            mNeedsMeasurePage = false;
//        } else if (getChildCount() > 0) {
//            View child = getChildAt(0);
//            child.measure(widthMeasureSpec, heightMeasureSpec);
//            int pageWidth = child.getMeasuredWidth();
//            View match = child.findViewById(mMatchWidthChildResId);
//            if (match == null) {
//                throw new NullPointerException(
//                        "MatchWithChildResId did not find that ID in the
//                        first fragment of the ViewPager; "
//                                + "is that view defined in the child view's
//                                layout? Note that MultiViewPager "
//                                + "only measures the child for index 0.");
//            }
//            int childWidth = match.getMeasuredWidth();
//            // Check that the measurement was successful
//            if (childWidth > 0) {
//                mNeedsMeasurePage = false;
//                int difference = pageWidth - childWidth;
//                setPageMargin(-difference - ViewUtilsKt.getDp(32));
//                int offscreen = (int) Math.ceil((float) pageWidth / (float)
//                childWidth) + 1;
//                setOffscreenPageLimit(offscreen);
//                requestLayout();
//            }
//        }
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        // Schedule a new measurement pass as the dimensions have changed
//        mNeedsMeasurePage = true;
//    }
//
//    /**
//     * Sets the child view inside a page to match the page size against.
//     *
//     * @param matchChildWidthResId the child id
//     */
//    public void setMatchChildWidth(int matchChildWidthResId) {
//        if (mMatchWidthChildResId != matchChildWidthResId) {
//            mMatchWidthChildResId = matchChildWidthResId;
//            mNeedsMeasurePage = true;
//        }
//    }
//
//    /**
//     * Sets the maximum size.
//     *
//     * @param width in pixels
//     */
//    public void setMaxWidth(int width) {
//        mMaxWidth = width;
//    }
//
//    /**
//     * Sets the maximum size.
//     *
//     * @param height in pixels
//     */
//    public void setMaxHeight(int height) {
//        mMaxHeight = height;
//    }
//
//}