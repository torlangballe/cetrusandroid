
//
//  ZProgressBar.swift
//
//  Created by Tor Langballe on /14/12/17.
//
package com.github.torlangballe.cetrusandroid

class ZProgressBar: ZCustomView {
    var height: Double
    var width: Double
    var color: ZColor
    private var value: Double = 0.0
    private var timer = ZRepeater()
    var Value: Double
        get() {
            return value
        }
        set(newValue) {
            value = newValue
            Expose()
        }

    constructor(height: Double = 2.0, width: Double = 100.0, color: ZColor = ZColor.Blue(), value: Double = 0.0) : super(name = "progress") {

        this.height = height
        this.width = width
        this.color = color
        this.value = value
        minSize = ZSize(width, height)
        SetBackgroundColor(ZColor.Clear())
        SetCornerRadius(height / 2)
    }

    override fun HandleClosing() {
        ZDebug.Print("HandleClosing progress")
        timer.Stop()
    }

    fun SetUpdate(update: (() -> Double)? = null) {
        if (update != null) {
            timer.Set(0.2) {   ->
                val v = update!!.invoke()
                if (v == -1.0) {
                    return@Set false
                }
                this.Value = v
                true
            }
        } else {
            timer.Stop()
        }
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        var r = rect
        r.Max.x = rect.size.w * value.toDouble()
        val path = ZPath(rect = r, corner = ZSize(height / 2, height / 2))
        canvas.SetColor(color)
        canvas.FillPath(path)
    }
}
