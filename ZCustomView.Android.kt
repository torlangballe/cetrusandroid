//  ZCustomView.swift
//
//  Created by Tor Langballe on /21/10/15.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Canvas
import android.view.GestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.MotionEvent


interface ZCustomViewDelegate {
    fun DrawInRect(rect: ZRect, canvas: ZCanvas)
}

open class ZCustomView: ViewGroup, ZView, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    override var objectName = ""
    override var isHighlighted: Boolean = false
    var touchInfo = ZTouchInfo()
    var handleValueChangedFunc: (() -> Unit)? = null
    var minSize = ZSize(0, 0)
    var drawHandler: ((rect: ZRect, canvas: ZCanvas, view: ZCustomView) -> Unit)? = null // lateinit
    var foregroundColor = ZColor.Black()
    var canFocus = false
    var valueTarget: ZCustomView? = null
    var timers = mutableListOf<ZTimerBase>()
    var amScrolling = false
    var xCornerRadius = 0.0
    var xBgColor = ZColor.Clear()
    var xStrokeColor = ZColor.Clear()
    var xStrokeWidth = 0.0

    var HandlePressedInPosFunc: ((pos: ZPos) -> Unit)?
        get() {
            return touchInfo.handlePressedInPosFunc
        }
        set(newValue) {
            touchInfo.handlePressedInPosFunc = newValue
//            isUserInteractionEnabled = true
            isAccessibilityElement = true
//            accessibilityTraits |= UIAccessibilityTraitButton
            if (ZIsTVBox()) {
                AddGestureTo(this, type = ZGestureType.tap)
            }
        }

    fun SetHandleValueChangedFunc(handler: () -> Unit) {
        handleValueChangedFunc = handler
        this.AddTarget(this, forEventType = ZControlEventType.valueChanged)
    }

    open fun AddTarget(t: ZCustomView?, forEventType: ZControlEventType) {
        when (forEventType) {
            ZControlEventType.pressed -> touchInfo.tapTarget = t
            ZControlEventType.valueChanged -> valueTarget = t
        }
        View().isClickable = true
    }

    override var Usable: Boolean
        get() {
            return View().isEnabled
        }
        set(u) {
            View().isEnabled = u
            // accessibilityTraits = if (isEnabled) UIAccessibilityTraitNone else UIAccessibilityTraitNotEnabled
            val e = View().isEnabled
            Expose()
        }

    override fun View(): ZNativeView =
            this

