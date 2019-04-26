//
//  ZTableView.Android.kt
//
//  Created by Tor Langballe on /05/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.content.Context
import android.widget.ListView
import android.graphics.*
import android.view.ViewGroup
import android.view.View
import android.widget.*

// TODO: RecyclerView is new modern performant thing to maybe use later?

interface ZTableViewDelegate {
    fun TableViewGetRowCount() : Int { return 0 }
    fun TableViewGetHeightOfItem(index: Int) : Double { return 10.0 }
    fun TableViewSetupCell(cellSize: ZSize, index: Int) : ZCustomView? { return null }
//    fun UpdateRow(index: Int) { }
    fun HandleRowSelected(index: Int) { }
    fun GetAccessibilityForCell(index: Int, prefix: String) : List<ZAccessibilty> { return listOf<ZAccessibilty>() }
}

enum class ZTableViewRowAnimation { fade }

class ZTableView : ZScrollView, ZTableViewDelegate { // , View.OnFocusChangeListener
    override var objectName = "ZTableView"
    var first = true
    var tableRowBackgroundColor = ZColor.Black()
    var scrolling = false
    var margins = ZSize(0, 0)
    var spacing = 0.0
    var focusedRow:Int? = null

    var selectionIndex: Int = 0
    var owner: ZTableViewDelegate? = null
    var selectable = true
    var deleteHandler: (() -> Unit)? = null
    var selectedColor = ZColor()

    var stack = ZVStackView()

    constructor(name: String = "customview") : super() {
        selectionIndex = -1
        Rect = ZRect(0.0, 0.0, 100.0, 300.0)
        SetChild(stack)

        focusable = android.view.View.NOT_FOCUSABLE

//        onFocusChangeListener = this

//        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        stack.space = spacing
        stack.margin = ZRect(margins.w, margins.h, -margins.w, -margins.h)
        ReloadData()
        super.onLayout(p0, p1, p2, p3, p4)
    }

    fun ExposeRows() {
        invalidate()
    }

    fun UpdateVisibleRows(animate: Boolean = true) {
    }

    fun ScrollToMakeRowVisible(row: Int, animated: Boolean = true) {
        if (row < 0 || row >= owner!!.TableViewGetRowCount()) {
            return
        }
        val c = owner!!.TableViewGetRowCount()
        var h = margins.h
        val top = scrollY
        for (i in 0 .. c - 1) {
            val e = h + owner!!.TableViewGetHeightOfItem(i)
            if (i == row) {
                if (h < 0) {
                    scrollTo(0, h.toInt())
                } else if (e > LocalRect.size.h) {
                    scrollTo(0, (e - LocalRect.size.h).toInt())
                }
                break
            }
            h = e + spacing
        }
    }

    fun IsFocused(rowView:ZCustomView) : Boolean {
        return rowView.isFocused
    }

    fun UpdateRow(row: Int, recalculate: Boolean = true) {
        val c = owner!!.TableViewGetRowCount()
        var s = ZSize(LocalRect.size.w - margins.w * 2, owner!!.TableViewGetHeightOfItem(row))
        val v = owner!!.TableViewSetupCell(s, row)
        v!!.id = row

        v?.focusable = FOCUSABLE
        if (row < stack.cells.count()) {
            zRemoveNativeViewFromParent(stack.cells[row].view!!, detachFromContainer = false)
            stack.cells[row].view = v
            zAddNativeView(v!!, toParent = stack, index = row)
        } else {
            stack.Add(v!!, ZAlignment.Left or ZAlignment.Top or ZAlignment.HorExpand or ZAlignment.NonProp)
            if (c > 1) {
                s.h += spacing
            }
            if (recalculate) {
                var r = stack.LocalRect
                s += margins * 2.0
                r.SetMaxY(r.Max.y + s.h)
                stack.Rect = r
                stack.ArrangeChildren()
            }
        }
        v!!.Expose()
    }

    private fun getFirstVisibleRowIndex(fromTop: Boolean) : Int? {
        val c = owner!!.TableViewGetRowCount()
        var h = margins.h
        val top = scrollY
        for (i in 0 .. c - 1) {
            val e = h + owner!!.TableViewGetHeightOfItem(i)
            if (e >= top) {
                if (fromTop) {
                    return i
                }
            }
            if (h >= top + LocalRect.size.h) {
                return i - 1
            }
            h = e + spacing
        }
        return null
    }
/*
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            focusable = android.view.View.NOT_FOCUSABLE
            descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
            val i = getFirstVisibleRowIndex(fromTop = true)
            if (i != null) {
                val v = GetRowViewFromIndex(i!!)
                if (v != null) {
                    v.Focus()
                }
            }
        } else {
            focusable = android.view.View.FOCUSABLE
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
    }
*/
    fun ReloadData(animate: Boolean = false) {
        val c = owner!!.TableViewGetRowCount()
        var h = 0.0
        var s = ZSize(LocalRect.size.w - margins.w * 2, 0.0)

        for (i in 0 .. c - 1) {
            s.h = owner!!.TableViewGetHeightOfItem(i)
            h += s.h
            if (i != 0) {
                h += spacing
            }
            UpdateRow(i)
        }
        s.h = h
        s += margins * 2.0
        stack.Rect = ZRect(size = s)
        stack.ArrangeChildren()
    }

    fun MoveRow(fromIndex: Int, toIndex: Int) {
        ZNOTIMPLEMENTED()
    }

    fun GetRowViewFromIndex(index: Int) : ZView? {
        if (index >= stack.cells.count()) {
            return null
        }
        val v = stack.cells[index].view as ZView
        return v
    }

    fun GetParentTableViewFromRow(child: ZContainerView) : ZTableView {
        var p: View? = child.View()
        while (p != null) {
            val v = p as? ZTableView
            if (v != null) {
                return v
            }
            p = p?.parent as View
        }
        throw Exception("ZTableView.GetParentTableViewFromRow failed!")
    }

    fun GetIndexFromRowView(view: ZContainerView) : Int {
        val v = GetParentTableViewFromRow(view)
        return v.GetIndexFromRowView(view) ?: -1
    }

    fun Select(row: Int) { // https://stackoverflow.com/questions/10788688/programmatically-select-item-listview-in-android
    }

    fun DeleteChildRow(index: Int, animation: ZTableViewRowAnimation = ZTableViewRowAnimation.fade) {
        removeViewAt(index)
    }

}
