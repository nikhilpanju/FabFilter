package com.nikhilpanju.fabfilter.main

import android.view.View
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.roundToInt

class OnToolbarOffsetChangedListener(
        private val toolbar: View,
        private val toolbarTitle: View,
        private val drawerIcon: View,
        /**
         * [placeHolderToolbar] is a dummy invisible view to make sure that whole toolbar will not
         * be hidden in scroll up
         */
        private val placeHolderToolbar: View
) : AppBarLayout.OnOffsetChangedListener {
    companion object {
        private const val SCALE = 0.6F
    }

    private var toolbarCollapsedHeight: Int = -1
    private var toolbarOriginalHeight: Int = -1

    private var initialized: Boolean = false

    init {
        /**
         * Post a function that set calculated collapsed size on [placeHolderToolbar]
         */
        placeHolderToolbar.post {
            initToolbarSize()

            placeHolderToolbar.layoutParams.height = toolbarCollapsedHeight
            placeHolderToolbar.requestLayout()
        }

    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout == null || appBarLayout.totalScrollRange <= 0) {
            // views are not initialized yet
            return
        }

        /**
         * Calculate Toolbar collapse offset from 1 (Expanded) to 0 (fully collapsed)
         */
        updateToolbarView(
                (appBarLayout.totalScrollRange + verticalOffset).toFloat()
                        / appBarLayout.totalScrollRange
        )
    }

    /**
     * Tales [offset] and calculates related toolbar height and drawer icon translation
     * and toolbarTitle scale
     *
     * @param [offset] Toolbar collapse offset from 1 (Expanded) to 0 (fully collapsed)
     */
    private fun updateToolbarView(
            offset: Float
    ) {
        initToolbarSize()
        val newHeight = toolbarCollapsedHeight + (offset * (toolbarOriginalHeight - toolbarCollapsedHeight)).roundToInt()
        if (newHeight != toolbar.layoutParams.height) {
            toolbar.layoutParams.height = newHeight
            toolbar.requestLayout()
        }

        // Hamburger menu transition
        drawerIcon.translationY = -((1 - offset) * toolbarOriginalHeight)

        /**
         * It will map [0, 1] range to [0.6, 1] range
         * Title scale should not be less than 0.6
         */
        val ratio = SCALE + (offset * (1 - SCALE))

        // Title scale
        toolbarTitle.scaleX = ratio
        toolbarTitle.scaleY = toolbarTitle.scaleX
    }

    /**
     * Set the required view variables. Only will be accessed once because of the [initialized] variable.
     */
    private fun initToolbarSize() {
        if (initialized) return
        initialized = true

        toolbarOriginalHeight = toolbar.layoutParams.height
        toolbarCollapsedHeight = (toolbarOriginalHeight * SCALE).roundToInt()
    }
}