//    fun Control() : UIControl =
//            this

    constructor(name: String = "customview") : super(zMainActivityContext) {
        var layout = LayoutParams(10, 10)
        setLayoutParams(layout)
        setBackgroundColor(0x000000FF) // is clear
//        setBackgroundColor(0x00000000) // is clear
        Expose()
        objectName = name
        minSize = ZSize(10, 10)
        foregroundColor = ZColor(color = ZColor.Black().color)
        touchInfo.doPressed = { pos: ZPos ->
            doPressed(pos)
        }
    }

    override fun CalculateSize(total: ZSize): ZSize =
            minSize

    override fun onDown(e: MotionEvent): Boolean {
        return handleTouch(this, e, touchInfo)
    }
    override fun onShowPress(e: MotionEvent) {
        return
    }
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return handleTouch(this, e, touchInfo)
    }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean { return true }

    override fun onLongPress(e: MotionEvent) {
        isHighlighted = false
        Expose()
        handleGesture(this, ZGestureType.longpress, touchInfo, e, null, ZPos(0.0, 0.0), 1)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val v = ZPos(velocityX.toDouble(), velocityY.toDouble())
        return handleGesture(this, ZGestureType.swipe, touchInfo, e1, e2, v, 1)
    }
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return handleGesture(this, ZGestureType.tap, touchInfo, e, null, ZPos(0.0, 0.0),1)
    }
    override fun onDoubleTap(e: MotionEvent): Boolean { return true }
    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_UP) {
            isHighlighted = false
            Expose()
            return handleGesture(this, ZGestureType.tap, touchInfo, e, null, ZPos(0.0, 0.0), 2)
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var insize = ZSize(widthMeasureSpec.toDouble(), heightMeasureSpec.toDouble())
//        if (insize.w > 9000 || insize.h > 9000) {
//            val s = LocalRect.size ZMath.Ceil* ZScreen.Scale
//            setMeasuredDimension(s.w.toInt(), s.h.toInt())
//            return
//        }
        val s = CalculateSize(insize) * ZScreen.Scale
        setMeasuredDimension(ZMath.Ceil(s.w).toInt(), ZMath.Ceil(s.h).toInt())
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
//        val sin = ZSize((p3 - p1).toDouble(), (p4 - p2).toDouble())
//        val s = CalculateSize(sin)
    }

    open fun HandlePressed(sender: ZView, pos: ZPos) {
    }

    open fun HandleTouched(sender: ZView, state: ZGestureState, pos: ZPos, inside: Boolean): Boolean =
            false

    open fun HandleValueChanged(sender: ZView) {}

    open fun HandleValueChangedEnded(sender: ZView) {}

    open fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        drawHandler?.invoke(rect, canvas, this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            super.onDraw(canvas)
            val scale = ZScreen.Scale
            val c = ZCanvas(canvas)
            c.PushState()
            val cs = ZSize(canvas.width, canvas.height) / scale
            canvas.scale(scale.toFloat(), scale.toFloat())
            val r = LocalRect

            if (xCornerRadius != 0.0 || xStrokeWidth != 0.0 || xBgColor.tileImage != null) {
                c.SetColor(xBgColor)
                val path = ZPath(rect = r, corner = ZSize(xCornerRadius, xCornerRadius))
                c.FillPath(path)

                if (xStrokeWidth != 0.0) {
                    c.SetColor(xStrokeColor)
                    c.StrokePath(path, width = xStrokeWidth)
                }
            }
            DrawInRect(r, canvas = c)
            c.PopState()
        }
    }

    fun SetFGColor(color: ZColor) {
        foregroundColor = color
        Expose()
    }

    fun GetPosFromMe(pos: ZPos, inView: ZNativeView): ZPos {
        ZNOTIMPLEMENTED()
//        val cgpos = this.convert(pos.GetCGPoint(), to = inView)
//        return ZPos(cgpos)
        return ZPos()
    }

    fun GetPosToMe(pos: ZPos, inView: ZNativeView): ZPos {
//        val cgpos = inView.convert(pos.GetCGPoint(), to = this)
//        return ZPos(cgpos)
        return ZPos()
    }

    fun GetViewsRectInMyCoordinates(view: ZView): ZRect {
        return ZRect()
//            ZRect(convert(CGRect(origin = CGPoint(), size = view.View().frame.size), from = view.View()))
    }

    fun getStateColor(col: ZColor): ZColor {
        var vcol = col
        if (isHighlighted) {
            val g = col.GrayScale
            if (g < 0.5) {
                vcol = col.Mix(ZColor.White(), amount = 0.5)
            } else {
                vcol = col.Mix(ZColor.Black(), amount = 0.5)
            }
        }
        if (!isEnabled) {
            vcol = vcol.OpacityChanged(0.3)
        }
        return vcol
    }

    open fun HandleGestureType(type: ZGestureType, view: ZView, pos: ZPos, delta: ZPos, state: ZGestureState, taps: Int, touches: Int, dir: ZAlignment, velocity: ZPos, gvalue: Float, name: String): Boolean {
        return true
    }

    private fun doPressed(p: ZPos?) {
        // maybe this can go
        var pos = LocalRect.Center
        if (p != null) {
            pos = p
        }
        if (touchInfo.handlePressedInPosFunc != null) {
            touchInfo.handlePressedInPosFunc!!.invoke(pos)
        } else {
            touchInfo.tapTarget!!.HandlePressed(this, pos = pos)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (touchInfo.gestureDetector != null) {
            touchInfo.gestureDetector!!.onTouchEvent(event)
            super.onTouchEvent(event)
            return true
        }
        return handleTouch(this, event, touchInfo)
        return super.onTouchEvent(event)
    }

    open fun HandleBeforeLayout() {
        Expose()
    }

    open fun HandleAfterLayout() {
        Expose()
    }

    open fun HandleTransitionedToSize() {}

    open fun HandleClosing() {
        for (t in timers) {
            t.Stop()
        }
        timers.removeAll()
        val cv = this as? ZContainerView
        if (cv != null) {
            cv.RangeChildren() { view ->
                val ccv = view as? ZCustomView
                if (ccv != null) {
                    ccv.HandleClosing()
                }
//                else val ssv = view as? ZScrollView
//                if (ssv != null) {
//                    ssv.child?.HandleClosing()
//                }
                val tv = view as? ZTableView
                if (tv != null) {
                    tv.scrolling = false
                }
                true
            }
        }
    }

    open fun HandleOpening() {
        Focus()
    }

    open fun HandleRevealedAgain() {
        Focus()
    }

    fun RefreshAccessibility() {//!
    }

    fun Activate(activate: Boolean) {// like being activated/deactivated for first time
    }

    private fun updateBGAndCorner() {
        if (xCornerRadius != 0.0 || xStrokeWidth != 0.0 || xBgColor.tileImage != null) {
            View().setBackgroundColor(ZColor.Clear().color.toArgb())
        } else {
            View().setBackgroundColor(xBgColor.color.toArgb())
        }
        Expose()
    }

    override fun SetCornerRadius(radius: Double) {
        xCornerRadius = radius
        updateBGAndCorner()
    }

    override fun SetBackgroundColor(color: ZColor) {
        xBgColor = color
        updateBGAndCorner()
    }

    override fun SetStroke(width: Double, color: ZColor) {
        xStrokeColor = color
        xStrokeWidth = width
        updateBGAndCorner()
    }

    /*
    override val canBecomeFirstResponder: Boolean
        get() = canFocus

    override fun motionEnded(motion: UIEventSubtype, event: UIEvent?) {
        if (motion == .motionShake) {
            mainZApp?.HandleShake()
        }
    }
*/
    fun AddGestureTo(view: ZView, type: ZGestureType, taps: Int = 1, touches: Int = 1, duration: Double = 0.8, movement: Double = 10.0, dir: ZAlignment = ZAlignment.None) {
        val gl = view.View() as? GestureDetector.OnGestureListener
        val gd = view.View() as? GestureDetector.OnDoubleTapListener

        view.View().isClickable = true
        var tinfo = ZTouchInfo()
        val vc = view.View() as? ZCustomView
        if (vc != null) {
            tinfo = vc.touchInfo
        }
        if (gl != null && gd != null) {
            tinfo = addGestureWithTouchInfoTo(gl, gd, this, tinfo, type, taps, touches, duration, movement, dir)
            if (vc != null) {
                vc.touchInfo = tinfo
                if (taps > 1) {
                    vc.touchInfo.wantsMultiTap = true
                }
            }
        }
    }

    fun Rotate(degrees: Double) {
//        val r = ZMath.DegToRad(degrees)
//        this.transform = CGAffineTransform(rotationAngle = CGFloat(r))
    }
}

