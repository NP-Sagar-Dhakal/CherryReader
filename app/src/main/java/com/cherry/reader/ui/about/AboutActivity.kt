/*
 * Copyright (c) 2012-2018 Frederic Julian
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package com.cherry.reader.ui.about

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cherry.reader.R
import com.cherry.reader.utils.setupTheme
import com.vansuita.materialabout.builder.AboutBuilder


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()

        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val view = AboutBuilder.with(this)
            .setBackgroundColor(R.color.primary_background)
            .setPhoto(R.mipmap.profile_picture)
            .setCover(R.mipmap.profile_cover)
            .setName("Sagar Dhakal")
            .setSubTitle("Android Developer | Web Developer\nCherry Digital Services")
            .setBrief(R.string.about_screen_info)
            .setAppIcon(R.drawable.ic_statusbar_rss)
            .setAppName(R.string.app_name)
            .addGitHubLink("NP-Sagar-Dhakal")
            .addEmailLink("cherrydigital.care@gmail.com")
            .addFacebookLink("cherrydigitalservices")
            .addFiveStarsAction()
            .addShareAction(R.string.app_name)
            .setWrapScrollView(true)
            .setLinksAnimated(true)
            .setShowAsCard(true)
            .build()

        setContentView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }
}