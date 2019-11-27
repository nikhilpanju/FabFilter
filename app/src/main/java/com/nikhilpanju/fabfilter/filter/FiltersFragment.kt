package com.nikhilpanju.fabfilter.filter

import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.main.animationPlaybackSpeed
import com.nikhilpanju.fabfilter.main.MainActivity
import com.nikhilpanju.fabfilter.utils.*

class FiltersFragment : Fragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Views
    ///////////////////////////////////////////////////////////////////////////

    private val fab: CardView by bindView(R.id.fab)
    private val fabFilterIcon: ImageView by bindView(R.id.fab_filter_icon)
    private val fabCloseIcon: ImageView by bindView(R.id.fab_close_icon)
    private val mainContainer: View by bindView(R.id.main_container)
    private val filtersContainer: View by bindView(R.id.filters_container)
    private val tabsContainer: View by bindView(R.id.tabs_container)
    private val tabsRecyclerView: NoScrollRecyclerView by bindView(R.id.tabs_recycler_view)
    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)
    private val bottomBarContainer: View by bindView(R.id.bottom_bar_container)
    private val closeIcon: View by bindView(R.id.close_icon)
    private val filterIcon: ImageView by bindView(R.id.filter_icon)

    ///////////////////////////////////////////////////////////////////////////
    // Colors & Dimens
    ///////////////////////////////////////////////////////////////////////////

    private val bottomBarColor: Int by bindColor(R.color.bottom_bar_color)
    private val bottomBarPinkColor: Int by bindColor(R.color.colorAccent)
    private val bgColor: Int by bindColor(R.color.colorPrimaryDark)
    private val filterIconColor: Int by bindColor(R.color.filter_icon_color)
    private val filterIconActiveColor: Int by bindColor(R.color.filter_icon_active_color)
    private val tabColor: Int by bindColor(R.color.tab_unselected_color)
    private val tabSelectedColor: Int by bindColor(R.color.tab_selected_color)

    private val fabSize: Float by bindDimen(R.dimen.fab_size)
    private val fabSizeInset: Float by bindDimen(R.dimen.fab_size_inset)
    private val fabElevation: Float by bindDimen(R.dimen.fab_elevation)
    private val fabElevation2: Float by bindDimen(R.dimen.fab_elevation_2)
    private val fabPressedElevation: Float by bindDimen(R.dimen.fab_pressed_elevation)
    private val fabMargin: Float by bindDimen(R.dimen.fab_margin)
    private val tabsHeight: Float by bindDimen(R.dimen.tabs_height)
    private val tabItemWidth: Float by bindDimen(R.dimen.tab_item_width)
    private val filterLayoutPadding: Float by bindDimen(R.dimen.filter_layout_padding)
    private val sheetWidth: Float by lazy { screenWidth.toFloat() }
    private val sheetHeight: Float by lazy { screenWidth.toFloat() }
    private val fragmentHeight: Int by lazy { mainContainer.height }

    private val fabX: Float by lazy { screenWidth - fabSize - fabMargin }
    private val fabY: Float by lazy { fragmentHeight - fabSize - fabMargin }
    private val fabX2: Float by lazy { (sheetWidth - fabSize) / 2 }
    private val fabY2: Float by lazy { fragmentHeight - sheetHeight / 2 - fabSize / 2 }
    private val bottomBarTranslateAmount: Float by lazy { screenWidth / 4f }
    private val viewPagerTranslateAmount = 32f.dp

    private lateinit var tabsAdapter: FiltersTabsAdapter
    private var totalTabsScroll = 0
    private var hasActiveFilters = false

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_filter, container, false)

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainContainer.doOnGlobalLayout {
            fab.x = fabX
            fab.y = fabY
        }

        // sheetHeight is dynamic (screenWidth) so we set it here instead of in XML
        viewPager.layoutParams.height = sheetHeight.toInt()

        // Click Listeners
        closeIcon.setOnClickListener { openFilterSheet(false) }
        filterIcon.setOnClickListener { onFilterApplied() }
        fab.setOnClickListener {
            if ((activity as MainActivity).isAdapterFiltered) {
                unFilterAdapterWithFabAnimation()
            } else {
                // We're opening the fab so set the adapters and open filter sheet
                setAdapters(true)
                openFilterSheet(true)
            }
        }

        // ViewPager & Tabs
        viewPager.offscreenPageLimit = numTabs
        tabsRecyclerView.updatePadding(right = (screenWidth - tabItemWidth - filterLayoutPadding).toInt())
        tabsRecyclerView.layoutManager = NoScrollHorizontalLayoutManager(context!!)

        // Sync Tabs And Pager
        tabsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                totalTabsScroll += dx
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Scroll tabs as viewpager is scrolled
                val x = (position + positionOffset) * tabItemWidth - totalTabsScroll
                tabsRecyclerView.scrollBy(x.toInt(), 0)

                // This acts like a page transformer for tabsRecyclerView. Ideally we should do this in the
                // onScrollListener for the RecyclerView but that requires extra math but positionOffset
                // is all we need so let's use that to apply transformation to the tabs

                val currentTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position)!!
                val nextTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position + 1)

                val defaultScale: Float = FiltersTabsAdapter.defaultScale
                val maxScale: Float = FiltersTabsAdapter.maxScale

                currentTabView.setPivotToCenter()
                nextTabView?.setPivotToCenter()

                currentTabView.setScale(defaultScale + (1 - positionOffset) * (maxScale - defaultScale))
                nextTabView?.setScale(defaultScale + positionOffset * (maxScale - defaultScale))

                currentTabView.findViewById<View>(R.id.tab_pill).backgroundTintList =
                        ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, 1 - positionOffset))
                nextTabView?.findViewById<View>(R.id.tab_pill)?.backgroundTintList =
                        ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, positionOffset))
            }
        })
    }

    /**
     * Callback method for FiltersPagerAdapter. When ever a filter is selected, adapter will call this function.
     * Animates the bottom bar to pink if there are any active filters and vice versa
     */
    private fun onFilterSelected(updatedPosition: Int, selectedMap: Map<Int, List<Int>>) {
        val hasActiveFilters = selectedMap.filterValues { it.isNotEmpty() }.isNotEmpty()
        val bottomBarAnimator =
                if (hasActiveFilters && !this.hasActiveFilters) ValueAnimator.ofFloat(0f, 1f)
                else if (!hasActiveFilters && this.hasActiveFilters) ValueAnimator.ofFloat(1f, 0f)
                else null

        tabsAdapter.updateBadge(updatedPosition, !selectedMap[updatedPosition].isNullOrEmpty())

        bottomBarAnimator?.let {
            this.hasActiveFilters = !this.hasActiveFilters
            it.addUpdateListener { animation ->
                val color = blendColors(bottomBarColor, bottomBarPinkColor, animation.animatedValue as Float)
                bottomBarCardView.setCardBackgroundColor(color)
            }
            it.duration = toggleDuration
            it.start()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Animation Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called when fab is clicked to open filter sheet (open = true)
     * or called when closeIcon is clicked to close the filter sheet (open = false)
     *
     * It's a 3 step animation played in sequence. Read the comments to know more.
     */
    private fun openFilterSheet(open: Boolean) {
        // 1) Fab Path Animator
        // Animates path of the fab from starting position to centre of screen

        val pathAnimator = getPathAnimator(open)

        // 2) Reveal Animator
        // Fab expands outwards to reveal the filter layout. It's just a trick.
        // After expanding, the mainContainer is made visible (it has higher elevation so
        // that it sits on top of the fab). To the user, it looks seamless
        // 0f -> 0.8f: Increase size of fab 0.8f -> 1f: Un-curve corners of fab

        val prop1 = CircleCardViewAnimator(
                fab, startSize = fabSize, endSize = sheetWidth, startX = fabX2, startY = fabY2)
        val prop2 = CardViewAnimator(fab, startRadius = sheetWidth / 2, endRadius = 0f)
        val revealAnimator = getValueAnimator(
                open, revealAnimationDuration, AccelerateDecelerateInterpolator()) { progress ->

            if (progress <= 0.8f) {
                prop1.progress = progress / 0.8f
            } else {
                if (prop1.progress != 1f) prop1.progress = 1f
                prop2.progress = (progress - 0.8f) / 0.2f
            }
        }

        // mainContainer contains all the filter sheet views. So when revealing or un-revealing
        // we need to show/hide the mainContainer because the fab is behind (elevation) the mainContainer
        // and we want to seamlessly transition from the fab to the mainContainer
        if (open) revealAnimator.doOnEnd { mainContainer.isVisible = true }
        else revealAnimator.doOnStart { mainContainer.isVisible = false }


        // 3) Settle Animator - All the elements of the filter screen settle into place
        // Since mainContainer sits on top of the fab, we can fade in all the elements of mainContainer
        // and the bottom bar

        val settleAnimator = getValueAnimator(open, fragmentOpenAnimDuration,
                if (open) DecelerateInterpolator() as TimeInterpolator else AccelerateInterpolator()) { progress ->

            // 3a) Bottom bar bg is faded in for it to stand out
            bottomBarCardView.setCardBackgroundColor(
                    blendColors(bgColor, if (hasActiveFilters) bottomBarPinkColor else bottomBarColor, progress))

            // 3b) Bottom bar slides in from left
            bottomBarContainer.translationX = -bottomBarTranslateAmount * (1 - progress)

            // 3c) Filter icon fades to white as it settles
            filterIcon.setColorFilter(blendColors(filterIconColor, filterIconActiveColor, progress))

            // 3d) Close icon fades in
            closeIcon.alpha = progress

            // 3e) Tabs slide up (Increasing height of filterContainer and tabsContainer, translating the
            //    tabsRecyclerView up to make it look more natural)
            val height = tabsHeight * progress
            filtersContainer.layoutParams.height = (sheetHeight + height).toInt()
            tabsContainer.layoutParams.height = height.toInt()
            tabsRecyclerView.translationY = tabsHeight * (1 - progress)
            filtersContainer.requestLayout()

            // 3f) Sliding up the ViewPager
            viewPager.translationY = (1 - progress) * viewPagerTranslateAmount
            viewPager.alpha = progress
        }

        // Animator Set choreographs all 3 animations based on the `open` variable.
        // If it's closing, we tell the activity, it's closed so that it can show its fab and remove
        // this fragment

        val set = AnimatorSet()
        if (open) {
            set.playSequentially(pathAnimator, revealAnimator, settleAnimator)
        } else {
            set.playSequentially(settleAnimator, revealAnimator, pathAnimator)

            // Remove adapters after filter sheet is closed
            set.doOnEnd { setAdapters(false) }
        }
        set.start()
    }

    /**
     * Callback for when filter is applied.
     * It's a choreography of 5 animations. Read the comments to know more.
     */
    private fun onFilterApplied() {
        if (!hasActiveFilters) return

        // 1) Fab anticipate animator
        // The fab which is hidden behind the container is made visible and collapses down
        // into the size of fab from MainActivity using AnticipateInterpolator

        fab.isVisible = true
        fabFilterIcon.isVisible = false
        val size = screenWidth * 1.5f
        val fabAnticipateAnimator = CircleCardViewAnimator(
                fab, startSize = size, endSize = fabSize,
                startX = (screenWidth - size) / 2, startY = fragmentHeight - (sheetHeight + size) / 2,
                duration = filterAnimDuration, interpolator = AnticipateInterpolator(1.7f)
        ).getAnimator()

        // 2) Bottom bar Inset Animator
        // The bottom bar collapses into the fab while centering the close button

        val delta = (fabSize - fabSizeInset) / 2
        val insetAnimator = CardViewAnimator(
                bottomBarCardView, endWidth = fabSizeInset, endHeight = fabSizeInset,
                endX = fabX2 + delta, endY = fabY2 + delta, endRadius = fabSizeInset / 2,
                duration = filterAnimDuration * 75 / 100, interpolator = DecelerateInterpolator(1.3f)
        ).getAnimator { progress ->

            // As the bottom bar collapses, we move the close icon to the centre of the inset fab
            val parent = closeIcon.parent as View
            parent.x = (bottomBarCardView.layoutParams.width - parent.width) / 2 * progress
        }

        // 3) Filter container alpha and scale animator
        // All the elements of filterContainer is scaled down and faded out and
        // the filter icon is also faded out

        val alphaAnimator = getValueAnimator(
                true, filterAnimDuration * 20 / 100, AccelerateInterpolator()) { progress ->

            filterIcon.alpha = 1 - progress
            filtersContainer.alpha = 1 - progress

            // scales from 1f -> 0.8f
            filtersContainer.setScale(1 - (0.2f * progress))
        }

        // 4) Close Icon rotate loading animator
        // Close icon rotates to simulate loading while adapter items in MainActivity are filtered

        val closeIconLoadingAnimator = getCloseIconAnimator()
        closeIconLoadingAnimator.doOnStart {
            fabCloseIcon.isVisible = true
            fabCloseIcon.alpha = 1f
            mainContainer.isInvisible = true
            (activity as MainActivity).isAdapterFiltered = true
        }

        // 5) Path Animator to animate path of fab back to original position
        val pathAnimator = getPathAnimator(false)

        // AnimatorSet choreography of the animation: (1,2,3) -> 4 -> 5
        // At the end of the animation, MainActivity shows it's fab. removes this fragment and we
        // hide this fab so that there is no "jerkiness" in animation (caused by fab shadows)
        // while fragment is being removed

        val animSet = AnimatorSet()
        animSet.play(fabAnticipateAnimator).with(insetAnimator).with(alphaAnimator).before(closeIconLoadingAnimator)
        animSet.play(closeIconLoadingAnimator).before(pathAnimator)
        animSet.doOnEnd {

            // Reset everything after animation is done. When opening the next time, there shouldn't be an issue
            setAdapters(false)
            insetAnimator.currentPlayTime = 0
            alphaAnimator.currentPlayTime = 0
            bottomBarCardView.translationY += delta * 2
        }
        animSet.start()
    }

    /**
     * Called when fab (with cross) is clicked to remove the filters
     * This animates the fab in a 3-step animation whilst removing filters from the adapter
     */
    private fun unFilterAdapterWithFabAnimation() {
        fabFilterIcon.isVisible = true


        val insetProp = CircleCardViewAnimator(fab,
                startSize = fabSize, endSize = fabSizeInset,
                startX = fabX, startY = fabY,
                startElevation = fabElevation, endElevation = fabPressedElevation)

        // 1) Inset Animator - Closes the fab into the cross
        val insetAnimator = insetProp.getAnimator(true)
        insetAnimator.duration = fabInsetDuration
        insetAnimator.interpolator = AccelerateInterpolator()

        // 2) Close Icon Animator - Rotates cross icon to simulate loading
        val closeIconLoadingAnimator = getCloseIconAnimator()
        // Also filter adapter items when animation starts
        closeIconLoadingAnimator.doOnStart { (activity as MainActivity).isAdapterFiltered = false }

        // 3) Outset Animator - Pops the fab back out and hides the cross to simulate a reset-ed state
        val outsetAnimator = insetProp.getAnimator(false) { progress ->
            fabCloseIcon.alpha = progress * 2 - 1
        }
        outsetAnimator.duration = fabInsetDuration
        outsetAnimator.interpolator = OvershootInterpolator()

        // Play all 3 sequentially using AnimatorSet
        val set = AnimatorSet()
        set.playSequentially(insetAnimator, closeIconLoadingAnimator, outsetAnimator)
        set.start()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Convenience Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Convenience method for getting path animator since it's used in a couple of places.
     * It animates the fab from it's resting position in an arc like motion to the centre
     * of the the FilterFragment sheet.
     *
     * ListAdapter items shrink when the fab opens and un-shrinks when fab closes so on start
     * of animation, we ask MainListAdapter from MainActivity to start it's animations as well.
     */
    private fun getPathAnimator(open: Boolean): ValueAnimator {
        val a = CardViewAnimator(
                fab, startX = fabX, startY = fabY, endX = fabX2, endY = fabY2,
                startElevation = fabElevation, endElevation = fabElevation2, isArcPath = true,
                duration = pathAnimDuration, interpolator = getPathAnimationInterpolator(open)
        ).getAnimator(open)
        a.doOnStart { (activity as MainActivity).animateMainListAdapter(open) }
        return a
    }

    /**
     * Convenience methods for getting close icon rotate animator.
     * Used in a couple of places so abstracted out into a function for safety
     */
    private fun getCloseIconAnimator() = getValueAnimator(
            true, closeIconRotationDuration, DecelerateInterpolator()) { progress ->
        fabCloseIcon.rotation = 270 * progress
    }

    /**
     * Used to set tab and view pager adapters and remove them when unnecessary.
     * This is done because keeping the adapters around when fab is never clicked is wasteful.
     */
    private fun setAdapters(set: Boolean) {
        if (set) {
            viewPager.adapter = FiltersPagerAdapter(context!!, ::onFilterSelected)

            // Tabs
            tabsAdapter = FiltersTabsAdapter(context!!) { clickedPosition ->
                // smoothScroll = true will call the onPageScrolled callback which will smoothly
                // animate (transform) the tabs accordingly
                viewPager.setCurrentItem(clickedPosition, true)
            }
            tabsRecyclerView.adapter = tabsAdapter
        } else {
            viewPager.adapter = null
            tabsRecyclerView.adapter = null
            hasActiveFilters = false
        }
    }

    companion object {
        val pathAnimDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
        val revealAnimationDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
        val closeIconRotationDuration: Long get() = (600L / animationPlaybackSpeed).toLong()
        val fragmentOpenAnimDuration: Long get() = (200L / animationPlaybackSpeed).toLong()
        val filterAnimDuration: Long get() = (350L / animationPlaybackSpeed).toLong()
        val fabInsetDuration: Long get() = (250L / animationPlaybackSpeed).toLong()
        val toggleDuration: Long get() = 100L

        const val numTabs = 5

        @Suppress("USELESS_CAST")
        fun getPathAnimationInterpolator(open: Boolean) =
                (if (open) DecelerateInterpolator() as TimeInterpolator else AccelerateInterpolator())
    }
}