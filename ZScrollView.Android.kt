//
//  ZScrollView.swift
//
//  Created by Tor Langballe on /13/11/15.
//
// Add single child. Stack or something

// https://tutorialwing.com/create-android-scrollview-programmatically-android/

package com.github.torlangballe.cetrusandroid

import android.widget.ScrollView

class ZScrollView: ScrollView, ZView {
    override var objectName = "scrollview"
    override var isHighlighted: Boolean = false
    override var Usable:Boolean = true

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
//        setContentOffset(offset.GetCGPoint(), animated = animated)
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
