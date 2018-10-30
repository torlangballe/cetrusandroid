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
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.util.TypedValue

private fun getDefaultDisplayMetrics() : DisplayMetrics {
    return zMainActivityContext!!.getResources().getDisplayMetrics()
}

private fun getDefaultDisplayRect() : ZRect {
    val d = getDefaultDisplayMetrics()
    val s = ZScreen.Scale
    var r = ZRect(0.0, 0.0, d.widthPixels.toDouble() / s, d.heightPixels.toDouble() / s)

    if (ZScreen.orientation == ZScreenLayout.landscapeRight || ZScreen.orientation == ZScreenLayout.landscapeLeft) {
//        r.size.Swap()
    }
    return r
}

enum class ZScreenLayout{ portrait, portraitUpsideDown, landscapeLeft, landscapeRight }

class ZScreen {
    companion object {
        var isLocked: Boolean = false
        var barVisible:Boolean = true
        var orientation: ZScreenLayout = ZScreenLayout.portrait

        var StatusBarVisible: Boolean
            get() {
                return barVisible
            }
            set(on) {
                barVisible = on
                val f = WindowManager.LayoutParams.FLAG_FULLSCREEN
                if (on) {
                    zMainActivity!!.window.clearFlags(f)
                } else {
                    zMainActivity!!.window.addFlags(f)
                }
            }

        val MainUsableRect: ZRect
            get() {
                var r = Main
                if (IsPortrait) {
                    r.size.h -= StatusBarHeight
                }
                return r
            }

        val Main: ZRect
            get() {
                return getDefaultDisplayRect()
            }

        var Scale =
                getDefaultDisplayMetrics().density.toDouble()

        val ActionBarHeight: Double
            get() {
                val tv = TypedValue()
                if (zMainActivityContext!!.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    val height = TypedValue.complexToDimensionPixelSize(tv.data, zMainActivityContext!!.getResources().getDisplayMetrics())
                    return height.toDouble() / ZScreen.Scale
                }
//                val tv = TypedValue()
//                zMainActivityContext!!.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)
//                val actionBarHeight = zMainActivity!!.getResources().getDimensionPixelSize(tv.resourceId)
                return 56.0
            }

        val StatusBarHeight: Double
            get() {
                val resources = zMainActivityContext!!.getResources();
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                var h = 0.0
                if (resourceId > 0) {
                    return resources.getDimensionPixelSize(resourceId).toDouble() / ZScreen.Scale
                } else {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24.0 else 25.0
                }
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
                return orientation == ZScreenLayout.portrait || orientation == ZScreenLayout.portraitUpsideDown
            }

        var KeyboardRect: ZRect? = null

        val HasSleepButtonOnSide: Boolean
            get() {
                ZNOTIMPLEMENTED()
                return false
            }

        fun ShowNetworkActivityIndicator(show: Boolean) {
            ZDebug.Print("ZScreen.ShowNetworkActivityIndicator not implemented")
            // TODO ZScreen.ShowNetworkActivityIndicator
            //            ZNOTIMPLEMENTED()
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
            return orientation
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
