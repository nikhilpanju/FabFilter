package com.nikhilpanju.fabfilter.legacy

//
//import android.animation.AnimatorSet
//import android.animation.ValueAnimator
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import com.nikhilpanju.fabfilter.R
//import com.nikhilpanju.fabfilter.utils.CustomSeekbar
//import com.nikhilpanju.fabfilter.utils.bindColor
//import com.nikhilpanju.fabfilter.utils.bindOptionalViews
//
//class FilterSubFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
//    private val filterViews: List<ImageView> by bindOptionalViews(R.id.filter_pill_1, R.id.filter_pill_2,
//            R.id.filter_pill_3, R.id.filter_pill_4, R.id.filter_pill_5, R.id.filter_pill_6)
//
//    private val seekBars: List<CustomSeekbar> by bindOptionalViews(R.id.rangeSeekbar1, R.id.rangeSeekbar2)
//
//    private var selectedList = arrayListOf<Int>()
//    private val position: Int by lazy { arguments?.getInt(POSITION)!! }
//
//    private val unselectedColor: Int by bindColor(R.color.filter_pill_color)
//    private val selectedColor: Int by bindColor(R.color.filter_pill_selected_color)
//    private val unselectedBarColor: Int by bindColor(R.color.filter_seek_bar_color)
//    private val selectedBarColor: Int by bindColor(R.color.filter_seek_bar_selected_color)
//
//    private var listener: ((position: Int, selectedFilters: List<Int>) -> Unit)? = null
//
//    companion object {
//        const val TOGGLE_DURATION = 150L
//        private const val TAG = "FilterSubFragment"
//        private const val POSITION = "position"
//
//        fun newInstance(position: Int): FilterSubFragment {
//            Log.d(TAG, "newInstance position = $position")
//            val fragment = FilterSubFragment()
//
//            val args = Bundle()
//            args.putInt(POSITION, position)
//            fragment.arguments = args
//
//            return fragment
//        }
//    }
//
//    fun setListener(listener: (position: Int, selectedFilters: List<Int>) -> Unit) {
//        this.listener = listener
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
//            inflater.inflate(getLayoutRes(), container, false)
//
//    private fun getLayoutRes() =
//            when {
//                position == 2 -> R.layout.filter_layout_3
//                position % 2 == 0 -> R.layout.filter_layout_1
//                else -> R.layout.filter_layout_2
//            }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        filterViews.forEach { filterView: View ->
//            filterView.setOnClickListener(this)
//        }
//        seekBars.forEach { seekBar: CustomSeekbar ->
//            seekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
//                val index = seekBars.indexOf(seekBar)
//                var thumbColorAnimator: ValueAnimator? = null
//                var barColorAnimator: ValueAnimator? = null
//                if (selectedList.indexOf(index) < 0 && !(minValue.toInt() == 0 && maxValue.toInt() == 100)) {
//                    selectedList.add(index)
//                    listener?.invoke(position, selectedList)
//                    thumbColorAnimator = ValueAnimator.ofArgb(unselectedColor, selectedColor)
//                    barColorAnimator = ValueAnimator.ofArgb(unselectedBarColor, selectedBarColor)
//                } else if (selectedList.indexOf(index) >= 0 && minValue.toInt() == 0 && maxValue.toInt() == 100) {
//                    selectedList.remove(index)
//                    listener?.invoke(position, selectedList)
//                    thumbColorAnimator = ValueAnimator.ofArgb(selectedColor, unselectedColor)
//                    barColorAnimator = ValueAnimator.ofArgb(selectedBarColor, unselectedBarColor)
//                }
//                thumbColorAnimator?.addUpdateListener { animation ->
//                    val value = animation.animatedValue as Int
//                    seekBar.setLeftThumbHighlightColor(value)
//                    seekBar.setRightThumbHighlightColor(value)
//                    seekBar.setLeftThumbColor(value)
//                    seekBar.setRightThumbColor(value)
//                }
//                barColorAnimator?.addUpdateListener { animation ->
//                    val value = animation.animatedValue as Int
//                    seekBar.setBarHighlightColor(value)
//                }
//
//                if (thumbColorAnimator != null && barColorAnimator != null) {
//                    val set = AnimatorSet()
//                    set.playTogether(thumbColorAnimator, barColorAnimator)
//                    set.duration = TOGGLE_DURATION
//                    set.start()
//                }
//            }
//        }
//    }
//
//    override fun onClick(v: View?) {
//        val index = filterViews.indexOf(v)
//        if (index >= 0) {
//            if (selectedList.indexOf(index) >= 0) {
//                selectedList.remove(index)
//                animateFilter(v as ImageView, false)
//            } else {
//                selectedList.add(index)
//                animateFilter(v as ImageView, true)
//            }
//            listener?.invoke(position, selectedList)
//        }
//    }
//
//    private fun animateFilter(view: ImageView, selected: Boolean) {
//        val colorAnimator =
//                if (selected)
//                    ValueAnimator.ofArgb(unselectedColor, selectedColor)
//                else
//                    ValueAnimator.ofArgb(selectedColor, unselectedColor)
//
//        colorAnimator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Int
//            view.setColorFilter(value)
//        }
//        colorAnimator.duration = TOGGLE_DURATION
//        colorAnimator.start()
//    }
//}