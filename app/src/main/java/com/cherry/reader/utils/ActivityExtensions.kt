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

package com.cherry.reader.utils

import android.app.Activity
import com.cherry.reader.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.inputMethodManager

fun Activity.closeKeyboard() {
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.setupTheme() {
    doAsync {
        setTheme(
            R.style.Feed_Theme
        )
    }
}

fun Activity.setupNoActionBarTheme() {
    doAsync {
        setTheme(
            R.style.Feed_Theme_NoActionBar
        )
    }
}
