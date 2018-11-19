
//
//  ZView.swift
//
//  Created by Tor Langballe on 11/19/18.
//

package com.github.torlangballe.cetrusandroid

data class ZFocus (val _dummy: Int = 0) {
    companion object {
        var color = ZColor(r = 0.5, g = 0.5, b = 1.0)

        fun Draw(canvas: ZCanvas, rect: ZRect, corner: Double = 5.0) {
            var w = 5.0
            val r = rect.Expanded(-2.0)
            var opacity = 0.4
            val path = ZPath(rect = r, corner = ZSize(corner, corner))
            while (w > 0) {
                val c = color.OpacityChanged(opacity)
                canvas.SetColor(c)
                canvas.StrokePath(path, width = w)
                w -= 2.0
                opacity += 0.3
            }
        }
    }
}
