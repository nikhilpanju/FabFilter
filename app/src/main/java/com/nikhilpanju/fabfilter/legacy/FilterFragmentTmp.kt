package com.nikhilpanju.fabfilter.legacy

//
//import com.nikhilpanju.fabfilter.filter.FiltersAdapter
//
//import android.animation.AnimatorSet
//import android.animation.ValueAnimator
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AccelerateInterpolator
//import android.view.animation.DecelerateInterpolator
//import android.widget.ImageView
//import androidx.cardview.widget.CardView
//import androidx.core.animation.doOnEnd
//import androidx.fragment.app.Fragment
//import androidx.viewpager.widget.ViewPager
//import androidx.viewpager2.widget.ViewPager2
//import com.nikhilpanju.fabfilter.FilterFragmentTabsAdapter
//import com.nikhilpanju.fabfilter.main.MainActivity
//import com.nikhilpanju.fabfilter.R
//import com.nikhilpanju.fabfilter.utils.*
//
//
//const val toggleDuration: Long = 100L
//
//class FilterFragment : Fragment() {
//
//    private val numTabs = 5
//
//    //    private lateinit var adapter: FilterFragmentPagerAdapter
//    private lateinit var tabsAdapter: FilterFragmentTabsAdapter
//    private var fragmentPositionWithActiveFilters = MutableList(numTabs) { false }
//    private var isBottomBarPink = false
//
//    private val tabLayout: View by bindView(R.id.tab_layout)
//    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
//    private val cardContainer: View by bindView(R.id.card_container)
//    private val tabsPager: MultiViewPager by bindView(R.id.pager)
//
//    // res/layout/bottom_bar.xml attributes
//    private val bottomBar: View by bindView(R.id.bottom_bar)
//    private val bottomBarContainer: CardView by bindView(R.id.bottom_bar_container)
//    private val closeButton: View by bindView(R.id.close_button)
//    private val filterIcon: ImageView by bindView(R.id.fab_icon)
//
//    private val bottomBarColor: Int by bindColor(R.color.bottom_bar_color)
//    private val bottomBarPinkColor: Int by bindColor(R.color.secondaryAccent)
//    private val bgColor: Int by bindColor(R.color.colorAccent)
//    private val filterIconColor: Int by bindColor(R.color.filter_icon_color)
//    private val filterIconActiveColor: Int by bindColor(R.color.filter_icon_active_color)
//
//    private val sheetPeekHeight: Float by lazy { screenWidth.toFloat() }
//    private val tabsHeight: Float by bindDimen(R.dimen.tabs_height)
//    private val tabItemWidth: Float by bindDimen(R.dimen.tab_item_width)
//    private val bottomBarTranslateAmount: Float by lazy { screenWidth / 4f }
//    private val viewPagerTranslateAmount = 16f.dp
//
//    private val settlingAnimationDuration: Long = (200 / DURATION_SCALE)
//    private val viewPagerAnimationDuration: Long = (150 / DURATION_SCALE)
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
//            inflater.inflate(R.layout.fragment_filter, container, false)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // viewpager
////        adapter = FilterFragmentPagerAdapter(childFragmentManager, numTabs)
//        viewPager.adapter = FiltersAdapter(context!!, ::onFilterApplied)
//        viewPager.offscreenPageLimit = numTabs
//
//        bottomBar.translationX = -bottomBarTranslateAmount
//        viewPager.translationY = viewPagerTranslateAmount
//        viewPager.alpha = 0f
//        (viewPager.parent as View).layoutParams.height = sheetPeekHeight.toInt()
//        animateFragment(true)
//        closeButton.alpha = 0f
//        tabsPager.translationY = tabsHeight
//        closeButton.setOnClickListener { animateFragment(false) }
//
//        // tabs
//        tabsAdapter = FilterFragmentTabsAdapter(childFragmentManager, numTabs)
//        tabsPager.setPageTransformer(false, TabsTransformer(context!!))
//        tabsPager.adapter = tabsAdapter
//
//        synchroniseViewPagers()
//
//        /*viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener(){
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                Log.d(TAG, "position = $position, positionOffset = $positionOffset, positionOffsetPixels = $positionOffsetPixels")
//            }
//        })*/
////        tabsPager.pageMargin = -16.dpToPx(context)
////        tabsPager.offscreenPageLimit = NUM_FRAGS
//    }
//
//    private fun onFilterApplied(updatedPosition: Int, selectedMap: Map<Int, List<Int>>) {
//        logd("onFilterApplied called with updatedPosition=$updatedPosition, map=$selectedMap")
//        val hasActiveFilters = selectedMap.filterValues { it.isNotEmpty() }.isNotEmpty()
//        val bottomBarAnimator =
//                if (hasActiveFilters && !isBottomBarPink) ValueAnimator.ofFloat(0f, 1f)
//                else if (!hasActiveFilters && isBottomBarPink) ValueAnimator.ofFloat(1f, 0f)
//                else null
//
//        tabsAdapter.getFragment(updatedPosition).updateState(!selectedMap[updatedPosition].isNullOrEmpty())
//
//        bottomBarAnimator?.apply {
//            isBottomBarPink = !isBottomBarPink
//            addUpdateListener { animation ->
//                val color = blendColors(bottomBarColor, bottomBarPinkColor, animation.animatedValue as Float)
//                bottomBarContainer.setCardBackgroundColor(color)
//            }
//            duration = toggleDuration
//            start()
//        }
//    }
//
//    private fun animateFragment(open: Boolean) {
//        val elementsSettlingAnimator =
//                if (open) ValueAnimator.ofFloat(0f, 1f)
//                else ValueAnimator.ofFloat(1f, 0f)
//
//        elementsSettlingAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//
//            val bottomBarBg = blendColors(bgColor, if (isBottomBarPink) bottomBarPinkColor else bottomBarColor, value)
//            bottomBarContainer.setCardBackgroundColor(bottomBarBg)
//
//            val bottomBarTx = -bottomBarTranslateAmount * (1 - value)
//            bottomBar.translationX = bottomBarTx
//
//            val filterIconTint = blendColors(filterIconColor, filterIconActiveColor, value)
//            filterIcon.setColorFilter(filterIconTint)
//
//            closeButton.alpha = value
//
//            val height = tabsHeight * value
//            cardContainer.layoutParams.height = (sheetPeekHeight + height).toInt()
//            tabLayout.layoutParams.height = height.toInt()
//            cardContainer.requestLayout()
//            tabLayout.requestLayout()
//            tabsPager.translationY = tabsHeight * (1 - value)
//        }
//        elementsSettlingAnimator.duration = settlingAnimationDuration
//
//        val viewPagerAnimator = ValueAnimator.ofFloat(0f, 1f)
//        viewPagerAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            if (open) {
//                viewPager.translationY = (1 - value) * viewPagerTranslateAmount
//                viewPager.alpha = value
//            } else {
//                viewPager.alpha = 1 - value
//            }
//        }
//        viewPagerAnimator.duration = viewPagerAnimationDuration
//        val set = AnimatorSet()
//        if (open) {
//            set.playSequentially(elementsSettlingAnimator, viewPagerAnimator)
//        } else {
//            set.playTogether(elementsSettlingAnimator, viewPagerAnimator)
//            set.doOnEnd { (activity as MainActivity).onFragmentExit() }
//        }
//        set.interpolator = if (open) DecelerateInterpolator() else AccelerateInterpolator()
//        set.start()
//    }
//
//    private var offset = 0f
//
//    private fun getOffset(): Float =
//    // offset = (tabItemWidth * positionOffset) - ((32).dpToPx(context) * positionOffset)
//            /*offset-*/viewPager.scrollX.toFloat() /
//            (screenWidth / (tabItemWidth + 32.dp))
//
//    private fun synchroniseViewPagers() {
//        /* viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//
//             private var mScrollState = ViewPager.SCROLL_STATE_IDLE
//             private var oldOffset = 0f
//             private var totalDrag = 0f
//
//
//             override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
// //                Log.d(TAG, "position = $position, positionOffset = $positionOffset, positionOffsetPixels = $positionOffsetPixels")
//                 if (positionOffsetPixels == 0) {
//                     return
//                 }
// //                Log.d(TAG, "Direction: " + if (positionOffsetPixels < 0) "Forward" else "Backward")
//
//                 if (!tabsPager.isFakeDragging) {
//                     tabsPager.beginFakeDrag()
// //                    tabsPager.currentItem = position
//                 }
//
//                 // 32 is 2*view pager margin offset
//                 // 5 is random int that works well
//                 offset = (tabItemWidth * positionOffset) - ((32 - 8).dp * positionOffset)
// //                Log.d(TAG, "max offset = ${(tabItemWidth * 1) - ((32 - 5).dpToPx(context) * 1)}")
//                 val fakeDragAmount = -1 * (offset - oldOffset)
//
//                 if (oldOffset != 0f)
//                     tabsPager.fakeDragBy(fakeDragAmount)
//
//                 totalDrag += fakeDragAmount
//                 logd("fakeDrag by: $oldOffset - $offset =  $fakeDragAmount")
// //                Log.d(TAG, "totalDrag = $totalDrag")
// //                Log.d(TAG, "viewPager scrollX = ${viewPager.scrollX}")
//                 oldOffset = offset
//             }
//
//             override fun onPageSelected(position: Int) {}
//
//             override fun onPageScrollStateChanged(state: Int) {
//                 mScrollState = state
//                 if (state == ViewPager.SCROLL_STATE_IDLE) {
//                     oldOffset = 0f
//                     if (tabsPager.isFakeDragging) {
//                         tabsPager.endFakeDrag()
//                     }
//                     tabsPager.setCurrentItem(viewPager.currentItem, true)
//                 }
//             }
//
//         })
// */
//        tabsPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//
//            private var mScrollState = ViewPager.SCROLL_STATE_IDLE
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                //                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
//                //                    return
//                //                }
//
//                logd("tab scroll pixel = $positionOffsetPixels")
//
//                //                viewPager.scrollTo(tabsPager.scrollX, viewPager.scrollY)
//            }
//
//            override fun onPageSelected(position: Int) {
//
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                //                mScrollState = state
//                //                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                //                    viewPager.setCurrentItem(tabsPager.currentItem, false)
//                //                }
//            }
//        })
//    }
//
//    /*override fun onAttachFragment(childFragment: Fragment) {
//        super.onAttachFragment(childFragment)
//        if (childFragment is FilterSubFragment) {
//            childFragment.setListener { position, selectedFilters ->
//                val initialState = fragmentPositionWithActiveFilters[position]
//                fragmentPositionWithActiveFilters[position] = selectedFilters.isNotEmpty()
//
//                var animator: ValueAnimator? = null
//
//                if (fragmentPositionWithActiveFilters.isAtLeastOneTrue() && !isBottomBarPink) {
//                    animator = ValueAnimator.ofArgb(bottomBarColor, bottomBarPinkColor)
//                    isBottomBarPink = !isBottomBarPink
//                } else if (!fragmentPositionWithActiveFilters.isAtLeastOneTrue() && isBottomBarPink) {
//                    animator = ValueAnimator.ofArgb(bottomBarPinkColor, bottomBarColor)
//                    isBottomBarPink = !isBottomBarPink
//                }
//
//                if (!initialState && fragmentPositionWithActiveFilters[position]) {
//                    tabsAdapter.getFragment(position).animateBadge(true)
//                } else if (initialState && !fragmentPositionWithActiveFilters[position]) {
//                    tabsAdapter.getFragment(position).animateBadge(false)
//                }
//
//                animator?.addUpdateListener { animation ->
//                    bottomBarContainer.setCardBackgroundColor(animation.animatedValue as Int)
//                }
//                animator?.addListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator?) {
//                        super.onAnimationEnd(animation)
//                    }
//                })
//                animator?.duration = FilterSubFragment.TOGGLE_DURATION
//                animator?.start()
//
//            }
//        }
//    }*/
//
//    // method from http://stackoverflow.com/a/11599282/1294681
//    private fun simulate(action: Int, startTime: Long, endTime: Long, motionX: Float) {
//        logd("getOffset() = ${getOffset()}\t\tmotionX = $motionX")
//        // specify the property for the two touch points
//        val properties = arrayOfNulls<MotionEvent.PointerProperties>(1)
//        val pp = MotionEvent.PointerProperties()
//        pp.id = 0
//        pp.toolType = MotionEvent.TOOL_TYPE_FINGER
//
//        properties[0] = pp
//
//        // specify the coordinations of the two touch points
//        // NOTE: you MUST set the pressure and size value, or it doesn't work
//        val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(1)
//        val pc = MotionEvent.PointerCoords()
//        pc.x = motionX
//        pc.pressure = 1f
//        pc.size = 1f
//        pointerCoords[0] = pc
//
//        val ev = MotionEvent.obtain(
//                startTime, endTime, action, 1, properties,
//                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0)
//
//        tabsPager.dispatchTouchEvent(ev)
//    }
//}