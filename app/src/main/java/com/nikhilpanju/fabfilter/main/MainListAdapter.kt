package com.nikhilpanju.fabfilter.main

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.filter.FiltersLayout
import com.nikhilpanju.fabfilter.utils.*
import java.util.*

data class MainListModel(val id: Int)

class MainListAdapter(context: Context) : RecyclerView.Adapter<MainListAdapter.ListViewHolder>() {

    private val shrinkAnimation = "SHRINK_ANIMATION"
    private val unShrinkAnimation = "UN_SHRINK_ANIMATION"

    private val listItemHorizontalPadding: Float by bindDimen(context, R.dimen.list_item_horizontal_padding)
    private val listItemVerticalPadding: Float by bindDimen(context, R.dimen.list_item_vertical_padding)

    private val originalBg: Int by bindColor(context, R.color.list_item_bg_collapsed)
    private val expandedBg: Int by bindColor(context, R.color.list_item_bg_expanded)

    private val originalWidth = context.screenWidth - 48.dp
    private val expandedWidth = context.screenWidth - 24.dp

    // height will be calculated dynamically
    private var originalHeight = -1
    private var expandedHeight = -1

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var recyclerView: RecyclerView

    var isFiltered = false
        set(value) {
            field = value
            filteredItems.forEach { if (value) notifyItemRemoved(it) else notifyItemInserted(it) }
        }
    private val filteredItems = intArrayOf(2, 5, 6, 8, 12)
    private val modelList = List(20) { MainListModel(it) }
    private val modelListFiltered =
            modelList.toMutableList().filterNot { it.id in filteredItems }
    private val adapterList: List<MainListModel> get() = if (isFiltered) modelListFiltered else modelList

    private var expandedModel: MainListModel? = null

    private var isShrunk = false

    override fun getItemCount(): Int = adapterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
            ListViewHolder(inflater.inflate(R.layout.item_list, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = adapterList[position]
        val titleWidth = (140 + Random().nextInt(6) * 20).dp
        holder.title.layoutParams.width = titleWidth

        if (expandedHeight <= 0 && position == 0) {
            holder.expandView.isVisible = true
            holder.cardContainer.doOnGlobalLayout {
                expandedHeight = holder.cardContainer.height
                holder.expandView.isVisible = false
            }
        }

        holder.cardContainer.layoutParams.width = originalWidth
        if (model == expandedModel && expandedHeight >= 0) {
            //if view is expanded
            holder.expandView.isVisible = true
            holder.cardContainer.layoutParams.height = expandedHeight
            holder.cardContainer.layoutParams.width = expandedWidth
            holder.cardContainer.setBackgroundColor(expandedBg)
            holder.chevron.rotation = 90f
        } else if (originalHeight >= 0) {
            // if view is collapsed
            holder.expandView.isVisible = false
            holder.cardContainer.layoutParams.height = originalHeight
            holder.cardContainer.layoutParams.width = originalWidth
            holder.cardContainer.setBackgroundColor(originalBg)
            holder.chevron.rotation = 0f
        }

        shrinkItems(holder, position, if (isShrunk) 1f else 0f)

        holder.cardContainer.setOnClickListener {
            // get original height first if not already set
            if (originalHeight <= 0) originalHeight = holder.cardContainer.height

            when {
                expandedModel == model -> {
                    // collapse clicked view
                    expandPosition(holder, false)
                    expandedModel = null
                }
                expandedModel != null -> {
                    // expand clicked view + collapse previously expanded view
                    expandPosition(holder, true)
                    val expandedModelPosition = adapterList.indexOf(expandedModel!!)
                    val oldViewHolder = recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ListViewHolder
                    if (oldViewHolder != null) expandPosition(oldViewHolder, false)

                    expandedModel = model
                }
                else -> {
                    // expand clicked view
                    expandPosition(holder, true)
                    expandedModel = model
                }
            }
        }
    }

    private fun expandPosition(holder: ListViewHolder, expand: Boolean) {
        val animator =
                if (expand) ValueAnimator.ofFloat(0f, 1f)
                else ValueAnimator.ofFloat(1f, 0f)

        holder.expandView.isVisible = true
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float

            holder.cardContainer.layoutParams.height =
                    (originalHeight + (expandedHeight - originalHeight) * value).toInt()

            holder.cardContainer.layoutParams.width =
                    (originalWidth + (expandedWidth - originalWidth) * value).toInt()

            holder.cardContainer.setBackgroundColor(blendColors(originalBg, expandedBg, value))
            holder.cardContainer.requestLayout()

            holder.chevron.rotation = 90 * value
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = listItemExpandDuration
        animator.start()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shrink Animation
    ///////////////////////////////////////////////////////////////////////////

    fun animateItems(shrink: Boolean) {
        isShrunk = shrink
        notifyItemRangeChanged(0, itemCount, if (shrink) shrinkAnimation else unShrinkAnimation)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull { it is String && (it == shrinkAnimation || it == unShrinkAnimation) }
                ?: return super.onBindViewHolder(holder, position, payloads)

        if (originalHeight <= 0) originalHeight = holder.cardContainer.height

        val shrinkAnimator =
                if (payload == shrinkAnimation) ValueAnimator.ofFloat(0f, 1f)
                else ValueAnimator.ofFloat(1f, 0f)

        shrinkAnimator.addUpdateListener { shrinkItems(holder, position, it.animatedValue as Float) }
        shrinkAnimator.duration = FiltersLayout.pathAnimDuration
        shrinkAnimator.interpolator = FiltersLayout.getPathAnimationInterpolator(payload == shrinkAnimation)
        shrinkAnimator.start()
    }

    private fun shrinkItems(holder: ListViewHolder, position: Int, ratio: Float) {
        val model = adapterList[position]
        val itemExpanded = model == expandedModel
        holder.cardContainer.layoutParams.width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * ratio)).toInt()
        holder.cardContainer.layoutParams.height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * ratio)).toInt()

        holder.scaleContainer.pivotX = 0f
        holder.scaleContainer.scaleX = 1 - 0.05f * ratio
        holder.scaleContainer.scaleY = 1 - 0.05f * ratio

        holder.scaleContainer.setPadding(
                (listItemHorizontalPadding * (1 - 0.2f * ratio)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * ratio)).toInt(),
                (listItemHorizontalPadding * (1 - 0.2f * ratio)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * ratio)).toInt()
        )

        holder.listItemFg.alpha = ratio

        holder.cardContainer.requestLayout()
    }

    ////////////////////////////////////////////////////////////
    //------------------- ViewHolder -------------------------//

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val subtitle1: View by bindView(R.id.subtitle1)
        val subtitle2: View by bindView(R.id.subtitle2)
        val title: View by bindView(R.id.title)
        val expandView: View by bindView(R.id.expand_view)
        val chevron: View by bindView(R.id.chevron)
        val cardContainer: View by bindView(R.id.filters_container)
        val scaleContainer: View by bindView(R.id.scale_container)
        val listItemFg: View by bindView(R.id.list_item_fg)
    }
}