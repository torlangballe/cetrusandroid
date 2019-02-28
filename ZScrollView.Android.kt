//
//  ZScrollView.swift
//
//  Created by Tor Langballe on /13/11/15.
//
// Add single child. Stack or something

// https://tutorialwing.com/create-android-scrollview-programmatically-android/

package com.github.torlangballe.cetrusandroid

import android.graphics.Canvas
import android.widget.ScrollView

open class ZScrollView: ScrollView, ZView {
    override var objectName = "scrollview"
    override var isHighlighted: Boolean = false
    override var Usable:Boolean = true

    var drawHandler:((rect: ZRect, canvas: ZCanvas) -> Unit)? = null

    var child: ZContainerView? = null
    var margin = ZRect()

    constructor() : super(zMainActivityContext!!) {

    }

    companion object {
        fun ScrollViewToMakeItVisible(view: ZView) {
        }
    }

    override fun View() : ZNativeView =
            this

    fun SetContentOffset(offset: ZPos, animated: Boolean = true) {
        if (animated) {
            smoothScrollTo(offset.x.toInt(), offset.y.toInt())
        } else {
            scrollTo(offset.x.toInt(), offset.y.toInt())
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if (child != null) {
            val s = ZSize(LocalRect.size.w, 9000.0)
            val size = zConvertViewSizeThatFitstToZSize(child!!, s)
            size.w = s.w
            var r = ZRect(size = size)
            r += margin
            zLayoutViewAndScale(child!!, r)
            child!!.ArrangeChildren()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null && drawHandler != null) {
            super.onDraw(canvas)
            val scale = ZScreen.Scale
            val c = ZCanvas(canvas)
            c.PushState()
            val cs = ZSize(canvas.width, canvas.height) / scale
            canvas.scale(scale.toFloat(), scale.toFloat())
            val r = LocalRect
            drawHandler?.invoke(LocalRect, c)
            c.PopState()
        }
    }

    fun ScrollToMakeSubChildVisible(view: ZView, animated: Boolean) {
        var pos = GetPosFromMe(inView = view)
        pos.y -= scrollY
        val h = LocalRect.size.h
        val end = pos.y + view.LocalRect.size.h
        var to = ZPos()
        if (pos.y < 0.0) {
            to.y = pos.y
        } else if (end > h) {
            to.y = end  - h
        } else {
            return
        }
        to.y += scrollY
        SetContentOffset(to, animated)
    }

    fun ArrangeChildren() {
//        layoutSubviews()
    }

    fun SetChild(view: ZContainerView) {
        if (child != null) {
            child?.RemoveFromParent()
        }
        child = view
        addView(view)
    }

//    fun scrollViewWillBeginDragging(scrollView: UIScrollView) {
////        this.endEditing(true)
//    }
//
//    fun scrollViewDidScroll(scrollView: UIScrollView) {}
//
//    override fun touchesBegan(touches: Set<UITouch>, event: UIEvent?) {
//        this.endEditing(true)
//        super.touchesBegan(touches, with = event)
//    }
}
