package com.nikhilpanju.fabfilter.legacy

//
//import android.animation.AnimatorSet
//import android.animation.ObjectAnimator
//import android.animation.ValueAnimator
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AccelerateInterpolator
//import android.view.animation.DecelerateInterpolator
//import android.widget.ImageView
//import androidx.cardview.widget.CardView
//import androidx.core.animation.doOnEnd
//import androidx.core.animation.doOnStart
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.nikhilpanju.fabfilter.utils.ArcAnimator.ArcAnimator
//import com.nikhilpanju.fabfilter.utils.ArcAnimator.Side
//import com.nikhilpanju.fabfilter.main.MainActivity
//import com.nikhilpanju.fabfilter.R
//import com.nikhilpanju.fabfilter.utils.*
//
//
//class FilterFrag : Fragment() {
//
//    private val numFrags = 5
//
//    private val fabCardView: CardView by bindView(R.id.fab_card_view)
//    private val tabsRecyclerView: RecyclerView by bindView(R.id.tabs_recycler_view)
//    private val filterViewPager: ViewPager2 by bindView(R.id.filter_view_pager)
//    private val bottomBarBg: View by bindView(R.id.bottom_bar_bg)
//    private val closeIcon: View by bindView(R.id.close_icon)
//    private val filterIcon: ImageView by bindView(R.id.filter_icon)
//
//    private val bottomBarColor: Int by bindColor(R.color.bottom_bar_color)
//    private val bottomBarPinkColor: Int by bindColor(R.color.secondaryAccent)
//    private val bgColor: Int by bindColor(R.color.colorAccent)
//    private val filterIconColor: Int by bindColor(R.color.filter_icon_color)
//    private val filterIconActiveColor: Int by bindColor(R.color.filter_icon_active_color)
//
//    private val fabSize: Float by bindDimen(R.dimen.fab_size)
//    private val sheetPeekHeight: Float by bindDimen(R.dimen.sheet_peek_height)
//    private val sheetPeekWidth: Float by lazy { screenWidth.toFloat() }
//    private val fabRadius: Float by bindDimen(R.dimen.fab_radius)
//    private val fabElevation: Float by bindDimen(R.dimen.fab_elevation)
//    private val fabMaxElevation: Float by bindDimen(R.dimen.fab_max_elevation)
//    private val tabsHeight: Float by bindDimen(R.dimen.tabs_height)
//    private val tabItemWidth: Float by bindDimen(R.dimen.tab_item_width)
//
//    private val fabX: Float by lazy { screenWidth - fabSize - 32.dp }
//    private val fabY: Float by lazy { screenHeight - fabSize - 48.dp } //32 + 16(status bar height)
//    private var deltaYFabIcon: Int = -1
//    private val bottomBarTranslateAmount: Float by lazy { screenWidth / 4f }
//    private val viewPagerTranslateAmount = 16f.dp
//
//    private val pathAnimationDuration: Long = (200 / DURATION_SCALE).toLong()
//    private val revealAnimationDuration: Long = (200 / DURATION_SCALE).toLong()
//    private val radiusAnimationDuration: Long = (100 / DURATION_SCALE).toLong()
//    private val revealDuration: Long = (300 / DURATION_SCALE).toLong()
//
//    private val initAnimDuration: Long = (200 / DURATION_SCALE).toLong()
//    private val pagerSlideUpDuration: Long = (150 / DURATION_SCALE).toLong()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
//            inflater.inflate(R.layout.frag_filter, container, false)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initial attributes
////        fabCardView.setPivotToCenter()
//        setState(false)
//
//        // filterViewPager setup
//        filterViewPager.offscreenPageLimit = numFrags
////        filterViewPager.adapter = FiltersAdapter(context!!) { position: Int, selectedFilters: List<Int> ->
////            //TODO
////        }
//
//        closeIcon.setOnClickListener {
//            (activity as? MainActivity)?.onFragmentExit()
////            animateFragment(false)
//        }
//
//        animateFragment(true)
//    }
//
//    private fun logXY() {
//        logd("x=${fabCardView.x}, y=${fabCardView.y}")
//    }
//
//    private fun getAnim2(open: Boolean): ValueAnimator? {
//        val animator =
//                if (open) ValueAnimator.ofFloat(0f, 1f)
//                else ValueAnimator.ofFloat(1f, 0f)
//
//        animator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            val fabWidth = (fabSize + (sheetPeekWidth - fabSize) * value)
//            val fabHeight = (fabSize + (sheetPeekHeight - fabSize) * value)
//
//            fabCardView.layoutParams.width = fabWidth.toInt()
//            fabCardView.layoutParams.height = fabHeight.toInt()
//            fabCardView.x = sheetPeekWidth / 2 - fabWidth / 2
//            fabCardView.y = screenHeight - sheetPeekHeight / 2 - fabHeight / 2
//            // to keep the cardView as a circle: (not exact circle since peekWidth > peekHeight but good enough)
////            fabCardView.radius = /*fabRadius - fabRadius * value*/fabWidth / 2
//            fabCardView.radius = fabRadius * (1 - value)
//            logXY()
//
//            // modValue is used to speed up fabIconMask translation when closing because,
//            // otherwise, it doesn't look natural
////            val modValue = if (open) value else value * 0.75f
////            if (deltaYFabIcon > 0) filterIcon.translationY = modValue * deltaYFabIcon
//
//            fabCardView.requestLayout()
//        }
//
//        animator.interpolator = if (open) DecelerateInterpolator() else AccelerateInterpolator()
//        animator.duration = revealDuration
//        return animator
//    }
//
//    private fun getRevealAnimator(open: Boolean): AnimatorSet {
//        val sizeAnimator =
//                if (open) ValueAnimator.ofFloat(0f, 1f)
//                else ValueAnimator.ofFloat(1f, 0f)
//
//        sizeAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//
//            val fabWidth = (fabSize + (sheetPeekWidth - fabSize) * value)
//            val fabHeight = (fabSize + (sheetPeekHeight - fabSize) * value)
//
//            fabCardView.layoutParams.width = fabWidth.toInt()
//            fabCardView.layoutParams.height = fabHeight.toInt()
//            // to keep the cardView as a circle: (not exact circle since peekWidth > peekHeight but good enough)
//            fabCardView.radius = /*fabRadius - fabRadius * value*/fabWidth / 2
//            fabCardView.x = sheetPeekWidth / 2 - fabWidth / 2
//            fabCardView.y = screenHeight - sheetPeekHeight / 2 - fabHeight / 2
//
//            logXY()
//
//            // modValue is used to speed up fabIconMask translation when closing because,
//            // otherwise, it doesn't look natural
////            val modValue = if (open) value else value * 0.75f
////            if (deltaYFabIcon > 0) filterIcon.translationY = modValue * deltaYFabIcon
//
//            fabCardView.requestLayout()
//        }
//        sizeAnimator.doOnStart { logXY();logd("----------") }
//        sizeAnimator.duration = revealAnimationDuration
//
//        val edgeRadiusAnimator =
//                if (open) ValueAnimator.ofFloat(sheetPeekWidth / 2, 0f)
//                else ValueAnimator.ofFloat(0f, sheetPeekWidth / 2)
//
//        edgeRadiusAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            fabCardView.radius = value
//        }
//        edgeRadiusAnimator.duration = radiusAnimationDuration
//
//        // note; we use 2 animators here because as per the animation, the fabCardView first re-sizes as a circle then
//        // the reveal is completed for the corners
//
//        val animatorSet = AnimatorSet()
//        if (open) {
//            animatorSet.playSequentially(sizeAnimator, edgeRadiusAnimator)
//        } else {
//            animatorSet.playSequentially(edgeRadiusAnimator, sizeAnimator)
////            animatorSet.doOnEnd { fabIconMask.translationY = 0f }
//        }
//        return animatorSet
//    }
//
//    private fun getPathAnimator(open: Boolean): AnimatorSet {
//        val startX = fabX + fabSize / 2
//        val startY = fabY + fabSize / 2
//        val endX = sheetPeekWidth / 2
//        val endY = screenHeight - sheetPeekHeight / 2
////        val endY = (coordinatorLayout.height - sheetPeekHeight / 2)
//
//        val arcAnimator =
//                if (open)
//                    ArcAnimator.createArcAnimator(fabCardView, startX, startY, endX, endY, 90f, Side.LEFT)
//                else
//                    ArcAnimator.createArcAnimator(fabCardView, endX, endY, startX, startY, 90f, Side.LEFT)
//
//        arcAnimator.addUpdateListener { logXY() }
//        arcAnimator.doOnEnd { logd("--------");logXY();logd("--------\n--------") }
//
////        if (deltaYFabIcon <= 0) {
////            arcAnimator.doOnEnd {
////                val location = IntArray(2)
////                filterIcon.getLocationOnScreen(location)
////                deltaYFabIcon = screenHeight - location[1] - 16.dp
////            }
////        }
//
//        val elevationAnimator =
//                if (open)
//                    ValueAnimator.ofFloat(fabElevation, fabMaxElevation)
//                else
//                    ValueAnimator.ofFloat(fabMaxElevation, fabElevation)
//
//        elevationAnimator.addUpdateListener { fabCardView.cardElevation = it.animatedValue as Float }
//
//        val animatorSet = AnimatorSet()
//        animatorSet.playTogether(arcAnimator, elevationAnimator)
//        animatorSet.duration = pathAnimationDuration
//        animatorSet.interpolator = if (open) DecelerateInterpolator() else AccelerateInterpolator()
////        if(!open) animatorSet.startDelay = 50
//        return animatorSet
//    }
//
//    private fun animateFragment(open: Boolean) {
//        // ----------------- Bottom Bar BG ---------------------- //
//        val bottomBarBgAnimator =
//                if (open)
//                    ValueAnimator.ofArgb(bgColor, bottomBarColor)
//                else
//                    ValueAnimator.ofArgb(bottomBarColor, bgColor)
//        //TODO
////                    ValueAnimator.ofArgb(if (isBottomBarPink) bottomBarPinkColor else bottomBarColor, bgColor)
//
//        bottomBarBgAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Int
//            bottomBarBg.setBackgroundColor(value)
//        }
//
//        // ----------------- Fab Icon Color ---------------------- //
//        val fabIconColorAnimator =
//                if (open) ValueAnimator.ofArgb(filterIconColor, filterIconActiveColor)
//                else ValueAnimator.ofArgb(filterIconActiveColor, filterIconColor)
//
//        fabIconColorAnimator.addUpdateListener { filterIcon.setColorFilter(it.animatedValue as Int) }
//
//        // ----------------- Tab & Card Height ---------------------- //
//        val heightAnimator =
//                if (open)
//                    ValueAnimator.ofInt(0, tabsHeight.toInt())
//                else
//                    ValueAnimator.ofInt(tabsHeight.toInt(), 0)
//
//        heightAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Int
//            fabCardView.layoutParams.height = (sheetPeekHeight + value).toInt()
//            tabsRecyclerView.layoutParams.height = value
//            fabCardView.requestLayout()
//            tabsRecyclerView.requestLayout()
//        }
//
////        heightAnimator.doOnStart {
////            tabsRecyclerView.pivotY = tabsHeight
////        }
//
//        // ----------------- Icons Translate & Close Button Fade ---------------------- //
//
//        val closeIconTxAnimator =
//                ObjectAnimator.ofFloat(closeIcon, View.TRANSLATION_X, if (open) 0f else -bottomBarTranslateAmount)
////        closeIconTxAnimator.doOnStart { closeIcon.x = (0-closeIcon.width).toFloat() }
//
//        val filterIconTxAnimator =
//                ObjectAnimator.ofFloat(filterIcon, View.TRANSLATION_X, if (open) bottomBarTranslateAmount else -bottomBarTranslateAmount)
//
//        val closeButtonAlphaAnimator =
//                ObjectAnimator.ofFloat(closeIcon, View.ALPHA, if (open) 1f else 0f)
//
//
//        val animSet = AnimatorSet()
//        animSet.play(bottomBarBgAnimator)
//                .with(closeIconTxAnimator)
//                .with(filterIconTxAnimator)
//                .with(heightAnimator)
//                .with(closeButtonAlphaAnimator)
//                .with(fabIconColorAnimator)
//        animSet.duration = initAnimDuration
////        if (open) animSet.startDelay = initAnimDelay
//
//        val viewPagerAnimator = ValueAnimator.ofFloat(0f, 1f)
//        viewPagerAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            if (open) {
//                filterViewPager.translationY = (1 - value) * viewPagerTranslateAmount
//                filterViewPager.alpha = value
//            } else {
//                filterViewPager.alpha = 1 - value
//            }
//        }
//        viewPagerAnimator.duration = pagerSlideUpDuration
//        val set = AnimatorSet()
//        if (open) {
//            set.playSequentially(getPathAnimator(true), getRevealAnimator(true), animSet, viewPagerAnimator)
//        } else {
//            set.playTogether(animSet, viewPagerAnimator)
//            set.play(animSet).with(viewPagerAnimator).before(getRevealAnimator(false)).before(getPathAnimator(false))
//            set.doOnEnd {
//                //TODO
////                listener?.onFragmentExit()
//            }
//        }
////        set.interpolator = if (open) DecelerateInterpolator() else AccelerateInterpolator()
//        set.start()
//    }
//
//    private fun setState(isExpanded: Boolean) {
//        // Card Container
//        fabCardView.layoutParams.let {
//            it.height = (if (isExpanded) sheetPeekHeight + tabsHeight else fabSize).toInt()
//            it.width = (if (isExpanded) sheetPeekWidth else fabSize).toInt()
//        }
//        fabCardView.radius = if (isExpanded) 0f else fabRadius
//        fabCardView.cardElevation = if (isExpanded) fabMaxElevation else fabElevation
//        if (!isExpanded) {
//            fabCardView.x = fabX
//            fabCardView.y = fabY
//        }
//
//        // Tabs
//        tabsRecyclerView.layoutParams.height = if (isExpanded) tabsHeight.toInt() else 0
//
//        // FilterViewPager
//        filterViewPager.alpha = if (isExpanded) 1f else 0f
//        filterViewPager.translationY = if (isExpanded) 0f else viewPagerTranslateAmount
//
//        // Bottom Bar Bg //TODO pink color? (is isExpanded=true used?)
//        bottomBarBg.setBackgroundColor(if (isExpanded) bottomBarColor else bgColor)
//
//        // Icons
//        closeIcon.alpha = if (isExpanded) 1f else 0f
//        closeIcon.doOnGlobalLayout { it.x = ((if (isExpanded) screenWidth / 4 else 0) - it.width).toFloat() }
//
//        filterIcon.setColorFilter(if (isExpanded) filterIconActiveColor else filterIconColor)
//        if (isExpanded) filterIcon.translationX = (screenWidth / 4).toFloat()
//    }
//}