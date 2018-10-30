package com.github.torlangballe.cetrusandroid

import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.content.res.Configuration
import android.view.View

enum class ZTransitionType(val rawValue: Int) {
    none(0), fromLeft(1), fromRight(2), fromTop(3), fromBottom(4), fade(5), reverse(6);
}

data class Attributes(
        var duration: Double = 0.0,
        var transition: ZTransitionType = ZTransitionType.none,
        var oldTransition: ZTransitionType = ZTransitionType.none,
        var lightContent: Boolean = true,
        var useableArea: Boolean = true,
        var portraitOnly:Boolean = false,
        var view: ZView? = null
) {}

var stack = mutableListOf<Attributes>()

fun ZPresentView(view: ZView, duration: Double = 0.5, transition: ZTransitionType = ZTransitionType.none, fadeToo: Boolean = false, oldTransition: ZTransitionType = ZTransitionType.reverse, makeFull: Boolean = false, useableArea: Boolean = false, deleteOld: Boolean = false, lightContent: Boolean = true, portraitOnly: Boolean? = null, done: (() -> Unit)? = null) {
    val vc = view as ZContainerView
    if (vc != null) {
        val a = Attributes()
        a.duration = duration
        a.transition = transition
        a.oldTransition = oldTransition
        a.lightContent = lightContent
        a.useableArea = !makeFull
        a.portraitOnly = portraitOnly ?: vc.portraitOnly
        a.view = view
        stack.append(a)
        view.SetAsFullView(useableArea = !makeFull)
        view.ArrangeChildren()
        zMainActivity!!.setContentView(view.View())
        val o = (if (a.portraitOnly) ActivityInfo.SCREEN_ORIENTATION_NOSENSOR else ActivityInfo.SCREEN_ORIENTATION_SENSOR)
        zMainActivity!!.setRequestedOrientation(o)
        ZScreen.StatusBarVisible = a.useableArea
        if (done != null) { // needs to be on callback after view presented or something
            done()
        }
    }
    // handle delete old and call HandleClosing on it...
}

fun ZPopTopView(namedView: String = "", animated: Boolean = true, overrideDuration: Float = -1f, overrideTransition: ZTransitionType = ZTransitionType.none, done: (() -> Unit)? = null, changeOrientation:Boolean = true) {
    var p = stack.popLast()
    var wasPortrait = false
    if (p != null) {
        wasPortrait = p.portraitOnly
        if (p.view != null) {
            val parent = p.view!!.View().parent
            if (parent != null) {
                val vg = parent as ViewGroup
                if (vg != null) {
                    parent.removeView(p.view!!.View())
                }
            }
            val v = p.view as? ZCustomView
            if (v != null) {
                v.HandleClosing()
            }
        }
    }
    if (stack.count() > 0) {
        p = stack.last()
        if (p != null) {
            ZScreen.StatusBarVisible = p.useableArea
            if (changeOrientation) {
                val o = zMainActivity!!.getResources().getConfiguration().orientation
                val isPortrait = (o == Configuration.ORIENTATION_PORTRAIT)

                if (p.portraitOnly && !isPortrait) {
                    zMainActivity!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    ZScreen.orientation = ZScreenLayout.portrait
                } else {
                    zMainActivity!!.setContentView(p.view!!.View())
                }
            }
        }
    }
}

fun zHandleOrientationChanged() {
    val s = stack.last()
    val o = zMainActivity!!.getResources().getConfiguration().orientation
    if (o == Configuration.ORIENTATION_PORTRAIT) {
        ZScreen.orientation = ZScreenLayout.portrait
    } else {
        ZScreen.orientation = ZScreenLayout.landscapeLeft
    }
    ZPopTopView(changeOrientation = false)
    ZPresentView(s.view!!, makeFull = !s.useableArea, deleteOld = true, lightContent = s.lightContent, portraitOnly = s.portraitOnly)
    val cv = s.view as ZContainerView
    if (cv != null) {
        cv.HandleRotation()
    }
}
/*
fun zHandleRotation(r:ZAlignment, angle:Int) {
    stack.forEachIndexed() { i, a ->
        var cv = a.view as? ZContainerView
        if (cv != null) {
            if (i == stack.lastIndex && !a.portraitOnly) {
                var c = ZScreen.Main.Center
                if (ZScreen.orientation == ZScreenLayout.landscapeRight ||ZScreen.orientation == ZScreenLayout.landscapeLeft) {
                    c.Swap()
                }
                c *= ZScreen.Scale
                cv.pivotX = c.x.toFloat()
                cv.pivotY = c.y.toFloat()
                val p = cv.parent as View
                if (p != null) {
                    p.rotation = -angle.toFloat()
                }
//                cv.parent = -angle.toFloat()
                val uArea = stack[i].useableArea
                cv.SetAsFullView(useableArea = uArea)
                val size = ZScreen.Main.size
                cv.Rect = ZRect(size = size)
                cv.RangeChildren() { view ->
                    val tv = view as? ZTableView
                    if (tv != null) {
                        tv.ReloadData()
                    }
                    true
                }
                cv.ArrangeChildren()
            }
        }
    }

}
*/
