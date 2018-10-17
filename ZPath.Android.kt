//
//  ZPath.Android.kt
//
//  Created by Tor Langballe on /21/10/15.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Path
import android.graphics.RectF
class ZPath {
    enum class LineType {
        square,
        round,
        butt
    }
    enum class PartType {
        move,
        line,
        quadCurveTo,
        curveTo,
        close
    }
    var path: Path

    constructor() {
        path = Path()
    }

    constructor(p: ZPath) {
        path = p.path
    }

    constructor(rect: ZRect, corner: ZSize = ZSize(), oval: Boolean = false) {
        path = Path()
        if (oval) {
            AddOval(inrect = rect)
        } else {
            AddRect(rect, corner = corner)
        }
    }

    fun Copy(p: ZPath) {
        path = Path(p.path)
    }

    fun Empty() {
        path.reset()
    }

    fun IsEmpty() : Boolean =
            path.isEmpty()

    fun GetRect() : ZRect {
        if (IsEmpty()) {
            return ZRect()
        }
        var r = RectF()
        path.computeBounds(r, true)
        return ZAndroidRectFToZRect(r)
    }

    fun AddOval(inrect: ZRect) {
        val r = ZRectToAndroidRectF(inrect)
        path.addOval(r, Path.Direction.CW)
    }

    fun GetPos() : ZPos {
        ZNOTIMPLEMENTED()
        return ZPos(0, 0)
    }

    fun MoveTo(pos: ZPos) {
        path.moveTo(pos.x.toFloat(), pos.y.toFloat())
    }

    fun LineTo(pos: ZPos) {
        path.lineTo(pos.x.toFloat(), pos.y.toFloat())
    }

    fun BezierTo(c1: ZPos, c2: ZPos, end: ZPos = ZPos(-999999, 0)) {
        var e: ZPos
        if (end.x == -999999.0) {
            e = c2
        } else {
            e = end
        }
        path.cubicTo(c1.x.toFloat(), c1.y.toFloat(), c2.x.toFloat(), c2.y.toFloat(), e.x.toFloat(), e.y.toFloat())
    }

    fun ArcTo(rect: ZRect, degStart: Double = 0.0, degDelta: Double = 350.999, clockwise: Boolean = false) {
        val r = ZRectToAndroidRectF(rect)
        val forceMoveTo = true

//        path.addRect(r, Path.Direction.CW)
        path.arcTo(r, degStart.toFloat(), degDelta.toFloat(), forceMoveTo)
        path.close()
    }

    fun Close() {
        path.close()
    }

    fun AddPath(p: ZPath, join: Boolean, m: ZMatrix?) {
        ZNOTIMPLEMENTED()
    }

    fun Rotated(deg: Double, origin: ZPos? = null) : ZPath {
        ZNOTIMPLEMENTED()
        return ZPath()
    }

    fun ForEachPart(forPart: (part: PartType, coords:Array<ZPos>) -> Unit) {
        ZNOTIMPLEMENTED()
    }

    fun AddRect(rect: ZRect, corner: ZSize = ZSize()) {
        val r = ZRectToAndroidRectF(rect)
        if (rect.size.w != 0.0 && rect.size.h != 0.0) {
            if (corner.IsNull() || rect.size.w == 0.0 || rect.size.h == 0.0) {
                path.addRect(r, Path.Direction.CW)
            } else {
                var c = corner
                val m = minOf(rect.size.w, rect.size.h) / 2
                c.w = minOf(c.w, m)
                c.h = minOf(c.h, m)
                path.addRoundRect(r, corner.w.toFloat(), corner.h.toFloat(), Path.Direction.CW)
            }
        }
    }
}
