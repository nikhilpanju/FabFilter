package com.nikhilpanju.fabfilter.filter

import android.animation.TimeInterpolator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.utils.bindView
import com.nikhilpanju.fabfilter.utils.setScale

class FiltersTabsAdapter(context: Context, private val listener: (Int) -> Unit) : RecyclerView.Adapter<FiltersTabsAdapter.FiltersTabsViewHolder>() {

    private val payloadUpdateBadge = "UPDATE_BADGE"

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var hasFilters = MutableList(FiltersFragment.numTabs) { false }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = FiltersFragment.numTabs

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersTabsViewHolder =
            FiltersTabsViewHolder(inflater.inflate(R.layout.item_filter_tab, parent, false))

    override fun onBindViewHolder(holder: FiltersTabsViewHolder, position: Int) {
        holder.itemView.setScale(defaultScale)
        holder.badge.setScale(if (hasFilters[position]) 1f else 0f)

        holder.itemView.setOnClickListener { listener(position) }
    }

    /**
     * This is called when badge state is updated so animate the badge
     */
    override fun onBindViewHolder(holder: FiltersTabsViewHolder, position: Int, payloads: MutableList<Any>) {
        payloads.firstOrNull { it is String && it == payloadUpdateBadge }
                ?: return super.onBindViewHolder(holder, position, payloads)

        val badgeShown = hasFilters[position]
        @Suppress("USELESS_CAST") val timeInterpolator =
                if (badgeShown) OvershootInterpolator(3f) as TimeInterpolator
                else AccelerateInterpolator()

        holder.badge.animate()
                .scaleX(if (badgeShown) 1f else 0f)
                .scaleY(if (badgeShown) 1f else 0f)
                .setDuration(FiltersFragment.toggleDuration)
                .setInterpolator(timeInterpolator)
                .start()

    }

    fun updateBadge(position: Int, hasFilters: Boolean) {
        if (this.hasFilters[position] == hasFilters) return
        this.hasFilters[position] = hasFilters
        notifyItemChanged(position, payloadUpdateBadge)
    }

    class FiltersTabsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tabItem: View by bindView(R.id.tab_container)
        val tabPill: ImageView by bindView(R.id.tab_pill)
        val badge: View by bindView(R.id.tab_badge)
    }

    companion object {
        const val defaultScale = 0.9f
        const val maxScale = 1.15f
    }
}