/*
private fun addGesture(g: UIGestureRecognizer, view: ZView, handler: ZCustomView) {
    view.View().isUserInteractionEnabled = true
    g.delaysTouchesEnded = true
    g.delegate = handler
}
*/

//fun sizeThatFits(v:View, s: ZSize) : ZSize {
//    return zConvertViewSizeThatFitstToZSize(view, s)
//}
//

fun zSetViewFrame(v:View, frame: ZRect, layout: Boolean = false) {
    val scale = ZScreen.Scale
    v.left = ZMath.Floor(frame.Min.x * scale).toInt()
    v.top = ZMath.Floor(frame.Min.y * scale).toInt()
    v.right = ZMath.Ceil(frame.Max.x * scale).toInt()
    v.bottom = ZMath.Ceil(frame.Max.y * scale).toInt()
    if (layout) {
        v.layout(ZMath.Floor(frame.Min.x * scale).toInt(), ZMath.Floor(frame.Min.y * scale).toInt(), ZMath.Ceil(frame.Max.x * scale).toInt(), ZMath.Ceil(frame.Max.y * scale).toInt())
    }
}

fun zLayoutViewAndScale(view:View, frame: ZRect) {
    val scale = ZScreen.Scale
    view.layout(ZMath.Floor(frame.Min.x * scale).toInt(), ZMath.Floor(frame.Min.y * scale).toInt(), ZMath.Ceil(frame.Max.x * scale).toInt(), ZMath.Ceil(frame.Max.y * scale).toInt())
}

fun zRemoveViewFromSuper(view:View) {
    val p = view.parent
    if (p is ViewGroup) {
        p.removeView(view)
    }
}

fun zConvertViewSizeThatFitstToZSize(view:View, sizeIn: ZSize) : ZSize {
//    val cv = view as? ZView
//    if (cv != null) {
//        return cv.CalculateSize(sizeIn)
//    }
    view.measure(sizeIn.w.toInt(), sizeIn.h.toInt())
    val scale = ZScreen.Scale
    return ZSize(view.measuredWidth.toDouble() / scale, view.measuredHeight.toDouble() / scale)
}

