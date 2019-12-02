package com.nikhilpanju.fabfilter.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.filter.FiltersLayout
import com.nikhilpanju.fabfilter.utils.*
import java.util.*

/**
 * List Model. A sample model that only contains id
 * titleWidth is a random dp value for the width of titleView in ViewHolder
 */
data class MainListModel(val id: Int) {
    val titleWidth = (140 + Random().nextInt(6) * 20).dp
}

/** Used as payload when doing the scale down animation */
class ScaleDownPayload(val scaleDown: Boolean)

class MainListAdapter(context: Context) : RecyclerView.Adapter<MainListAdapter.ListViewHolder>() {

    private val originalBg: Int by bindColor(context, R.color.list_item_bg_collapsed)
    private val expandedBg: Int by bindColor(context, R.color.list_item_bg_expanded)

    private val listItemHorizontalPadding: Float by bindDimen(context, R.dimen.list_item_horizontal_padding)
    private val listItemVerticalPadding: Float by bindDimen(context, R.dimen.list_item_vertical_padding)
    private val originalWidth = context.screenWidth - 48.dp
    private val expandedWidth = context.screenWidth - 24.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private val filteredItems = intArrayOf(2, 5, 6, 8, 12)
    private val modelList = List(20) { MainListModel(it) }
    private val modelListFiltered = modelList.filterNot { it.id in filteredItems }
    private val adapterList: List<MainListModel> get() = if (isFiltered) modelListFiltered else modelList
    /** Variable used to filter adapter items. 'true' if filtered and 'false' if not */
    var isFiltered = false
        set(value) {
            field = value
            filteredItems.forEach { if (value) notifyItemRemoved(it) else notifyItemInserted(it) }
        }

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: MainListModel? = null
    private var isScaledDown = false

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = adapterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
            ListViewHolder(inflater.inflate(R.layout.item_list, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = adapterList[position]

        expandItem(holder, model == expandedModel, animate = false)
        scaleDownItem(holder, position, isScaledDown, animate = false)

        holder.title.layoutParams.width = model.titleWidth

        holder.cardContainer.setOnClickListener {

            if (expandedModel == model) {

                // collapse clicked view
                expandItem(holder, expand = false, animate = true)
                expandedModel = null
            } else if (expandedModel != null) {

                // collapse previously expanded view
                val expandedModelPosition = adapterList.indexOf(expandedModel!!)
                val oldViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ListViewHolder
                if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            } else {

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            }
        }
    }

    private fun expandItem(holder: ListViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            val animator = getValueAnimator(
                    expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.expandView.isVisible = true }
            else animator.doOnEnd { holder.expandView.isVisible = false }

            animator.start()
        } else {

            // get originalHeight & expandedHeight if not gotten before
            if (expandedHeight < 0) {
                expandedHeight = 0 // so that this block is only called once

                holder.cardContainer.doOnGlobalLayout {
                    originalHeight = holder.cardContainer.height

                    // show expandView and record expandedHeight in
                    // next layout pass (doOnGlobalLayout) and hide it immediately
                    holder.expandView.isVisible = true
                    holder.cardContainer.doOnGlobalLayout {
                        expandedHeight = holder.cardContainer.height
                        holder.expandView.isVisible = false
                    }
                }
            } else {
                holder.expandView.isVisible = expand
            }
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    private fun setExpandProgress(holder: ListViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.cardContainer.layoutParams.height =
                    (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.cardContainer.layoutParams.width =
                (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        holder.cardContainer.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
        holder.cardContainer.requestLayout()

        holder.chevron.rotation = 90 * progress
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scale Down Animation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called to shrink items. notifyItemRangeChanged with payload will call onBindViewHolder
     * with payload where we will do the animation.
     *
     * Note: This animation is very heavy because it animates each item individually. Not very feasible
     * unless the no. of visible items in recy
     */
    fun animateItems(scaleDown: Boolean) {
        isScaledDown = scaleDown
        notifyItemRangeChanged(0, itemCount, ScaleDownPayload(scaleDown))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull { it is ScaleDownPayload } as? ScaleDownPayload
                ?: return super.onBindViewHolder(holder, position, payloads)

        scaleDownItem(holder, position, payload.scaleDown, animate = true)
    }

    private fun scaleDownItem(holder: ListViewHolder, position: Int, scaleDown: Boolean, animate: Boolean) {
        if (animate) {
            val shrinkAnimator = getValueAnimator(
                    scaleDown,
                    FiltersLayout.pathAnimDuration,
                    FiltersLayout.getPathAnimationInterpolator(scaleDown)
            ) { progress -> setScaleDownProgress(holder, position, progress) }
            shrinkAnimator.start()
        } else {
            setScaleDownProgress(holder, position, if (scaleDown) 1f else 0f)
        }
    }

    private fun setScaleDownProgress(holder: ListViewHolder, position: Int, progress: Float) {
        val model = adapterList[position]
        val itemExpanded = model == expandedModel
        holder.cardContainer.layoutParams.width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
        holder.cardContainer.layoutParams.height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()

        holder.scaleContainer.pivotX = 0f
        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.scaleContainer.setPadding(
                (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(),
                (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

        holder.listItemFg.alpha = progress

        holder.cardContainer.requestLayout()
    }

    ///////////////////////////////////////////////////////////////////////////
    // ViewHolder
    ///////////////////////////////////////////////////////////////////////////

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: View by bindView(R.id.title)
        val expandView: View by bindView(R.id.expand_view)
        val chevron: View by bindView(R.id.chevron)
        val cardContainer: View by bindView(R.id.card_container)
        val scaleContainer: View by bindView(R.id.scale_container)
        val listItemFg: View by bindView(R.id.list_item_fg)
    }
}