//
//  ZActivityIndicator.Android.kt
//
//  Created by Tor Langballe on /12/09/18.
//

package com.github.torlangballe.cetrusandroid

import android.widget.ProgressBar
import android.support.v4.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable

class ZActivityIndicator: ProgressBar, ZView {
    override fun View() : UIView = this
    override var objectName = "activity"
    override var isHighlighted: Boolean = false
    override var Usable:Boolean = true

    constructor(big: Boolean = true, dark: Boolean = false) : super(zMainActivityContext, null,
            if (big) android.R.attr.progressBarStyle else android.R.attr.progressBarStyleSmall) {
        isIndeterminate = true
        val col = if (dark) ZColor(white = 0.1) else ZColor(white = 0.9)

        val wrapDrawable = DrawableCompat.wrap(getIndeterminateDrawable())
        DrawableCompat.setTint(wrapDrawable, col.color.toArgb())
        setIndeterminateDrawable(DrawableCompat.unwrap<Drawable>(wrapDrawable))
    }

    fun Start(start: Boolean = true, whenVisible: Boolean = true) {
    }
}

fun ZAddActivityToContainer(on: Boolean, container:ZContainerView, align:ZAlignment, marg:ZSize = ZSize(0.0, 0.0)) {
    if (on) {
        if (container.FindCellWithName("activity") != null) {
            return
        }
        val v = ZActivityIndicator(big = false)
        container.Add(v, align = ZAlignment.Right or ZAlignment.Top, marg = ZSize(0.0, 0.0))
        container.ArrangeChildren()
        v.Start()
    } else {
        container.RemoveNamedChild("activity")
        container.ArrangeChildren()
    }
}

