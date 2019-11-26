package com.nikhilpanju.fabfilter.legacy

//
//import android.animation.TimeInterpolator
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AccelerateInterpolator
//import android.view.animation.OvershootInterpolator
//import android.widget.ImageView
//import androidx.annotation.Nullable
//import com.nikhilpanju.fabfilter.R
//import com.nikhilpanju.fabfilter.filter.toggleDuration
//import com.nikhilpanju.fabfilter.utils.bindView
//import com.nikhilpanju.fabfilter.utils.dp
//
//class TabFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
//
//    private val tabItem: View by bindView(R.id.tab_container)
//    private val tabPill: ImageView by bindView(R.id.tab_pill)
//    private val badge: View by bindView(R.id.tab_badge)
//
//    private var hasFilters = false
//    private var position = -1
//
//    companion object {
//        private val POSITION = "position"
//
//        fun newInstance(position: Int): TabFragment {
//            val fragment = TabFragment()
//
//            val args = Bundle()
//            args.putInt(POSITION, position)
//            fragment.arguments = args
//
//            return fragment
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        position = arguments?.getInt(POSITION) ?: -1
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
//            inflater.inflate(R.layout.item_filter_tab, container, false)
//
//    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val padding = (if (position == 0) 24 else 24).dp
////        val paddingRight: Int = if (position == FilterFragment.NUM_FRAGS - 1)
////            (context.resources.getDimension(R.dimen.tab_item_width) * 10).toInt()
////        else
////            0
//        tabItem.setPadding(padding, 0, 0, 0)
//    }
//
//    override fun onClick(v: View?) {
//    }
//
//    @Suppress("USELESS_CAST")
//    fun updateState(hasFilters: Boolean) {
////        badge.animate(
////                sX = if (toggle) 1f else 0f,
////                sY = if (toggle) 1f else 0f,
////                duration = FilterSubFragment.TOGGLE_DURATION,
////                interpolator = if (toggle) OvershootInterpolator() as TimeInterpolator else AccelerateInterpolator()
////        )
//        if (this.hasFilters == hasFilters) return
//
//        this.hasFilters = hasFilters
//        badge.animate()
//                .scaleX(if (hasFilters) 1f else 0f)
//                .scaleY(if (hasFilters) 1f else 0f)
//                .setDuration(toggleDuration)
//                .setInterpolator(if (hasFilters) OvershootInterpolator() as TimeInterpolator else AccelerateInterpolator())
//                .start()
//    }
//}