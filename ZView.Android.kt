//
//  ZViewAndroid.swift
//  Zed
//
//  Created by Tor Langballe on /20/7/2018.
//

// https://stackoverflow.com/questions/34470703/generic-extending-class-and-implements-interface-in-kotlin
// https://developer.android.com/reference/android/view/View#SizePaddingMargins

package com.github.torlangballe.cetrusandroid

import android.support.v4.view.GestureDetectorCompat
import android.view.*

enum class ZGestureType(dayNumber: Int) { tap(1), longpress(2), pan(4), pinch(8), swipe(16), rotation(32) }
enum class ZGestureState(state: Int)    { began(1), ended(2), changed(4), possible(8), canceled(16), failed(32) }
typealias ZNativeView = View

fun nativeViewFrame(v: ZNativeView) : ZRect {
    val scale = ZScreen.Scale
    val left = ZMath.Floor(v.left.toDouble() / scale)
    val top = ZMath.Floor(v.top.toDouble() / scale)
    val right = ZMath.Ceil(v.right.toDouble() / scale)
    val bottom = ZMath.Ceil(v.bottom.toDouble() / scale)
    return ZRect(left, top, right, bottom)
}

var collapsedViews = mutableMapOf<ZNativeView, ZContainerView>()

interface ZView {
    var objectName: String
    var isHighlighted: Boolean
    var Usable: Boolean
    fun SetOpaque(opaque:Boolean) {}
    fun View(): ZNativeView

    var accessibilityLabel : String?
        get()  { return "" }
        set(l) { }

    var isAccessibilityElement: Boolean
        get()  { return false }
        set(i) { }

    var Rect: ZRect // todo: this needs to account for padding
        get() = nativeViewFrame(View())
        set(r) {
            val scale = ZScreen.Scale
            View().left = ZMath.Floor(r.Min.x * scale).toInt()
            View().right = ZMath.Ceil(r.Max.x * scale).toInt()
            View().top = ZMath.Floor(r.Min.y * scale).toInt()
            View().bottom = ZMath.Ceil(r.Max.y * scale).toInt()
        }

    val LocalRect: ZRect
        get() {
            return ZRect(ZPos(), Rect.size)
        }

    val Alpha: Double
        get() {
            return View().alpha.toDouble()
        }

    fun SetAlpha(a:Double) {
        View().alpha = a.toFloat()
    }

    fun CalculateSize(total: ZSize) : ZSize =
            ZSize(10.0, 10.0)

    fun Pop(animated: Boolean = true, done: (() -> Unit)? = null) {
        ZPopTopView(animated = animated, done = done)
    }

    fun Show(show: Boolean = true) {
        var v = View.GONE //INVISIBLE
        if (show) {
            v = View.VISIBLE
        }
        View().setVisibility(v)
    }

    fun ZView.IsVisible(): Boolean {
        if (View().visibility == View.VISIBLE) {
            return true
        }
        return false
    }

    fun ZView.GetBoundsRect(): ZRect {
        val v = View()
        val pos = ZPos(v.left.toFloat(), v.top.toFloat())
        val s = ZSize(v.measuredWidth.toFloat(), v.measuredHeight.toFloat())
        val scale = ZScreen.Scale
        return ZRect(pos / scale, s / scale)
    }

    fun ZView.Child(path: String): ZNativeView? =
            null // not done yet!!!

    fun ZView.DumpTree() {
        dumpZNativeViewTree(View(), padding = "")
    }

    var ZView.Usable: Boolean
        get() {
            return View().isEnabled
        }
        set(enabled:Boolean) {
            View().isEnabled = enabled
            View().alpha = if (enabled) 1.0f else 0.3f
        }

    fun ZView.RemoveFromParent() {
        zRemoveNativeViewFromParent(View(), detachFromContainer = true)
    }

    fun Unfocus() {
        View().focusSearch(View.FOCUS_RIGHT).requestFocus()
    }

    fun Focus() {
        View().requestFocus()
    }

    fun Parent(): ZView? {
        return View().parent as? ZView
    }

    fun SetBackgroundColor(color: ZColor) {
        View().setBackgroundColor(color.color.toArgb())
    }

    fun SetDropShadow(delta: ZSize = ZSize(3, 3), blur: Float = 3f, color: ZColor = ZColor.Black()) {
        // not implemented yet!
        // View().elevation = xx perhaps only clean way...
    }

    fun SetDropShadowOff() {
        // not implemented yet, see above
    }

    fun SetCornerRadius(radius: Double) {
        // not implemented yet!
    }

    fun SetStroke(width: Double, color: ZColor) {
        // not implemented yet!
    }

    fun Expose(fadeIn: Float = 0f) {
        View().invalidate()
    }

    fun Scale(scale: Double) {
        // not implemented yet!
    }

    fun GetContainer(): ZContainerView? {
        val p = View().parent as? ZContainerView
        if (p != null) {
            return p
        }
        val c = collapsedViews[this.View()]
        if (c != null) {
            return c
        }
        return null
    }

