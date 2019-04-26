
//
//  ZBallSwitch.swift
//
//  Created by Tor Langballe on /14/11/15.
//
package com.github.torlangballe.cetrusandroid

class ZBallSwitch: ZCustomView {
    var on: Boolean
    var color = ZColor.White()

    constructor(value: Boolean = false) : super(name = "ZSwitch") {

        on = value
        minSize = ZSize(44.0, 44.0) * ZScreen.SoftScale
        HandlePressedInPosFunc = { pos  ->
            this!!.Value = !this!!.on
            ZPerformAfterDelay(0.1) {
                ZDebug.Print("Value Changed")
                HandleValueChangedFunc?.invoke()
            }
        }
        //        isAccessibilityElement = true
        canFocus = true
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        val path = ZPath()
        if (IsFocused) {
            ZFocus.Draw(canvas, rect = rect, corner = rect.size.w / 2.0)
        }
        val r = rect.Expanded(-6.0 * ZScreen.SoftScale)
        canvas.SetColor(color.OpacityChanged(if (Usable) 1.0 else 0.5))
        path.AddOval(inrect = r.Expanded(-1.0))
        canvas.StrokePath(path, width = 2.0 * ZScreen.SoftScale)
        if (on) {
            path.Empty()
            val radius = r.size.w / 2.0 - 4.0 * ZScreen.SoftScale
            path.ArcDegFromCenter(r.Center, radius = radius)
            canvas.FillPath(path)
        }
    }
    var Value: Boolean
        get() {
            return on
        }
        set(newValue) {
            on = newValue
            Expose()
        }

}
