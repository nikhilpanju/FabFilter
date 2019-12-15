package com.nikhilpanju.fabfilter.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.cardview.widget.CardView
import com.nikhilpanju.fabfilter.utils.ArcAnimator.ArcMetric
import com.nikhilpanju.fabfilter.utils.ArcAnimator.ArcUtils
import com.nikhilpanju.fabfilter.utils.ArcAnimator.Side

/**
 * Helper class to animate circular CardViews (width = height = radius * 2)
 * It maintains the centre point and radius of the circle as it animates
 * It is only capable of animating size and elevation of the circle.
 * For more flexibility use [CardViewAnimatorHelper]
 */
class CircleCardViewAnimatorHelper(cardView: CardView, startSize: Float, endSize: Float,
                                   startX: Float, startY: Float,
                                   startElevation: Float = -1f, endElevation: Float = -1f,
                                   duration: Long = 300L,
                                   interpolator: TimeInterpolator = AccelerateDecelerateInterpolator())
    : CardViewAnimatorHelper(
        cardView, startSize, endSize, startSize, endSize,
        startX = startX, startY = startY,
        endX = startX + (startSize - endSize) / 2, endY = startY + (startSize - endSize) / 2,
        startRadius = startSize / 2, endRadius = endSize / 2,
        startElevation = startElevation, endElevation = endElevation,
        duration = duration, interpolator = interpolator
)

/**
 * Helper class to animate CardView Properties.
 * startValues are automatically detected. Make sure you instantiate this after
 * view layout or supply your own start values
 *
 * isArcPath = true will disregard width and height changes
 */
open class CardViewAnimatorHelper(
        private val cardView: CardView,
        private val startWidth: Float = cardView.width.toFloat(),
        private val endWidth: Float = -1f,
        private val startHeight: Float = cardView.height.toFloat(),
        private val endHeight: Float = -1f,
        private val startX: Float = cardView.x,
        private val startY: Float = cardView.y,
        private val endX: Float = -1f,
        private val endY: Float = -1f,
        private val startRadius: Float = cardView.radius,
        private val endRadius: Float = -1f,
        private val startElevation: Float = cardView.elevation,
        private val endElevation: Float = -1f,
        private val isArcPath: Boolean = false,
        val duration: Long = 300L,
        val interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
) {

    var progress: Float = 0f
        set(value) {
            field = value

            if (!isArcPath) {
                if (endWidth >= 0) cardView.layoutParams.width = getProgressValue(startWidth, endWidth, progress).toInt()
                if (endHeight >= 0) cardView.layoutParams.height = getProgressValue(startHeight, endHeight, progress).toInt()
                if (endWidth >= 0 || endHeight >= 0) cardView.requestLayout()

                if (endX >= 0) cardView.x = getProgressValue(startX, endX, progress)
                if (endY >= 0) cardView.y = getProgressValue(startY, endY, progress)

            } else {
                val arcMetric = ArcMetric.evaluate(startX, startY, endX, endY, 90f, Side.LEFT)
                val degree = arcMetric.getDegree(progress).toDouble()
                cardView.x = arcMetric.axisPoint.x + arcMetric.mRadius * ArcUtils.cos(degree)
                cardView.y = arcMetric.axisPoint.y - arcMetric.mRadius * ArcUtils.sin(degree)
            }
            if (endRadius >= 0) cardView.radius = getProgressValue(startRadius, endRadius, progress)
            if (endElevation >= 0) cardView.cardElevation = getProgressValue(startElevation, endElevation, progress)
        }

    /**
     * Helper method to get a ValueAnimator and set the CardView progress automatically
     */
    fun getAnimator(forward: Boolean = true, updateListener: ((progress: Float) -> Unit)? = null): ValueAnimator {
        val a =
                if (forward) ValueAnimator.ofFloat(0f, 1f)
                else ValueAnimator.ofFloat(1f, 0f)
        a.addUpdateListener {
            val progress = (it.animatedValue as Float)
            this.progress = progress
            updateListener?.invoke(progress)
        }
        a.duration = duration
        a.interpolator = interpolator
        return a
    }

    private fun getProgressValue(start: Float, end: Float, progress: Float): Float =
            start + (end - start) * progress

}
