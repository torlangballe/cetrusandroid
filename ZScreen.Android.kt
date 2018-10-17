//
//  ZScreen.Android.kt
//
//  Created by Tor Langballe on /20/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View

private fun getDefaultDisplayMetrics() : DisplayMetrics {
    return zMainActivityContext!!.getResources().getDisplayMetrics()
}

private fun getDefaultDisplayRect() : ZRect {
    val d = getDefaultDisplayMetrics()
    val s = ZScreen.Scale
    return ZRect(0.0, 0.0, d.widthPixels.toDouble() / s, d.heightPixels.toDouble() / s)
}

enum class ZScreenLayout{ portrait, portraitUpsideDown, landscapeLeft, landscapeRight }

class ZScreen {
    companion object {

        var isLocked: Boolean = false
        var MainUsableRect = getDefaultDisplayRect() // TODO: actually remove bar etc
        var Scale = getDefaultDisplayMetrics().density.toDouble()
        var KeyboardRect: ZRect? = null
        val Main: ZRect
            get() {
                return getDefaultDisplayRect()
            }
        val StatusBarHeight: Double
            get() {
                // TODO:
                return 24.0
            }
        val IsTall: Boolean
            get() {
                return Main.size.h > 568
            }
        val IsWide: Boolean
            get() {
                return Main.size.w > 320
            }
        val IsPortrait: Boolean
            get() {
                return Main.size.h > Main.size.w
            }

        fun ShowNetworkActivityIndicator(show: Boolean) {
            ZDebug.Print("ZScreen.ShowNetworkActivityIndicator not implemented")
            // TODO ZScreen.ShowNetworkActivityIndicator
//            ZNOTIMPLEMENTED()
        }
        val HasSleepButtonOnSide: Boolean
            get() {
                ZNOTIMPLEMENTED()
                return false
            }
        var StatusBarVisible: Boolean
            get() {
                val rectangle = Rect()
                zMainActivity!!.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                return statusBarHeight != 0
            }
            set(newValue) {
                zMainActivity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }

        fun SetStatusBarForLightContent(light: Boolean = true) {
            var col = Color.WHITE
            if (light) {
                col = Color.BLACK
            }
            zMainActivity!!.getWindow().setStatusBarColor(col)
        }

        fun EnableIdle(on: Boolean = true) {
            ZNOTIMPLEMENTED()
        }

        fun Orientation() : ZScreenLayout {
            return when (zMainActivity!!.getResources().getConfiguration().orientation) {
                    ORIENTATION_LANDSCAPE -> ZScreenLayout.landscapeLeft
                    ORIENTATION_PORTRAIT -> ZScreenLayout.portrait
                    else -> ZScreenLayout.portrait
            }
        }

        fun HasNotch() : Boolean {
            ZNOTIMPLEMENTED()
            return false
        }

        fun HasSwipeUpAtBottom() : Boolean {
            ZNOTIMPLEMENTED()
            return false
        }
    }
}
