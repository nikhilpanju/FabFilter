package com.nikhilpanju.fabfilter.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.filter.FiltersFragment
import com.nikhilpanju.fabfilter.utils.bindView


/**
 * https://dribbble.com/shots/2940944--5-Filters
 */
class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)

    private lateinit var mainListAdapter: MainListAdapter

    /**
     * Used by FiltersFragment since we don't want to expose mainListAdapter (why?)
     * (Option: Combine everything into one activity if & when necessary)
     */
    var isAdapterFiltered: Boolean
        get() = mainListAdapter.isFiltered
        set(value) {
            mainListAdapter.isFiltered = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Appbar behavior init
        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        // RecyclerView Init
        mainListAdapter = MainListAdapter(this)
        recyclerView.adapter = mainListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.removeDuration = FiltersFragment.closeIconRotationDuration
        recyclerView.itemAnimator?.addDuration = FiltersFragment.closeIconRotationDuration

        // Show FiltersFragment
        supportFragmentManager.beginTransaction()
                .add(R.id.coordinator_layout, FiltersFragment(), "FiltersFragment")
                .commit()
    }

    /**
     * Called from FiltersFragment when performing animation to simultaneously animate the adapter
     */
    fun animateMainListAdapter(open: Boolean) {
        mainListAdapter.animateItems(open)
    }
}
