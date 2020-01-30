package com.nikhilpanju.fabfilter.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A hack-job way to allow a RecyclerView to be scrolled only programmatically and not via touch inputs
 */
class NoScrollRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    override fun scrollBy(x: Int, y: Int) {
        val lm = layoutManager as? NoScrollHorizontalLayoutManager
        lm?.canScrollHorizontally = true
        super.scrollBy(x, y)
        lm?.canScrollHorizontally = false
    }
}

class NoScrollHorizontalLayoutManager(context: Context)
    : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {

    var canScrollHorizontally = false

    override fun canScrollHorizontally(): Boolean = canScrollHorizontally
}