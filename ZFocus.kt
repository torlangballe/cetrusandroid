
//
//  ZFocus.Swift
//
//  Created by Tor Langballe on 11/19/18.
//
package com.github.torlangballe.cetrusandroid

data class ZFocus (val _dummy: Int = 0) {
    companion object {
        var color = ZColor(r = 0.5, g = 0.5, b = 1.0)

        fun Draw(canvas: ZCanvas, rect: ZRect, corner: Double = 7.0, width: Double = 4.0, opacity: Double = 1.0) {
            val w = width * ZScreen.SoftScale
            val r = rect.Expanded(-width / 2 * ZScreen.SoftScale)
            val path = ZPath(rect = r, corner = ZSize(corner, corner))
            canvas.SetColor(color.OpacityChanged(opacity))
            canvas.StrokePath(path, width = w)
        }
    }
}
