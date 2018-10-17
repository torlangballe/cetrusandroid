//
//  ZPath.swift
//
//  Created by Tor Langballe on /21/8/18.
//

package com.github.torlangballe.cetrusandroid

fun ZPath.AddStar(rect: ZRect, points: Int, inRatio: Double = 0.3) {
    val c = rect.Center
    val delta = (rect.size.w / 2) - 1
    val inAmount = (1 - inRatio)
    for (i in 0 until points * 2) {
        val deg = (360 * i + 720).toDouble() / (points * 2).toDouble()
        var d = ZMath.AngleDegToPos(deg) * delta
        if ((i and 1) != 0) {
            d *= inAmount
        }
        val p = c + d
        if (i != 0) {
            LineTo(p)
        } else {
            MoveTo(p)
        }
    }
    Close()
}

fun ZPath.ArcDegFromCenter(center: ZPos, radius: Double, degStart: Double = 0.0, degEnd: Double = 359.999, radiusy: Double = 0.0) {
    var vradiusy = radiusy
    if (vradiusy == 0.0) {
        vradiusy = radius
    }
    val clockwise = (degStart < degEnd)
    val rect = ZRect(size = ZSize(radius * 2, vradiusy * 2)).Centered(center)
    ArcTo(rect, degStart = degStart, degDelta = degEnd - degStart, clockwise = clockwise)
}
