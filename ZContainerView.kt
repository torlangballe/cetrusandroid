//
//  ZContainerView.Android.kt
//  Created by Tor Langballe on /20/08/18.

package com.github.torlangballe.cetrusandroid

import android.view.View
import android.view.ViewGroup

// TODO: This contains some complex layout code it would be good to move out to shared extension later

data class ZContainerCell(
        var alignment: ZAlignment = ZAlignment.Left,
        var margin: ZSize = ZSize(0, 0),
        var view: View? = null,
        var maxSize: ZSize = ZSize(0, 0),
        var collapsed:Boolean = false,
        var free:Boolean = false,
        var handleTransition: ((size: ZSize, layout: ZScreenLayout, inRect: ZRect, alignRect: ZRect) -> ZRect?)? = null
        )

open class ZContainerView: ZCustomView {
    var cells = mutableListOf<ZContainerCell>()
    var margin = ZRect()
    var liveArrange = false
    var portraitOnly = true

    constructor(name: String = "ZContainerView") // required
        : super(name = name) {
        cells = mutableListOf<ZContainerCell>()
        margin = ZRect()
    }

    fun VG() : ViewGroup {
        return View() as ViewGroup
    }

    fun AddCell(cell: ZContainerCell, index: Int = -1) : Int {
        if (index == -1) {
            cells.append(cell)
            this.VG().addView(cell.view)
            return cells.count() - 1
        } else {
            cells.add(index, cell)
            VG().addView(cell.view, index)
            return index
        }
    }

    open fun Add(view: ZNativeView, align: ZAlignment, marg: ZSize = ZSize(), maxSize: ZSize = ZSize(), index: Int = -1, free: Boolean = false) : Int {
        return AddCell(ZContainerCell(alignment = align, margin = marg, view = view, maxSize = maxSize, collapsed = false, free = free), index = index)
    }

    fun Contains(view: ZNativeView) : Boolean {
        for (c in cells) {
            if (c.view == view) {
                return true
            }
        }
        return false
    }

    override fun CalculateSize(total: ZSize) : ZSize {
        return minSize
    }

    fun SetAsFullView(useableArea: Boolean) {
        var r = ZScreen.Main
        if (useableArea) {
            r = ZScreen.MainUsableRect
        }
        minSize = r.size
        Rect = r
    }

    fun Sort(sorter: (a: ZContainerCell, b: ZContainerCell) -> Boolean) {
//        cells.sort(by = sorter)
//        ArrangeChildren()
//        Expose()
    }

    fun ArrangeChildrenAnimated(onlyChild: ZView? = null) {
        ZAnimation.Do(duration = 0.6, animations = { ->
            this.ArrangeChildren(onlyChild = onlyChild)
        })
    }

    fun arrangeChild(c: ZContainerCell, r: ZRect) {
        val ir = r.Expanded(c.margin * -2.0)
        val s = zConvertViewSizeThatFitstToZSize(c.view!!, ir.size)
        var rv = r.Align(s, align = c.alignment, marg = c.margin, maxSize = c.maxSize)
        if (c.handleTransition != null) {
            val rnew = c.handleTransition!!.invoke(s, ZScreen.Orientation(), r, rv)
            if (rnew != null) {
                rv = rnew
            }
        }
        zLayoutViewAndScale(c.view!!, rv)
    }

    open fun ArrangeChildren(onlyChild: ZView? = null) {
        HandleBeforeLayout()
        val r = ZRect(size = Rect.size) + margin
        for (c in cells) {
            (c.view as? ZCustomView)?.HandleBeforeLayout()
        }
        for (c in cells) {
            if (c.alignment != ZAlignment.None) {
                if (onlyChild == null || c.view == onlyChild.View()) {
                    arrangeChild(c, r = r)
                }
                val v = c.view as? ZContainerView
                if (v != null) {
                    v.ArrangeChildren(onlyChild = onlyChild)
                }
            }
        }
        HandleAfterLayout()
        for (c in cells) {
            (c.view as? ZCustomView)?.HandleAfterLayout()
        }
    }