    fun CollapseInParent(collapse: Boolean = true, arrange: Boolean = false) {
        val c = GetContainer()
        if (c != null) {
            if (collapse) {
                collapsedViews[this.View()] = c
            } else {
                collapsedViews.remove(key = this.View())
            }
            c.CollapseChild(this, collapse = collapse, arrange = arrange)
        }
    }

    fun GetContainerAndCellIndex(): Pair<ZContainerView, Int>? {
        val container = GetContainer()
        if (container != null) {
            for ((i, c) in container.cells.withIndex()) {
                if (c.view == View()) {
                    return Pair(container, i)
                }
            }
        }
        return null
    }

    fun GetViewRenderedAsImage(): ZImage? {
        // not implemeneted yet
        return null
    }

    fun dumpZNativeViewTree(view: ZNativeView, padding: String) {
        val v = view as? ZView
        if (v != null) {
            ZDebug.Print(padding + v.objectName)
        } else {
            ZDebug.Print(padding + view::class)
        }
        if (view is ViewGroup) {
            for (c in getViewChildren(view)) {
                dumpZNativeViewTree(c, padding = padding + "  ")
            }
        }
    }

    fun getZNativeViewChild(view: ZNativeView, path: String): ZNativeView? {
        var part = ""
        var vpath = path

        fun popPath(): Boolean {
            var parts = ZStr.Split(path, sep = "/")
            val p = parts.firstOrNull()
            if (p != null) {
                part = p
                parts = parts.dropLast(1)
                vpath = ZStr.Join(parts, sep = "/")
                return true
            }
            return false
        }
        if (popPath()) {
            if (part == "*") {
                while (!vpath.isEmpty()) {
                    val v = getZNativeViewChild(view, path = vpath)
                    if (v != null) {
                        return v
                    }
                }
            }
            val i = part.toInt()
            val upper = (part == part.uppercased())
            for (c in getViewChildren(view)) {
                if (i != null) {
                    if (c.tag == i!!) {
                        return (c as ZNativeView)
                    }
                    return null
                } else if (upper) {
                    if (part == c::class.toString()) {
                        if (!vpath.isEmpty()) {
                            return getZNativeViewChild(c, path = vpath)
                        }
                        return c
                    }
                } else {
                    val v = c as? ZView
                    if (v != null) {
                        if (v.objectName == part) {
                            return c
                        }
                    }
                }
            }
        }
        return null
    }

    fun GetPosFromMe(pos: ZPos = ZPos(0, 0), inView: ZView): ZPos {
        var v = inView as View
        var p = pos
        while (v != null) {
            if (v === this) {
                break
            }
            p += ZPos(v.left, v.top)
            v = v.parent as View
        }
        return p
    }

    fun GetPosToMe(pos: ZPos, inView: ZView): ZPos {
//        val cgpos = inView.convert(pos.GetCGPoint(), to = this)
//        return ZPos(cgpos)
        return ZPos()
    }


}

fun zRemoveNativeViewFromParent(view:ZNativeView, detachFromContainer:Boolean) {
    val p = view.parent
    if (p == null) {
        return
    }
    if (p is ViewGroup) {
        if (detachFromContainer && p is ZContainerView) {
            p.DetachChild(view)
        }
        p.removeView(view)
    }
}

fun zAddNativeView(view: ZNativeView, toParent: ZNativeView, index: Int? = null) {
    val vg = toParent as? ViewGroup
    if (vg != null) {
        if (index != null) {
           vg.addView(view, index!!)
        } else {
            vg.addView(view)
        }
    }
}

fun dumpZNativeViewTree(view: ZNativeView, padding: String) {
}

fun getZNativeViewChild(view: ZNativeView, path: String) : ZNativeView? {
    return null
}

fun getViewChildren(view: ZNativeView) : List<ZNativeView> {
    var all = mutableListOf<View>()
    if (view is ViewGroup) {
        for (i in 0 .. view.childCount) {
            all.append(view.getChildAt(i))
        }
    }
    return all
}

data class ZGestureInfo(val dummy:Int = 0) {
    var target: ZCustomView? = null
    var type: ZGestureType = ZGestureType.swipe
    var taps: Int = 1
    var touches: Int = 1
}

data class ZTouchInfo(val dummy:Int = 0) {
    var touchDownRepeatSecs:Double = 0.0
    val touchDownRepeatTimer: ZRepeater = ZRepeater()
    var handlePressedInPosFunc: ((pos: ZPos) -> Unit)? = null
    var tapTarget: ZCustomView? = null
    var doPressed: ((pos: ZPos) -> Unit)? = null
    var gestures = mutableListOf<ZGestureInfo>()
    var gestureDetector: GestureDetectorCompat? = null
    var wantsMultiTap = false
}

