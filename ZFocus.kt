
//
//  ZFocus.Swift
//
//  Created by Tor Langballe on 11/19/18.
//
package com.github.torlangballe.cetrusandroid

data class ZFocus (val _dummy: Int = 0) {
    companion object {
        var color = ZColor(r = 0.5, g = 0.5, b = 1.0)

        fun Draw(canvas: ZCanvas, rect: ZRect, corner: Double = 7.0) {
            if (ZIsTVBox()) {
                var w = 4.0 * ZScreen.SoftScale
                val r = rect.Expanded(-2.0 * ZScreen.SoftScale)
                val path = ZPath(rect = r, corner = ZSize(corner, corner) * ZScreen.SoftScale)
                canvas.SetColor(color)
                canvas.StrokePath(path, width = w)
            }
        }
    }
}
