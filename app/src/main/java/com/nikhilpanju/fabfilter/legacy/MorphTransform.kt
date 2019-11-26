///*
// * Copyright 2015 Google Inc.
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
package com.nikhilpanju.fabfilter.legacy

//
//import android.animation.Animator
//import android.animation.AnimatorSet
//import android.animation.ValueAnimator
//import android.os.Build
//import androidx.annotation.RequiresApi
//import android.transition.ChangeBounds
//import android.transition.TransitionValues
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AccelerateInterpolator
//import android.view.animation.DecelerateInterpolator
//import com.nikhilpanju.fabfilter.utils.ArcAnimator.ArcAnimator
//import com.nikhilpanju.fabfilter.utils.ArcAnimator.Side
//import com.nikhilpanju.fabfilter.R
//import com.nikhilpanju.fabfilter.utils.*
//
//
///**
// * An extension to [ChangeBounds] that also morphs the views background (color & corner
// * radius).
// */
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//class MorphTransform() : ChangeBounds() {
//
//    private var fabSize: Float = -1f
//    private var fabRadius: Float = -1f
//    private var fabElevation: Float = -1f
//    private var fabX: Float = -1f
//    private var fabY: Float = -1f
//    private var sheetWidth: Float = -1f
//    private var sheetHeight: Float = -1f
//    private var sheetRadius: Float = -1f
//    private var sheetElevation: Float = -1f
//    private var windowHeight: Float = -1f
//    private lateinit var fab: androidx.cardview.widget.CardView
//    private lateinit var fabIcon: View
//
//    private val pathAnimationDuration: Long = (200 / DURATION_SCALE).toLong()
//    private val revealAnimationDuration: Long = (200 / DURATION_SCALE).toLong()
//    private val radiusAnimationDuration: Long = (100 / DURATION_SCALE).toLong()
//
//
//    init {
////        duration = DEFAULT_DURATION
//    }
//
//    override fun captureStartValues(transitionValues: TransitionValues) {
//        super.captureStartValues(transitionValues)
//        val view = transitionValues.view as androidx.cardview.widget.CardView
//
//        if (view.isLaidOut || view.width != 0 || view.height != 0) {
//            transitionValues.values.put("FAB_SIZE", view.width)
//            transitionValues.values.put("FAB_RADIUS", view.radius)
//            transitionValues.values.put("FAB_ELEVATION", view.elevation)
//            transitionValues.values.put("FAB_X", view.x)
//            transitionValues.values.put("FAB_Y", view.y)
//        }
//    }
//
//    override fun captureEndValues(transitionValues: TransitionValues) {
//        super.captureEndValues(transitionValues)
//        val view = transitionValues.view as androidx.cardview.widget.CardView
//
//        if (view.isLaidOut || view.width != 0 || view.height != 0) {
//            transitionValues.values.put("SHEET_WIDTH", view.width)
//            transitionValues.values.put("SHEET_HEIGHT", view.height)
//            transitionValues.values.put("SHEET_RADIUS", view.radius)
//            transitionValues.values.put("SHEET_ELEVATION", view.elevation)
//            transitionValues.values.put("FAB_ICON_ID", view.findViewById(R.id.fab_icon) ?:
//                    throw IllegalStateException("CardView must have child with it R.id.fab_icon"))
//        }
//    }
//
//
//    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues, endValues: TransitionValues): Animator? {
//        val changeBounds = super.createAnimator(sceneRoot, startValues, endValues) ?: return null
//
//        fab = endValues.view as androidx.cardview.widget.CardView
//        fabIcon = fab.findViewById(endValues.values["FAB_ICON_ID"] as Int)
//
//        fabSize = startValues.values["FAB_SIZE"] as Float
//        fabRadius = startValues.values["FAB_RADIUS"] as Float
//        fabElevation = startValues.values["FAB_ELEVATION"] as Float
//        fabX = startValues.values["FAB_X"] as Float
//        fabY = startValues.values["FAB_Y"] as Float
//        sheetWidth = endValues.values["SHEET_WIDTH"] as Float
//        sheetHeight = endValues.values["SHEET_HEIGHT"] as Float
//        sheetRadius = endValues.values["SHEET_RADIUS"] as Float
//        sheetElevation = endValues.values["SHEET_ELEVATION"] as Float
//        // screen height - status bar height
//        windowHeight = (fab.context.screenHeight - 16.dp).toFloat()
//
//
//        val set = AnimatorSet()
//        set.play(getPathAnimator(true)).with(changeBounds).before(getRevealAnimator(true))
//        return set
//    }
//
//    private fun getPathAnimator(open: Boolean): AnimatorSet {
//        val fromX = fabX + fabSize / 2
//        val fromY = fabY + fabSize / 2
//        val endX = (sheetWidth / 2)
//        val endY = (windowHeight - sheetHeight / 2)
//
//
//        val arcAnimator =
//                if (open)
//                    ArcAnimator.createArcAnimator(fab, fromX, fromY, endX, endY, 90f, Side.LEFT)
//                else
//                    ArcAnimator.createArcAnimator(fab, endX, endY, fromX, fromY, 90f, Side.LEFT)
//
//        /* arcAnimator.addListener(object : AnimatorListenerAdapter() {
//             override fun onAnimationEnd(animation: Animator?) {
//                 getRevealAnimator(true)
//             }
//         })*/
//
//        val elevationAnimator =
//                if (open)
//                    ValueAnimator.ofFloat(fabElevation, sheetElevation)
//                else
//                    ValueAnimator.ofFloat(sheetElevation, fabElevation)
//
//        elevationAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//
//            fab.cardElevation = value
////                fab.requestLayout()
//        }
//
////        return arrayOf(arcAnimator, elevationAnimator)
//
//        val animatorSet = AnimatorSet()
//        animatorSet.playTogether(arcAnimator, elevationAnimator)
//        animatorSet.duration = pathAnimationDuration
//        animatorSet.interpolator = DecelerateInterpolator()
//        return animatorSet
//    }
//
//    private fun getRevealAnimator(open: Boolean): AnimatorSet {
//        fab.setPivotToCenter()
//        val location = IntArray(2)
//        val fabIcon: View = fab.findViewById(R.id.fab_icon)
//        fabIcon.getLocationOnScreen(location)
//        val diff = windowHeight - location[1] - 16.dp
//
//        val sizeAnimator =
//                if (open)
//                    ValueAnimator.ofFloat(0f, 1f)
//                else
//                    ValueAnimator.ofFloat(1f, 0f)
//
//        sizeAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//
//            val fabHeight = (fabSize + (sheetHeight - fabSize) * value)
//            val fabWidth = (fabSize + (sheetWidth - fabSize) * value)
//
//            fab.layoutParams.height = fabHeight.toInt()
//            fab.layoutParams.width = fabWidth.toInt()
//            // to keep the cardView as a circle:
//            fab.radius = fabWidth / 2
//            fab.x = (sheetWidth / 2) - (fabWidth / 2)
//            fab.y = (windowHeight - sheetHeight / 2) - fabHeight / 2
//
//            fabIcon.translationY = value * diff
//            fab.requestLayout()
//        }
//        sizeAnimator.interpolator = AccelerateInterpolator(1.5f)
//        sizeAnimator.duration = revealAnimationDuration
//
//
//        val edgeRadiusAnimator =
//                if (open)
//                    ValueAnimator.ofFloat(sheetWidth / 2, 0f)
//                else
//                    ValueAnimator.ofFloat(0f, sheetWidth / 2)
//
//        edgeRadiusAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            fab.radius = value
//        }
//        edgeRadiusAnimator.interpolator = DecelerateInterpolator(1.5f)
//        edgeRadiusAnimator.duration = radiusAnimationDuration
//
//        // note; we use 2 animators here because as per the animation, the fab first re-sizes as a circle then
//        // the reveal is completed for the corners
//
//        val animatorSet = AnimatorSet()
//        if (open)
//            animatorSet.playSequentially(sizeAnimator, edgeRadiusAnimator)
//        else
//            animatorSet.playSequentially(edgeRadiusAnimator, sizeAnimator)
//
//        return animatorSet
//    }
//
//    companion object {
//        /**
//         * Configure [MorphTransform]s & set as `activity`'s shared element enter and return
//         * transitions.
//         */
//        fun setup(fragment: androidx.fragment.app.Fragment) {
//            val sharedEnter = MorphTransform()
//            // Reverse the start/end params for the return transition
//            val sharedReturn = MorphTransform()
////            sharedEnter.addTarget(target)
////            sharedReturn.addTarget(target)
//
//            fragment.sharedElementEnterTransition = sharedEnter
//            fragment.sharedElementReturnTransition = sharedReturn
//        }
//    }
//}
