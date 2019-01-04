
//
//  ZStack.swift
//
//  Created by Tor Langballe on /20/10/15.
//
package com.github.torlangballe.cetrusandroid

open class ZStackView: ZContainerView {
    var space = 6.0
    var vertical = false

    constructor(name: String = "stackview") : super(name = name) {
        //    userInteractionEnabled = false
    }

    private fun getCellFitSizeInTotal(total: ZSize, cell: ZContainerCell) : ZSize {
        var tot = total - cell.margin
        if (cell.alignment and ZAlignment.HorCenter) {
            tot.w -= cell.margin.w
        }
        if (cell.alignment and ZAlignment.VertCenter) {
            tot.h -= cell.margin.h
        }
        return tot
    }

    override public fun CalculateSize(total: ZSize) : ZSize {
        var s = nettoCalculateSize(total)
        s.Maximize(minSize)
        return s
    }

    fun nettoCalculateSize(total: ZSize) : ZSize {
        // can force size calc without needed result
        var s = ZSize(0, 0)
        for (c1 in cells) {
            if (!c1.collapsed && !c1.free) {
                val tot = getCellFitSizeInTotal(total = total, cell = c1)
                val cv = c1.view as? ZView
                var fs = ZSize(50, 50)
                if (cv != null) {
                    fs = cv.CalculateSize(tot)
                }
                //                var fs = zConvertViewSizeThatFitstToZSize(c1.view!, sizeIn:tot)
                var m = c1.margin
                if ((c1.alignment and ZAlignment.MarginIsOffset)) {
                    m = ZSize(0, 0)
                }
                s[vertical] += fs[vertical] + m[vertical]
                s[!vertical] = maxOf(s[!vertical], fs[!vertical] - m[!vertical])
                s[vertical] += space
            }
        }
        s -= margin.size
        if (cells.size > 0) {
            s[vertical] -= space
        }
        s[!vertical] = maxOf(s[!vertical], minSize[!vertical])
        return s
    }

    private fun handleAlign(size: ZSize, inRect: ZRect, a: ZAlignment, cell: ZContainerCell) : ZRect {
        var vr = inRect.Align(size, align = a, marg = cell.margin, maxSize = cell.maxSize)
        if (cell.handleTransition != null) {
            val r = cell.handleTransition!!.invoke(size, ZScreen.Orientation(), inRect, vr)
            if (r != null) {
                vr = r
            }
        }
        return vr
    }

