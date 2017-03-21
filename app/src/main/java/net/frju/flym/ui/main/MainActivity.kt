package net.frju.flym.ui.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_main_containers.view.*
import net.fred.feedex.R
import net.frju.androidquery.gen.FEED
import net.frju.androidquery.operation.condition.Where
import net.frju.flym.data.Feed
import net.frju.flym.data.Item
import net.frju.flym.ui.itemdetails.ItemDetailsFragment
import net.frju.flym.ui.items.ItemsFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainNavigator {

    private val feedGroups = mutableListOf<FeedGroup>()
    private val feedAdapter = FeedAdapter(feedGroups)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        nav.layoutManager = LinearLayoutManager(this)

        doAsync {
            feedGroups.clear()

            feedGroups.addAll(
                    FEED.select()
                    .where(Where.field(FEED.IS_GROUP).isTrue.or(Where.field(FEED.GROUP_ID).isEqualTo(null)))
                            .queryAndInit().map { FeedGroup(it, it.subFeeds?.toMutableList() ?: mutableListOf()) }
            )

            val unreads = Feed()
            unreads.id = Feed.UNREAD_ITEMS_ID
            unreads.title = getString(R.string.unread_entries)
            feedGroups.add(0, FeedGroup(unreads, mutableListOf()))

            val all = Feed()
            all.id = Feed.ALL_ITEMS_ID
            all.title = getString(R.string.all_entries)
            feedGroups.add(1, FeedGroup(all, mutableListOf()))

            val favorites = Feed()
            favorites.id = Feed.FAVORITES_ID
            favorites.title = getString(R.string.favorites)
            feedGroups.add(2, FeedGroup(favorites, mutableListOf()))

            uiThread {
                feedAdapter.notifyParentDataSetChanged(true)

                feedAdapter.onFeedClick { view, feed ->
                    goToItemsList(feed)
                    closeDrawer()
                }

                nav.adapter = feedAdapter
            }
        }

        containers_layout.custom_appbar.setOnNavigationClickListener(View.OnClickListener { toggleDrawer() })

        if (savedInstanceState == null) {
            closeDrawer()

            goToItemsList(null)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        feedAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        feedAdapter.onSaveInstanceState(outState)
    }

    fun closeDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.postDelayed({ drawer.closeDrawer(GravityCompat.START) }, 100)
        }
    }

    fun openDrawer() {
        if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    fun toggleDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_nav__settings -> {
                closeDrawer()

                goToSettings()
            }

            R.id.menu_main_nav__feedback -> {
                closeDrawer()

                goToFeedback()
            }

            else -> return false
        }
        return true
    }

    fun goBack(): Boolean {
        val state = containers_layout.state
        if (state == MainNavigator.State.TWO_COLUMNS_WITH_DETAILS && !containers_layout.hasTwoColumns()) {
            if (clearDetails()) {
                containers_layout.state = MainNavigator.State.TWO_COLUMNS_EMPTY
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (!goBack()) {
            super.onBackPressed()
        }
    }

    private fun clearDetails(): Boolean {
        val details = supportFragmentManager.findFragmentByTag(TAG_DETAILS)
        if (details != null) {
            supportFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .remove(details)
                    .commit()
            return true
        }
        return false
    }

    override fun goToItemsList(feed: Feed?) {
        clearDetails()
        containers_layout.custom_appbar.setState(MainNavigator.State.TWO_COLUMNS_EMPTY)
        containers_layout.state = MainNavigator.State.TWO_COLUMNS_EMPTY
        val master = ItemsFragment.newInstance(feed)
        supportFragmentManager.beginTransaction().replace(R.id.frame_master, master, TAG_MASTER).commit()
    }

    override fun goToItemDetails(item: Item) {
        containers_layout.custom_appbar.setState(MainNavigator.State.TWO_COLUMNS_WITH_DETAILS)
        containers_layout.state = MainNavigator.State.TWO_COLUMNS_WITH_DETAILS
        val fragment = ItemDetailsFragment.newInstance(item)
        supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_details, fragment, TAG_DETAILS)
                .commit()
    }

    override fun goToPreviousItem() {
        val listFragment = supportFragmentManager.findFragmentById(R.id.frame_master) as ItemsFragment
        val detailFragment = supportFragmentManager.findFragmentById(R.id.frame_details) as ItemDetailsFragment

        val previousItem = listFragment.getPreviousItem()
        if (previousItem != null) {
            listFragment.setSelectedItem(previousItem)
            detailFragment.setItem(previousItem)
        }
    }

    override fun goToNextItem() {
        val listFragment = supportFragmentManager.findFragmentById(R.id.frame_master) as ItemsFragment
        val detailFragment = supportFragmentManager.findFragmentById(R.id.frame_details) as ItemDetailsFragment

        val nextItem = listFragment.getNextItem()
        if (nextItem != null) {
            listFragment.setSelectedItem(nextItem)
            detailFragment.setItem(nextItem)
        }
    }

    override fun goToSettings() {
        //start new activity
    }

    override fun goToFeedback() {
        //start new activity
    }

    companion object {

        private val TAG_DETAILS = "tag_details"
        private val TAG_MASTER = "tag_master"
    }
}
