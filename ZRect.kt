
//
//  ZRect.swift
//
//  Created by Tor Langballe on /23/9/14.
//
package com.github.torlangballe.cetrusandroid

import kotlin.math.*

data class ZRect(
        var pos: ZPos = ZPos(),
        var size: ZSize = ZSize()) {
    companion object {
        val Null: ZRect
            get() {
                return ZRect(0.0, 0.0, 0.0, 0.0)
            }

        fun MergeAll(rects: List<ZRect>) : List<ZRect> {
            var merged = true
            var rold = rects
            while (merged) {
                var rnew = mutableListOf<ZRect>()
                merged = false
                for ((i, r) in rold.withIndex()) {
                    var used = false
                    for (j in i + 1 until rold.size) {
                        if (r.Overlaps(rold[j].Expanded(4.0))) {
                            var n = rects[i]
                            n.UnionWith(rect = rold[j])
                            rnew.append(n)
                            merged = true
                            used = true
                        }
                    }
                    if (!used) {
                        rnew.append(r)
                    }
                }
                rold = rnew
            }
            return rold
        }
    }

    val IsNull: Boolean
        get() = pos.x == 0.0 && pos.y == 0.0 && size.w == 0.0 && size.h == 0.0
    val TopLeft: ZPos
        get() = Min
    val TopRight: ZPos
        get() = ZPos(Max.x, Min.y)
    val BottomLeft: ZPos
        get() = ZPos(Min.x, Max.y)
    val BottomRight: ZPos
        get() = Max
    var MaxPos: ZPos
        get() {
            return Max
        }
        set(newValue) {
            pos += (newValue - Max)
        }
    var Max: ZPos
        get() {
            return ZPos(pos.x + size.w, pos.y + size.h)
        }
        set(newValue) {
            size.w = newValue.x - pos.x
            size.h = newValue.y - pos.y
        }
    var Min: ZPos
        get() {
            return pos
        }
        set(newValue) {
            size.w += (pos.x - newValue.x)
            size.h += (pos.y - newValue.y)
            pos = newValue
        }

    fun SetMaxX(x: Double) {
        size.w = x - pos.x
    }

    fun SetMaxY(y: Double) {
        size.h = y - pos.y
    }

    fun SetMinX(x: Double) {
        size.w += (pos.x - x)
        pos.x = x
    }

    fun SetMinY(y: Double) {
        size.h += (pos.y - y)
        pos.y = y
    }
    var Center: ZPos
        get() {
            return pos + size / 2.0
        }
        set(newValue) {
            pos = newValue - size.GetPos() / 2.0
        }

    constructor(x0: Double, y0: Double, x1: Double, y1: Double) : this(pos = ZPos(x0, y0), size = ZSize(x1 - x0, y1 - y0)) {
    }

    constructor(min: ZPos, max: ZPos) : this(pos = min, size = ZSize(max.x - min.x, max.y - min.y)) {
    }

    constructor(rect: ZRect) : this(pos = rect.pos, size = rect.size) {
    }

    constructor(center: ZPos, radius: Double, radiusy: Double? = null) : this(rect = centerToRect(center = center, radius = radius, radiusy = radiusy)) {
    }

    fun Expanded(e: ZSize) : ZRect =
            ZRect(pos = pos - e.GetPos(), size = size + e * 2.0.toDouble())

    fun Expanded(n: Double) : ZRect =
            Expanded(ZSize(n, n))

    fun Centered(center: ZPos) : ZRect =
            ZRect(pos = center - size.GetPos() / 2.0, size = size)

    fun Overlaps(rect: ZRect) : Boolean =
            rect.Min.x < Max.x && rect.Min.y < Max.y && rect.Max.x > Min.x && rect.Max.y > Min.y

    fun Contains(pos: ZPos) : Boolean =
            pos.x >= Min.x && pos.x <= Max.x && pos.y >= Min.y && pos.y <= Max.y

    fun Align(s: ZSize, align: ZAlignment, marg: ZSize = ZSize(), maxSize: ZSize = ZSize()) : ZRect {
        var x: Double
        var y: Double
        var scalex: Double
        var scaley: Double
        var wa = (s.w).toDouble()
        var wf = (size.w).toDouble()
        //        if (align & (ZAlignment.HorShrink|ZAlignment.HorExpand)) {
        if (!(align and ZAlignment.MarginIsOffset)) {
            wf -= (marg.w).toDouble()
            if (align and ZAlignment.HorCenter) {
                wf -= (marg.w).toDouble()
            }
        }
        //        }
        var ha = (s.h).toDouble()
        var hf = (size.h).toDouble()
        //        if (align & (ZAlignment.VertShrink|ZAlignment.VertExpand)) {
        if (!(align and ZAlignment.MarginIsOffset)) {
            hf -= (marg.h * 2.0).toDouble()
        }
        if (align == ZAlignment.ScaleToFitProportionally) {
            val xratio = wf / wa
            val yratio = hf / ha
            var ns = size
            if (xratio != 1.0 || yratio != 1.0) {
                if (xratio > yratio) {
                    ns = ZSize(wf, ha * xratio)
                } else {
                    ns = ZSize(wa * yratio, hf)
                }
            }
            return ZRect(size = ns).Centered(Center)
        }
        if ((align and ZAlignment.HorExpand) && (align and ZAlignment.VertExpand)) {
            if ((align and ZAlignment.NonProp)) {
                wa = wf
                ha = hf
            } else {
                assert(!(align and ZAlignment.HorOut))
                scalex = wf / wa
                scaley = hf / ha
                if (scalex > 1 || scaley > 1) {
                    if ((scalex < scaley)) {
                        wa = wf
                        ha *= scalex
                    } else {
                        ha = hf
                        wa *= scaley
                    }
                }
            }
        } else if ((align and ZAlignment.NonProp)) {
            if ((align and ZAlignment.HorExpand) && wa < wf) {
                wa = wf
            } else if ((align and ZAlignment.VertExpand) && ha < hf) {
                ha = hf
            }
        }
        if ((align and ZAlignment.HorShrink) && (align and ZAlignment.VertShrink) && !(align and ZAlignment.NonProp)) {
            scalex = wf / wa
            scaley = hf / ha
            if ((align and ZAlignment.HorOut) && (align and ZAlignment.HorOut)) {
                if (scalex < 1 || scaley < 1) {
                    if ((scalex > scaley)) {
                        wa = wf
                        ha *= scalex
                    } else {
                        ha = hf
                        wa *= scaley
                    }
                }
            } else {
                if (scalex < 1 || scaley < 1) {
                    if ((scalex < scaley)) {
                        wa = wf
                        ha *= scalex
                    } else {
                        ha = hf
                        wa *= scaley
                    }
                }
            }
        } else if ((align and ZAlignment.HorShrink) && wa > wf) {
            wa = wf
        }
        //  else
        if ((align and ZAlignment.VertShrink) && ha > hf) {
            ha = hf
        }
        if (maxSize.w != 0.0) {
            wa = minOf(wa, (maxSize.w).toDouble())
        }
        if (maxSize.h != 0.0) {
            ha = minOf(ha, (maxSize.h).toDouble())
        }
        if ((align and ZAlignment.HorOut)) {
            if ((align and ZAlignment.Left)) {
                x = (pos.x - marg.w - s.w).toDouble()
            } else if ((align and ZAlignment.HorCenter)) {
                //                x = Double(pos.x) - wa / 2.0
                x = (pos.x).toDouble() + (wf - wa) / 2.0
            } else {
                x = (Max.x + marg.w).toDouble()
            }
        } else {
            if ((align and ZAlignment.Left)) {
                x = (pos.x + marg.w).toDouble()
            } else if ((align and ZAlignment.Right)) {
                x = (Max.x).toDouble() - wa - (marg.w).toDouble()
            } else {
                x = (pos.x).toDouble()
                if (!(align and ZAlignment.MarginIsOffset)) {
                    x += (marg.w).toDouble()
                }
                x = x + (wf - wa) / 2.0
                if (align and ZAlignment.MarginIsOffset) {
                    x += (marg.w).toDouble()
                }
            }
        }
        if ((align and ZAlignment.VertOut)) {
            if ((align and ZAlignment.Top)) {
                y = (pos.y - marg.h).toDouble() - ha
            } else if ((align and ZAlignment.VertCenter)) {
                //                y = Double(pos.y) - ha / 2.0;
                y = (pos.y).toDouble() + (hf - ha) / 2.0
            } else {
                y = (pos.y + marg.h).toDouble()
            }
        } else {
            if ((align and ZAlignment.Top)) {
                y = (pos.y + marg.h).toDouble()
            } else if ((align and ZAlignment.Bottom)) {
                y = (Max.y).toDouble() - ha - (marg.h).toDouble()
            } else {
                y = (pos.y).toDouble()
                if (!(align and ZAlignment.MarginIsOffset)) {
                    y += (marg.h).toDouble()
                }
                y = y + maxOf(0.0, hf - ha) / 2.0
                if (align and ZAlignment.MarginIsOffset) {
                    y += (marg.h).toDouble()
                }
            }
        }
        return ZRect(pos = ZPos(x, y), size = ZSize(wa, ha))
    }

    fun MoveInto(rect: ZRect) {
        pos.x = maxOf(pos.x, rect.pos.x)
        pos.y = maxOf(pos.y, rect.pos.y)
        MaxPos.x = minOf(MaxPos.x, rect.MaxPos.x)
        MaxPos.y = minOf(MaxPos.y, rect.MaxPos.y)
    }

    fun UnionWith(rect: ZRect) {
        if (!rect.IsNull) {
            if (IsNull) {
                pos = rect.pos
                size = rect.size
            } else {
                if (rect.Min.x < Min.x) {
                    Min.x = rect.Min.x
                }
                if (rect.Min.y < Min.y) {
                    Min.y = rect.Min.y
                }
                if (rect.Max.x > Max.x) {
                    Max.x = rect.Max.x
                }
                if (rect.Max.y > Max.y) {
                    Max.y = rect.Max.y
                }
            }
        }
    }

    fun UnionWith(pos: ZPos) {
        if (pos.x > Max.x) {
            Max.x = pos.x
        }
        if (pos.y > Max.y) {
            Max.y = pos.y
        }
        if (pos.x < Min.x) {
            Min.x = pos.x
        }
        if (pos.y < Min.y) {
            Min.y = pos.y
        }
    }

    operator fun plus(a: ZRect) : ZRect =
            ZRect(min = pos + a.pos, max = Max + a.Max)

    operator fun minus(a: ZRect) : ZRect =
            ZRect(min = pos - a.pos, max = Max - a.Max)

    operator fun div(a: ZSize) : ZRect =
            ZRect(min = Min / a.GetPos(), max = Max / a.GetPos())

    //    mutating func operator_plusAssign(_ a:ZRect)  { Min += (a.pos); Max += (a.Max) }
    //    mutating func operator_minusAssign(_ a:ZRect) { Min -= (a.pos); Max -= (a.Max) }
    operator fun plusAssign(a: ZPos) {
        pos += a
    }

    fun vminusAssign(a: ZPos) {
        pos -= a
    }

}

private fun centerToRect(center: ZPos, radius: Double, radiusy: Double? = null) : ZRect {
    var s = ZSize(radius, radius)
    if (radiusy != null) {
        s = ZSize(radius, radiusy!!)
    }
    return ZRect(pos = center - s.GetPos(), size = s * 2.0)
}

