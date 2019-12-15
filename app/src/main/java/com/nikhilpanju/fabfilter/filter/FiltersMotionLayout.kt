package com.nikhilpanju.fabfilter.filter

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.main.MainActivity
import com.nikhilpanju.fabfilter.main.animationPlaybackSpeed
import com.nikhilpanju.fabfilter.utils.MultiListenerMotionLayout
import com.nikhilpanju.fabfilter.utils.bindView
import kotlinx.coroutines.launch

/**
 * A MotionLayout version of [FiltersLayout]
 * All Transitions and ConstraintSets are defined in R.xml.scene_filter
 *
 * Code in this class contains mostly only choreographing the transitions.
 */
class FiltersMotionLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MultiListenerMotionLayout(context, attrs, defStyleAttr) {

    private val tabsRecyclerView: NoScrollRecyclerView by bindView(R.id.tabs_recycler_view)
    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
    private val closeIcon: ImageView by bindView(R.id.close_icon)
    private val filterIcon: ImageView by bindView(R.id.filter_icon)
    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)

    /** Store all the transition durations to be able to update them dynamically later */
    private val durationsMap: Map<MotionScene.Transition, Int> = definedTransitions.associateWith { it.duration }
    private val tabsHandler: ViewPagerTabsHandler by lazy {
        ViewPagerTabsHandler(viewPager, tabsRecyclerView, bottomBarCardView)
    }

    init {
        inflate(context, R.layout.layout_filter_motion, this)
        tabsHandler.init()
        updateDurations()
        enableClicks()
    }

    private fun enableClicks() = when (currentState) {
        R.id.original_set -> {
            filterIcon.setOnClickListener { openSheet() }
            closeIcon.setOnClickListener(null)
        }
        R.id.settle_set -> {
            filterIcon.setOnClickListener { onFilterApplied() }
            closeIcon.setOnClickListener { closeSheet() }
        }
        R.id.original_filtered_set -> {
            closeIcon.setOnClickListener { unFilterAdapterItems() }
            filterIcon.setOnClickListener(null)
        }
        else -> {
            throw IllegalAccessException("Can be called only for the permitted 3 currentStates")
        }
    }

    private fun disableClicks() {
        filterIcon.setOnClickListener(null)
        closeIcon.setOnClickListener(null)
    }

    private fun openSheet() {
        launchInScopeAndHandleClicks {
            tabsHandler.setAdapters(true)

            startScaleDownAnimator(true)

            transitionToState(R.id.path_set)
            awaitTransitionComplete(R.id.path_set)

            transitionToState(R.id.reveal_set)
            awaitTransitionComplete(R.id.reveal_set)

            transitionToState(R.id.settle_set)
            awaitTransitionComplete(R.id.settle_set)
        }
    }

    private fun closeSheet() {
        launchInScopeAndHandleClicks {
            setTransition(R.id.settle_transition)
            transitionToStart()
            awaitTransitionComplete(R.id.reveal_set)

            setTransition(R.id.reveal_transition)
            transitionToStart()
            awaitTransitionComplete(R.id.path_set)
            tabsHandler.setAdapters(false)

            setTransition(R.id.path_transition)
            startScaleDownAnimator(false)
            transitionToStart()
            awaitTransitionComplete(R.id.original_set)
        }
    }

    private fun onFilterApplied() {
        if (!tabsHandler.hasActiveFilters) return

        launchInScopeAndHandleClicks {
            transitionToState(R.id.collapse_set)
            awaitTransitionComplete(R.id.collapse_set)
            (context as MainActivity).isAdapterFiltered = true

            awaitTransitionComplete(R.id.collapse_loading_set)

            startScaleDownAnimator(false)

            awaitTransitionComplete(R.id.original_filtered_set)
            tabsHandler.setAdapters(false)
        }
    }

    private fun unFilterAdapterItems() {
        launchInScopeAndHandleClicks {
            startScaleDownAnimator(true)
            transitionToState(R.id.fab_inset_set)

            awaitTransitionComplete(R.id.fab_inset_set)

            (context as MainActivity).isAdapterFiltered = false

            awaitTransitionComplete(R.id.fab_inset_loading_set)
            startScaleDownAnimator(false)

            awaitTransitionComplete(R.id.fab_outset_set)
            setTransition(R.id.path_transition)
            progress = 0f
        }
    }

    /**
     * Convenience method to launch a coroutine in MainActivity's lifecycleScope (to start animating
     * transitions in MotionLayout).
     * Note: block() must contain only animation related code. Clicks are disabled at start and enabled
     * at the end.
     */
    private inline fun launchInScopeAndHandleClicks(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            disableClicks()
            block()
            enableClicks()
        }
    }

    fun updateDurations() = definedTransitions.forEach {
        it.duration = (durationsMap[it]!! / animationPlaybackSpeed).toInt()
    }

    private fun startScaleDownAnimator(isScaledDown: Boolean) =
            (context as MainActivity)
                    .getAdapterScaleDownAnimator(isScaledDown)
                    .apply { duration = getTransition(R.id.path_transition).duration.toLong() }
                    .start()
}