fun handleTouch(view: ZView, event: MotionEvent, info: ZTouchInfo): Boolean {
    val pos = ZPos(event.x, event.y) / ZScreen.Scale
    // put your code in here to handle the event
    if (event.action == MotionEvent.ACTION_DOWN) {
        if (info.tapTarget != null || info.handlePressedInPosFunc != null) {
            view.isHighlighted = true
            view.Expose()
            info.tapTarget?.HandleTouched(view, state = ZGestureState.began, pos = pos, inside = true)
            if (info.touchDownRepeatSecs != 0.0) {
                info.touchDownRepeatTimer.Set(info.touchDownRepeatSecs) { ->
                    info.doPressed!!(pos)
                    return@Set true
                }
            }
        }
        return true
    }
    if (event.action == MotionEvent.ACTION_UP) {
        view.isHighlighted = false
        if (info.tapTarget != null || info.handlePressedInPosFunc != null) {
            val inside = view.LocalRect.Contains(pos)
            if (info.tapTarget == null || !info.tapTarget!!.HandleTouched(view, state = ZGestureState.ended, pos = pos, inside = inside)) {
                ZPerformAfterDelay(0.05) { ->
                    view.Expose()
                }
                if (inside && info.doPressed != null) {
                    info.doPressed!!(pos)
                }
            }
            info.touchDownRepeatTimer.Stop()
        }
    } else if (event.action == MotionEvent.ACTION_MOVE) {
        if (info.tapTarget != null) {
            val inside = view.Rect.Contains(pos)
//            if (info.handlePressedInPosFunc != null) {
//                info.handlePressedInPosFunc!!(pos)
            //} else
            if (!info.tapTarget!!.HandleTouched(view, state = ZGestureState.changed, pos = pos, inside = inside)) {
//                if (info.tapTarget != null) {
//                    info.tapTarget?.HandlePressed(view, pos)
//                }
            }
//            info.touchDownRepeatTimer.Stop()
        }
    }
    return false
}

fun handleGesture(view: ZView, type: ZGestureType, touchInfo: ZTouchInfo, e1:MotionEvent, e2:MotionEvent?, velocity:ZPos, taps:Int) : Boolean {
    for (g in touchInfo.gestures) {
        if (g.type == type && g.taps == taps && g.touches == e1.pointerCount) {
            val scale = ZScreen.Scale
            val pos = (ZPos(e1.x, e1.y) - view.Rect.pos) / scale
            val delta = ZPos(0.0, 0.0)
            var align = ZAlignment.None
            var state = ZGestureState.ended
            val gvalue = 0f
            val name = ""
            if (type == ZGestureType.longpress) {
                state = ZGestureState.began
            }
            if (type == ZGestureType.swipe) {
                val SWIPE_THRESHOLD = 100
                val SWIPE_VELOCITY_THRESHOLD = 100

                val diffY = e2!!.y - e1.y
                val diffX = e2!!.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocity.x) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0.0) {
                        align = ZAlignment.Right
                    } else {
                        align = ZAlignment.Left
                    }
                }  else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocity.y) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0.0) {
                        align = ZAlignment.Bottom
                    } else {
                        align = ZAlignment.Top
                    }
                } else {
                    return false
                }
            }
            return g.target?.HandleGestureType(type, view = view, pos = pos, delta = delta, state = state, taps = taps, touches = e1.pointerCount, dir = align, velocity = velocity, gvalue = gvalue, name = name) ?: false
        }
    }
    return false
}

fun addGestureWithTouchInfoTo(gl: GestureDetector.OnGestureListener, gd: GestureDetector.OnDoubleTapListener, target: ZCustomView, touchInfo: ZTouchInfo, type: ZGestureType, taps: Int = 1, touches: Int = 1, duration: Double = 0.8, movement: Double = 10.0, dir: ZAlignment = ZAlignment.None) : ZTouchInfo {
    val ginfo = ZGestureInfo()
    ginfo.type = type
    ginfo.taps = taps
    ginfo.touches = touches
    ginfo.target = target

    touchInfo.gestures.append(ginfo)

//!!!        view.View().setClickable(true)
    if (touchInfo.gestureDetector == null) {
        touchInfo.gestureDetector = GestureDetectorCompat(zMainActivityContext!!, gl)
    }
//            view.View().isUserInteractionEnabled = true
    if (type == ZGestureType.tap) {
        touchInfo.gestureDetector!!.setOnDoubleTapListener(gd)
    } else if (type == ZGestureType.longpress) {
        touchInfo.gestureDetector!!.setIsLongpressEnabled(true)
    } else if (type == ZGestureType.swipe) {
    } else if (type == ZGestureType.rotation) {

    } else if (type == ZGestureType.pan) {

    } else if (type == ZGestureType.pinch) {

    }
    return touchInfo
}

fun ZViewSetRect(view:ZView, rect:ZRect) {
    zSetViewFrame(view.View(), rect)
}

interface ZViewHandler {
    fun HandleClose(sender: ZView)
}

/*
var View.accessibilityLabel : String?
    get()  { return "" }
    set(l) { }

var View.isAccessibilityElement: Boolean
    get()  { return false }
    set(i) { }


*/