    fun CollapseChild(view: ZView, collapse: Boolean = true, arrange: Boolean = false) : Boolean {
        val i = FindCellWithView(view)
        if (i != null) {
            val changed = (cells[i].collapsed != collapse)
            if (changed) {
                if (collapse) {
                    zRemoveViewFromSuper(cells[i].view!!)
                } else {
                    VG().addView(cells[i].view!!)
                }
            }
            cells[i].collapsed = collapse
            if (arrange) {
                ArrangeChildren()
            }
            return changed
        }
        return false
    }

    fun CollapseChildWithName(name: String, collapse: Boolean = true, arrange: Boolean = false) : Boolean {
        val v = FindViewWithName(name)
        if (v != null) {
            return CollapseChild(v, collapse = collapse, arrange = arrange)
        }
        return false
    }
/*
    override fun didAddSubview(subview: ZNativeView) {
        super.didAddSubview(subview)
        if (liveArrange) {
            ArrangeChildren()
        }
    }
*/

    fun RangeChildren(subViews: Boolean = false, foreach: (ZView) -> Boolean) {
        for (c in cells) {
            val v = c.view as? ZView
            if (v != null) {
                if (!foreach(v)) {
                    return
                }
                if (subViews) {
                    val cv = v as? ZContainerView
                    if (cv != null) {
                        cv.RangeChildren(subViews = subViews, foreach = foreach)
                    }
                }
            }
        }
    }

    fun FindViewWithName(name: String) : ZView? {
        val i = FindCellWithName(name)
        if (i != null) {
            val v = cells[i].view!! as? ZView
            if (v != null) {
                return v
            }
        }
        return null
    }

    fun RemoveNamedChild(name: String, all: Boolean = false) : Boolean {
        for (c in cells) {
            val v = c.view as? ZView
            if (v != null && v.objectName == name) {
                RemoveChild(c.view!!)
                if (!all) {
                    return true
                }
            }
        }
        return false
    }

    fun FindViewWithName(name: String, recursive: Boolean = false) : ZView? {
        for (c in cells) {
            val v = c.view as? ZView
            if (v != null) {
                if (v.objectName == name) {
                    return v
                }
                if (recursive) {
                    val cv = v as? ZContainerView
                    if (cv != null) {
                        val fv = cv.FindViewWithName(name)
                        if (fv != null) {
                            return fv
                        }
                    }
                }
            }
        }
        return null
    }

    fun FindCellWithName(name: String) : Int? {
        cells.forEachIndexed { i, c ->
            val v = c.view as? ZView
            if (v != null) {
                if (v.objectName == name) {
                    return i
                }
            }
        }
        return null
    }

    fun FindCellWithView(view: ZView) : Int? {
        cells.forEachIndexed { i, c ->
            val v = c.view as? ZView
            if (v != null) {
                if (v.View() == view.View()) {
                    return i
                }
            }
        }
        return null
    }

    fun RemoveChild(subView: ZNativeView) {
        zRemoveViewFromSuper(subView)
        DetachChild(subView)
    }

    fun RemoveAllChildren() {
        for (c in cells) {
            DetachChild(c.view!!)
            zRemoveViewFromSuper(c.view!!)
        }
    }

    fun DetachChild(subView: ZNativeView) {
        val i = cells.indexWhere { it.view == subView }
        if (i != null) {
            cells.removeAt(i)
        }
    }

    open fun HandleRotation() {
    }

    open fun HandleBackButton() {
        RangeChildren(subViews = true) { view: ZView ->
            val t = view as? ZTitleBar
            if (t != null && t!!.closeButton != null) {
                t?.HandlePressed(t!!.closeButton!!, ZPos(0.0, 0.0))
            }
            true
        }
    }
}
