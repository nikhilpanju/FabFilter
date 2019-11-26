package com.nikhilpanju.fabfilter.legacy

//
//import android.animation.ArgbEvaluator
//import android.content.Context
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import androidx.core.content.ContextCompat
//import com.nikhilpanju.fabfilter.R
//
//class TabsTransformer(val context: Context) : androidx.viewpager.widget.ViewPager.PageTransformer {
//    val TAG = "TabsTransformer"
//    var ratio: Float = (context.resources.getDimension(R.dimen.tab_item_width)
//            - 16.dp.toFloat()) / context.screenWidth
//    val deltaScale = 0.15f
//
//    private val tabColor = ContextCompat.getColor(context, R.color.tab_unselected_color)
//    private val tabSelectedColor = ContextCompat.getColor(context, R.color.tab_selected_color)
//
//    init {
//        Log.d(TAG, "ratio = $ratio")
//    }
//
//    override fun transformPage(page: View, position: Float) {
//        val absPos = position.abs
//        val tabPill: ImageView = page.findViewById(R.id.tab_pill)
//        val tabItem: View = page.findViewById(R.id.tab_container)
//        if (absPos <= ratio) {
//            val newPos = absPos / ratio
//            tabPill.setPivotToCenter()
//            tabPill.setColorFilter(
//                    ArgbEvaluator().evaluate(1 - newPos, tabColor, tabSelectedColor) as Int
//            )
//            tabItem.setScale(
//                    (1 - newPos) * deltaScale + (1 - deltaScale)
//            )
//        } else {
//            tabPill.setColorFilter(tabColor)
//            tabItem.setScale(1 - deltaScale)
//        }
//
//        Log.d(TAG, "position = $position")
////        tabPill.scaleX = 1 + (1 - Math.abs(position)) * 1.2f
////        tabPill.scaleY = 1 + (1 - Math.abs(position)) * 1.2f
//    }
//}