
//  ZContainerView.swift
//  Created by Tor Langballe on /23/9/14.
package com.github.torlangballe.cetrusandroid

data class ZContainerCell(
        var alignment: ZAlignment,
        var margin: ZSize,
        var view: ZNativeView? = null,
        var maxSize: ZSize = ZSize(0.0, 0.0),
        var collapsed: Boolean = false,
        var free: Boolean = false,
        var handleTransition: ((size: ZSize, layout: ZScreenLayout, inRect: ZRect, alignRect: ZRect) -> ZRect?)? = null) {}

open class ZContainerView: ZCustomView {
    var cells: MutableList<ZContainerCell>
    var margin = ZRect()
    var liveArrange = false
    var portraitOnly = true

    constructor(name: String = "ZContainerView") // required
            : super(name = name) {
        cells = mutableListOf<ZContainerCell>()
        margin = ZRect()
        //        backgroundColor = UIColor.redColor()
    }

    fun AddCell(cell: ZContainerCell, index: Int? = null) : Int {
        if (index == -1) {
            cells.append(cell)
            zAddNativeView(cell.view!!, toParent = this)
            return cells.size - 1
        } else {
            cells.insert(cell, at = index!!)
            zAddNativeView(cell.view!!, toParent = this, index = index)
            return index!!
        }
    }

    open fun Add(view: ZNativeView, align: ZAlignment, marg: ZSize = ZSize(), maxSize: ZSize = ZSize(), index: Int = -1, free: Boolean = false) : Int =
            AddCell(ZContainerCell(alignment = align, margin = marg, view = view, maxSize = maxSize, collapsed = false, free = free, handleTransition = null), index = index)

    fun Contains(view: ZNativeView) : Boolean {
        for (c in cells) {
            if (c.view == view) {
                return true
            }
        }
        return false
    }

    override fun CalculateSize(total: ZSize) : ZSize =
            minSize

    fun SetAsFullView(useableArea: Boolean) {
        ZViewSetRect(this, rect = ZScreen.Main)
        minSize = ZScreen.Main.size
        if (!ZIsTVBox()) {
            val h = ZScreen.StatusBarHeight
            var r = Rect
            if (h > 20 && !ZScreen.HasNotch()) {
                r.size.h -= h
                ZViewSetRect(this, rect = r)
            } else if (useableArea) {
                margin.SetMinY(h.toDouble())
            }
        }
    }

    //    func Sort(_ sorter:(_ a:ZContainerCell, _ b:ZContainerCell) -> Bool) {
    open//        cells.sort(by: sorter)
    //        ArrangeChildren()
    //        Expose()
    //    }
    //
    fun ArrangeChildrenAnimated(onlyChild: ZView? = null) {
        ZAnimation.Do(duration = 0.6, animations = {   ->
            this.ArrangeChildren(onlyChild = onlyChild)
        })
    }

    fun arrangeChild(c: ZContainerCell, r: ZRect) {
        val ir = r.Expanded(c.margin * -2.0)
        val s = zConvertViewSizeThatFitstToZSize(c.view!!, sizeIn = ir.size)
        var rv = r.Align(s, align = c.alignment, marg = c.margin, maxSize = c.maxSize)
        if (c.handleTransition != null) {
            val r = c.handleTransition!!.invoke(s, ZScreen.Orientation(), r, rv)
            if (r != null) {
                rv = r
            }
        }
        zSetViewFrame(c.view!!, frame = rv)
    }

    open fun ArrangeChildren(onlyChild: ZView? = null) {
        HandleBeforeLayout()
        val r = ZRect(size = Rect.size) + margin
        for (c in cells) {
            (c.view as? ZCustomView)?.HandleBeforeLayout()
        }
        for (c in cells) {
            if (c.alignment != ZAlignment.None) {
                if (onlyChild == null || c.view == onlyChild!!.View()) {
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
                    zRemoveNativeViewFromParent(cells[i].view!!, detachFromContainer = false)
                } else {
                    zAddNativeView(cells[i].view!!, toParent = this)
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
            val v = cells[i].view as? ZView
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
                        val v = cv.FindViewWithName(name)
                        if (v != null) {
                            return v
                        }
                    }
                }
            }
        }
        return null
    }

    fun FindCellWithName(name: String) : Int? {
        for ((i, c) in cells.withIndex()) {
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
        for ((i, c) in cells.withIndex()) {
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
        zRemoveNativeViewFromParent(subView, detachFromContainer = false)
        DetachChild(subView)
    }

    fun RemoveAllChildren() {
        for (c in cells) {
            DetachChild(c.view!!)
            zRemoveNativeViewFromParent(c.view!!, detachFromContainer = false)
        }
    }

    open fun HandleRotation() {}

    fun DetachChild(subView: ZNativeView) {
        for ((i, c) in cells.withIndex()) {
            if (c.view == subView) {
                cells.removeAt(i)
                break
            }
        }
    }

    open fun HandleBackButton() {// only android has hardware back button...
    }
}
