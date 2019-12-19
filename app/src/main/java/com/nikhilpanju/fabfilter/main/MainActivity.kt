package com.nikhilpanju.fabfilter.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar
import com.google.android.material.appbar.AppBarLayout
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.filter.FiltersLayout
import com.nikhilpanju.fabfilter.filter.FiltersMotionLayout
import com.nikhilpanju.fabfilter.utils.bindView


var animationPlaybackSpeed: Double = 0.8

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    private val filtersLayout: FiltersLayout by bindView(R.id.filters_layout)
    private val filtersMotionLayout: FiltersMotionLayout by bindView(R.id.filters_motion_layout)

    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val motionLayoutCheckbox: CheckBox by bindView(R.id.motion_layout_checkbox)
    private val animationSpeedSeekbar: CrystalSeekbar by bindView(R.id.animation_speed_seekbar)
    private val animationSpeedText: TextView by bindView(R.id.animation_speed_text)
    private val githubCodeLink: TextView by bindView(R.id.github_code_link)
    private val githubMeLink: TextView by bindView(R.id.github_me_link)

    private lateinit var mainListAdapter: MainListAdapter
    private val loadingDuration: Long
        get() = (resources.getInteger(R.integer.loadingAnimDuration) / animationPlaybackSpeed).toLong()

    /**
     * Used to open nav drawer when opening app for first time (to show options)
     */
    private val prefs: SharedPreferences
        get() = getSharedPreferences("FabFilter", Context.MODE_PRIVATE)
    private var isFirstTime: Boolean
        get() = prefs.getBoolean("isFirstTime", true)
        set(value) = prefs.edit { putBoolean("isFirstTime", value) }
    /**
     * Used by FiltersLayout since we don't want to expose mainListAdapter (why?)
     * (Option: Combine everything into one activity if & when necessary)
     */
    var isAdapterFiltered: Boolean
        get() = mainListAdapter.isFiltered
        set(value) {
            mainListAdapter.isFiltered = value
        }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Appbar behavior init
        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        // Init FilterLayout
        useFiltersMotionLayout(false)

        // RecyclerView Init
        mainListAdapter = MainListAdapter(this)
        recyclerView.adapter = mainListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        updateRecyclerViewAnimDuration()

        // Nav Drawer Init
        animationSpeedSeekbar.setOnSeekbarChangeListener { value ->
            animationPlaybackSpeed = value as Double
            animationSpeedText.text = "${"%.1f".format(animationPlaybackSpeed)}x"
            filtersMotionLayout.updateDurations()
            updateRecyclerViewAnimDuration()
        }
        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        githubCodeLink.setOnClickListener { openBrowser(R.string.github_link_code) }
        githubMeLink.setOnClickListener { openBrowser(R.string.github_link_me) }
        motionLayoutCheckbox.setOnCheckedChangeListener { _, isChecked -> useFiltersMotionLayout(isChecked) }

        // Open Nav Drawer when opening app for the first time
        if (isFirstTime) {
            drawerLayout.openDrawer(GravityCompat.START)
            isFirstTime = false
        }
    }

    /**
     * Callback for motionLayoutCheckbox
     * isChecked = true -> Use [FiltersMotionLayout]
     * isChecked = false -> Use [FiltersLayout]
     */
    private fun useFiltersMotionLayout(isChecked: Boolean) {
        filtersLayout.isVisible = !isChecked
        filtersMotionLayout.isVisible = isChecked
    }

    /**
     * Update RecyclerView Item Animation Durations
     */
    private fun updateRecyclerViewAnimDuration() = recyclerView.itemAnimator?.run {
        removeDuration = loadingDuration * 60 / 100
        addDuration = loadingDuration
    }

    /**
     * Open browser for given string resId URL
     */
    private fun openBrowser(@StringRes resId: Int): Unit =
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(resId))))

    /**
     * Called from FiltersLayout to get adapter scale down animator
     */
    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator =
            mainListAdapter.getScaleDownAnimator(isScaledDown)
}