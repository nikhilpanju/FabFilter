package com.nikhilpanju.fabfilter.views

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import kotlin.math.min

/**
 * A custom [CardView] that does not allow the radius to exceed the minimum of width/2 or height/2
 * On some versions, this causes a strange behavior so we use this to stay safe.
 */
class CircleCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : CardView(context, attrs, defStyleAttr) {

    override fun setRadius(radius: Float) {
        super.setRadius(
                if (radius > height / 2 || radius > width / 2) min(height, width) / 2f
                else radius
        )
    }
}