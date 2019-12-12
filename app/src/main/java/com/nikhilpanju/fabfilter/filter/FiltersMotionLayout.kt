package com.nikhilpanju.fabfilter.filter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.main.MainActivity
import com.nikhilpanju.fabfilter.utils.MultiListenerMotionLayout
import com.nikhilpanju.fabfilter.utils.bindColor
import com.nikhilpanju.fabfilter.utils.bindDimen
import com.nikhilpanju.fabfilter.utils.bindView
import kotlinx.coroutines.launch

/**
 * A MotionLayout version of [FiltersLayout]
 * ViewPager and Tabs related stuff are identical (
 */
@SuppressLint("WrongConstant")
class FiltersMotionLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MultiListenerMotionLayout(context, attrs, defStyleAttr) {

    private val fab: View by bindView(R.id.fab)
    private val tabsRecyclerView: NoScrollRecyclerView by bindView(R.id.tabs_recycler_view)
    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
    private val closeIcon: ImageView by bindView(R.id.close_icon)
    private val filterIcon: ImageView by bindView(R.id.filter_icon)
    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)

    private val tabsHandler: ViewPagerTabsHandler by lazy {
        ViewPagerTabsHandler(viewPager, tabsRecyclerView, bottomBarCardView)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors & Dimens
    ///////////////////////////////////////////////////////////////////////////

    private val bottomBarColor: Int by bindColor(R.color.bottom_bar_color)
    private val bottomBarPinkColor: Int by bindColor(R.color.colorAccent)
    private val tabColor: Int by bindColor(R.color.tab_unselected_color)
    private val tabSelectedColor: Int by bindColor(R.color.tab_selected_color)

    private val tabItemWidth: Float by bindDimen(R.dimen.tab_item_width)
    private val filterLayoutPadding: Float by bindDimen(R.dimen.filter_layout_padding)

    private lateinit var tabsAdapter: FiltersTabsAdapter
    private var totalTabsScroll = 0
    private var hasActiveFilters = false

    init {
        inflate(context, R.layout.layout_filter_motion, this)
        tabsHandler.init()

        filterIcon.setOnClickListener {
            (context as MainActivity).lifecycleScope.launch {
                tabsHandler.setAdapters(true)

                getScaleDownAnimator(true).start()

                transitionToState(R.id.path_set)
                awaitTransitionComplete(R.id.path_set)

                transitionToState(R.id.reveal_set)
                awaitTransitionComplete(R.id.reveal_set)

                transitionToState(R.id.settle_set)

                // TODO change
                filterIcon.setOnClickListener {
                    if (tabsHandler.hasActiveFilters) {
                        context.lifecycleScope.launch {
                            transitionToState(R.id.collapse_set)
                            awaitTransitionComplete(R.id.close_icon_loading_set)

                            getScaleDownAnimator(false).start()

                            awaitTransitionComplete(R.id.original_filtered_set)
                            tabsHandler.setAdapters(false)
                        }
                    }
                }
            }
        }
        closeIcon.setOnClickListener {
            (context as MainActivity).lifecycleScope.launch {
                setTransition(R.id.settle_transition)
                transitionToStart()
                awaitTransitionComplete(R.id.reveal_set)

                setTransition(R.id.reveal_transition)
                transitionToStart()
                awaitTransitionComplete(R.id.path_set)
                tabsHandler.setAdapters(false)

                getScaleDownAnimator(false).start()

                setTransition(R.id.path_transition)
                transitionToStart()
            }
        }
    }

    private fun getScaleDownAnimator(isScaledDown: Boolean) =
            (context as MainActivity).getAdapterScaleDownAnimator(isScaledDown).apply {
                duration = getTransition(R.id.path_transition).duration.toLong()
            }
}