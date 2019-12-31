package com.nikhilpanju.fabfilter.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar
import com.google.android.material.appbar.AppBarLayout
import com.nikhilpanju.fabfilter.R
import com.nikhilpanju.fabfilter.filter.FiltersLayout
import com.nikhilpanju.fabfilter.utils.bindView


var animationPlaybackSpeed: Double = 0.8

/**
 * https://dribbble.com/shots/2940944--5-Filters
 */
class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)
    private val toolbar: View by bindView(R.id.appbar_container)
    private val toolbarTitle: View by bindView(R.id.toolbar_title)
    private val placeHolderToolbar: View by bindView(R.id.place_holder_toolbar)
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val animationSpeedSeekbar: CrystalSeekbar by bindView(R.id.animation_speed_seekbar)

    private val animationSpeedText: TextView by bindView(R.id.animation_speed_text)
    private val githubCodeLink: TextView by bindView(R.id.github_code_link)
    private val githubMeLink: TextView by bindView(R.id.github_me_link)

    private lateinit var mainListAdapter: MainListAdapter
    private lateinit var onToolbarOffsetChangedListener: OnToolbarOffsetChangedListener

    /**
     * Used by FiltersLayout since we don't want to expose mainListAdapter (why?)
     * (Option: Combine everything into one activity if & when necessary)
     */
    var isAdapterFiltered: Boolean
        get() = mainListAdapter.isFiltered
        set(value) {
            mainListAdapter.isFiltered = value
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onToolbarOffsetChangedListener = OnToolbarOffsetChangedListener(
                toolbar,
                toolbarTitle,
                drawerIcon,
                placeHolderToolbar
        )
        appbar.addOnOffsetChangedListener(onToolbarOffsetChangedListener)

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
            updateRecyclerViewAnimDuration()
        }
        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        githubCodeLink.setOnClickListener { openBrowser(R.string.github_link_code) }
        githubMeLink.setOnClickListener { openBrowser(R.string.github_link_me) }
    }

    private fun updateRecyclerViewAnimDuration() {
        recyclerView.itemAnimator?.removeDuration = FiltersLayout.closeIconRotationDuration * 60 / 100
        recyclerView.itemAnimator?.addDuration = FiltersLayout.closeIconRotationDuration
    }

    private fun openBrowser(resId: Int) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(resId))))
    }

    override fun onDestroy() {
        if (::onToolbarOffsetChangedListener.isInitialized) {
            appbar.removeOnOffsetChangedListener(onToolbarOffsetChangedListener)
        }
        super.onDestroy()
    }

    /**
     * Called from FiltersLayout to get adapter scale down animator
     */
    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator =
            mainListAdapter.getScaleDownAnimator(isScaledDown)
}