    override open fun ArrangeChildren(onlyChild: ZView?) {
        var incs = 0
        var decs = 0
        var sizes = mutableMapOf<ZNativeView, ZSize>()
        var ashrink = ZAlignment.HorShrink
        var aexpand = ZAlignment.HorExpand
        var aless = ZAlignment.Left
        var amore = ZAlignment.Right
        var amid = ZAlignment.HorCenter or ZAlignment.MarginIsOffset
        HandleBeforeLayout()
        if ((vertical)) {
            ashrink = ZAlignment.VertShrink
            aexpand = ZAlignment.VertExpand
            aless = ZAlignment.Top
            amore = ZAlignment.Bottom
            amid = ZAlignment.VertCenter
        }
        for (c2 in cells) {
            if (!c2.free) {
                if (c2.collapsed) {
                    zRemoveViewFromSuper(c2.view!!)
                } else {
                    if ((c2.alignment and ashrink)) {
                        decs += 1
                    }
                    if ((c2.alignment and aexpand)) {
                        incs += 1
                    }
                }
            }
            val cv = c2.view as? ZCustomView
            if (cv != null) {
                cv.HandleBeforeLayout()
            }
        }
        var r = Rect
        r.pos = ZPos()
        // translate to 0,0 cause children are in parent
        r += margin
        for (c1 in cells) {
            if (c1.free) {
                arrangeChild(c1, r = r)
            }
        }
        val cn = r.Center[vertical]
        var cs = CalculateSize(r.size)[vertical]
        cs += margin.size[vertical]
        // subtracts margin, since we've already indented for that
        val diff = r.size[vertical] - cs
        var lastNoFreeIndex = -1
        for ((i, c3) in cells.withIndex()) {
            if (!c3.collapsed && !c3.free) {
                lastNoFreeIndex = i
                val tot = getCellFitSizeInTotal(total = r.size, cell = c3)
                var s = zConvertViewSizeThatFitstToZSize(c3.view!!, sizeIn = tot)
                if (decs > 0 && (c3.alignment and ashrink) && diff != 0.0) {
                    s[vertical] += diff / decs.toDouble()
                } else if (incs > 0 && (c3.alignment and aexpand) && diff != 0.0) {
                    s[vertical] += diff / incs.toDouble()
                }
                sizes[c3.view!!] = s
            }
        }
        var centerDim = 0.0
        var firstCenter = true
        for ((i, c4) in cells.withIndex()) {
            if (!c4.collapsed && !c4.free) {
                if ((c4.alignment and (amore or aless))) {
                    var a = c4.alignment
                    if (i != lastNoFreeIndex) {
                        a = a.Subtracted(ZAlignment.Expand[vertical])
                    }
                    val vr = handleAlign(size = sizes[c4.view!!]!!, inRect = r, a = a, cell = c4)
                    //                ZDebug.Print("alignx:", (c4.view as! ZView).objectName, vr)
                    if (onlyChild == null || onlyChild!!.View() == c4.view) {
                        zSetViewFrame(c4.view!!, frame = vr, layout = true)
                    }
                    if ((c4.alignment and aless)) {
                        val m = maxOf(r.Min[vertical], vr.Max[vertical] + space)
                        if (vertical) {
                            r.SetMinY(m)
                        } else {
                            r.SetMinX(m)
                        }
                    }
                    if ((c4.alignment and amore)) {
                        val m = minOf(r.Max[vertical], vr.pos[vertical] - space)
                        if (vertical) {
                            r.SetMaxY(m)
                        } else {
                            r.SetMaxX(m)
                        }
                    }
                    val v = c4.view as? ZContainerView
                    if (v != null) {
                        v.ArrangeChildren()
                    } else {//! (c4.view as? ZCustomView)?.HandleAfterLayout()
                    }
                } else {
                    centerDim += sizes[c4.view!!]!![vertical]
                    if (!firstCenter) {
                        centerDim += space
                    }
                    firstCenter = false
                }
            }
        }
        if (vertical) {
            r.SetMinY(maxOf(r.Min.y, cn - centerDim / 2))
        } else {
            r.SetMinX(maxOf(r.Min.x, cn - centerDim / 2))
        }
        if (vertical) {
            r.SetMaxY(minOf(r.Max.y, cn + centerDim / 2))
        } else {
            r.SetMaxX(minOf(r.Max.x, cn + centerDim / 2))
        }
        for (c5 in cells) {
            if (!c5.collapsed && (c5.alignment and amid) && !c5.free) {
                // .reversed()
                val a = c5.alignment.Subtracted(amid) or aless
                val vr = handleAlign(size = sizes[c5.view!!]!!, inRect = r, a = a, cell = c5)
                if (onlyChild == null || onlyChild!!.View() == c5.view) {
                    zSetViewFrame(c5.view!!, frame = vr, layout = true)
                }
                //                ZDebug.Print("alignm:", (c5.view as! ZView).objectName, vr)
                r.pos[vertical] = vr.Max[vertical] + space
                val v = c5.view as? ZContainerView
                if (v != null) {
                    v.ArrangeChildren()
                } else {//!          (c5.view as? ZCustomView)?.HandleAfterLayout()
                }
            }
        }
        HandleAfterLayout()
    }
}

fun ZHStackView(name: String = "ZHStackView", space: Double = 6.0) : ZStackView {
    val h = ZStackView(name = name)
    h.space = space
    return h
}

fun ZVStackView(name: String = "ZVStackView", space: Double = 6.0) : ZStackView {
    val v = ZStackView(name = name)
    v.vertical = true
    v.space = space
    return v
}

open class ZColumnStack: ZStackView {
    var vstack: ZStackView? = null
    var max: Int = 0

    constructor(max: Int, horSpace: Double) : super(name = "zcolumnstack") {

        this.max = max
        space = horSpace
        vertical = false
    }

    override open fun Add(view: ZNativeView, align: ZAlignment, marg: ZSize, maxSize: ZSize, index: Int, free: Boolean) : Int {
        if (vstack == null || vstack!!.cells.size == max) {
            vstack = ZVStackView(space = space)
            return super.Add(vstack!!, align = ZAlignment.Left or ZAlignment.Bottom, marg = ZSize(), maxSize = ZSize(), index = -1, free = false)
        }
        return vstack!!.Add(view, align = align, marg = marg, maxSize = maxSize, index = index, free = free)
        // need all args specified for kotlin super call
    }
}
