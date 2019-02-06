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
    fun TableViewGetHeightOfItem(index: ZTableIndex) : Double { return 10.0 }
    fun TableViewSetupCell(cellSize: ZSize, index: ZTableIndex) : ZCustomView? { return null }
    fun UpdateRow(index: Int) { }
    fun HandleRowSelected(index: ZTableIndex) { }
    fun GetAccessibilityForCell(index: ZTableIndex, prefix: String) : List<ZAccessibilty> { return listOf<ZAccessibilty>() }
}

enum class ZTableViewRowAnimation { fade }
data class ZTableIndex(var row:Int = -1) { }

class ZTableView : ListView, ZView, ZTableViewDelegate {
    override var objectName = "ZTableView"
    var first = true
    var tableRowBackgroundColor = ZColor.Black()
    var scrolling = false
    var drawHandler: ((rect: ZRect, canvas: ZCanvas) -> Unit)? = null // lateinit ?
    var margins = ZSize(0, 0)
    var spacing = 0.0
    var focusedRow:Int? = null

    private var ladapter: listAdapter? = null

    override var isHighlighted:Boolean = false
    override var Usable = true
    override fun View() : ZNativeView = this

    var selectionIndex: ZTableIndex = ZTableIndex()
    var owner: ZTableViewDelegate? = null
    var selectable = true
    var deleteHandler: (() -> Unit)? = null
    var selectedColor = ZColor()

    constructor(name: String = "customview") : super(zMainActivityContext!!) {
        selectionIndex = ZTableIndex(-1)
        isFocusableInTouchMode = true

        layoutMode = ViewGroup.LayoutParams.WRAP_CONTENT // was to make getView() work, might not be needed

        Rect = ZRect(0.0, 0.0, 100.0, 300.0)

        this.setOnItemClickListener { parent, view, position, id ->
            val index = ZTableIndex(position)
            owner!!.HandleRowSelected(index)
            selectionIndex = index
        }
        focusable = View.NOT_FOCUSABLE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (first && owner != null) {
            ladapter = listAdapter(zMainActivityContext!!, owner!!, this)
            adapter = ladapter
        }
        if (first) {
            if (selectionIndex.row != -1) {
                Select(selectionIndex.row)
            }
            if (selectable) {
                choiceMode = ListView.CHOICE_MODE_SINGLE
            }
            first = false
        }
        val w = ZMath.Ceil(MeasureSpec.getSize(widthMeasureSpec).toDouble() * ZScreen.Scale).toInt()
        val h = ZMath.Ceil(MeasureSpec.getSize(heightMeasureSpec).toDouble() * ZScreen.Scale).toInt()

        setMeasuredDimension(w, h)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        super.onLayout(p0, p1, p2, p3, p4)
    }

    override fun onDraw(canvas: Canvas?) {
        if (first && owner != null) {
        }
        first = false
        if (canvas != null) { // && drawHandler != null) {
            val scale = ZScreen.Scale
            val c = ZCanvas(canvas!!)
            c.PushState()
//            val cs = ZSize(canvas.width, canvas.height) / scale
            canvas.scale(scale.toFloat(), scale.toFloat())
            val r = LocalRect

//            c.SetColor(ZColor.Green())
//            c.FillPath(ZPath(rect = r))
//
            if (drawHandler != null) {
                drawHandler!!(r, c)
            }
            c.PopState()
            super.onDraw(canvas)
        }
    }

    fun ExposeRows() {
        invalidate()
        invalidateViews()
    }

    fun UpdateVisibleRows(animate: Boolean = true) {
        invalidateViews()
    }

    fun updateRow(index:Int) {
        owner!!.UpdateRow(index)
        val v = GetRowViewFromIndex(index)
        if (v != null) {
            v.View().invalidate()
            val vg = v.View() as ViewGroup
            if (vg!!.childCount > 0) {
                vg!!.getChildAt(0).invalidate()
            }
        }
    }

    fun ScrollToMakeRowVisible(row: Int, animated: Boolean = true) {
        if (row < 0 || row>= getCount()) {
            return
        }
        smoothScrollToPosition(row)
    }

    fun IsFocused(rowView:ZCustomView) : Boolean {
        return rowView.isFocused
    }

    fun ReloadData(row: Int? = null, animate: Boolean = false) {
        if (row != null) {
            updateRow(row!!)
            return
        }
        ladapter = listAdapter(zMainActivityContext!!, owner!!, this)
        adapter = ladapter

        // incredible hack below, notifications, requestLayout, nothing else worked!!!:
        var r = Rect
        r += ZPos(0.0, 1.0)
        zLayoutViewAndScale(this, r)
        r += ZPos(0.0, -1.0)
        zLayoutViewAndScale(this, r)

//        requestLayout()
//        ExposeRows()
//        Expose()
//        refreshDrawableState()
    }

    fun MoveRow(fromIndex: Int, toIndex: Int) {
        ZNOTIMPLEMENTED()
    }

    fun GetRowViewFromIndex(index: Int) : ZView? {
        var firstListItemPosition = getFirstVisiblePosition()
        var lastListItemPosition = firstListItemPosition + getChildCount() - 1

        val childIndex = index - firstListItemPosition
        if (index < firstListItemPosition || index > lastListItemPosition ) {
            val l = ladapter!!
            var v = l.getView(index, null, this)
            if (margins.h > 0.0) {
                val vc = v as ViewGroup
                v = vc.getChildAt(0)
            }
            return v as ZView
        }
        val v = getChildAt(childIndex)
        if (!margins.IsNull() || spacing != 0.0) {
            val vg = v as ViewGroup
            if (vg != null) {
                return vg.getChildAt(0) as ZView
            }
        }
        return v as ZView
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
        val oldSelection = selectionIndex
        selectionIndex = ZTableIndex(row = row)
        if (selectable) {
            setItemChecked(row, true)
        }
    }

    fun DeleteChildRow(index: Int, animation: ZTableViewRowAnimation = ZTableViewRowAnimation.fade) {
        removeViewAt(index)
    }

//    fun scrollViewWillBeginDragging(scrollView: UIScrollView) {
//        scrolling = true
//    }
//
//    fun scrollViewDidEndDragging(scrollView: UIScrollView, decelerate: Boolean) {
//        if (!decelerate) {
//            scrolling = false
//        }
//    }
//
//    fun scrollViewDidEndDecelerating(scrollView: UIScrollView) {
//        scrolling = false
//    }

}

//class OuterRow : ZCustomView("list.row.container") {
//    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
//        super.DrawInRect(rect, canvas)
//        getChildAt(0).invalidate()
//    }
//}
//

internal class listAdapter(var context: Context, val owner: ZTableViewDelegate, val table: ZTableView) : BaseAdapter() {
    override fun getCount(): Int {
        val n = owner.TableViewGetRowCount()
        return n
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vi: View? = convertView
//        if (true) { //vi == null) { // HACK FOR NOW
            val index = ZTableIndex(position)
            val p = parent as? ZTableView
            var size = ZSize()
            if (p == null) {
                size.w = ZScreen.Main.size.w
            } else {
                size.w = p.LocalRect.size.w
            }
            if (size.w == 0.0) {
                size.w = 100.0
            }
            size.h = owner.TableViewGetHeightOfItem(index)
            var ov = owner.TableViewSetupCell(cellSize = size - ZSize(table.margins.w * 2.0, 0.0), index = index)
            ov!!.canFocus = true
            var r = ZRect(size = size)
            var outView = ov
            if (!table.margins.IsNull() || table.spacing != 0.0) {
                if (table.margins.h != 0.0 && (position == 0 || position == owner.TableViewGetRowCount() - 1)) {
                    size.h += table.margins.h
                }
                if (position != owner.TableViewGetRowCount() - 1) {
                    size.h += table.spacing * ZScreen.SoftScale
                }
                val container = ZCustomView("list.row.container")
                zLayoutViewAndScale(container, r)
                container.minSize = r.size
                container.addView(ov!!)
                outView = container
            }
            val or = r.Expanded(ZSize(-table.margins.w, 0.0))
            if (position == 0) {
                or.SetMinY(or.Min.y + table.margins.h)
            }
            if (position == owner.TableViewGetRowCount() - 1) {
                or.SetMaxY(or.Max.y - table.margins.h)
            } else {
                or.size.h -= table.spacing * ZScreen.SoftScale
            }
            zLayoutViewAndScale(ov!!, or)
            val cv = ov as? ZContainerView
            if (cv != null) {
                cv.ArrangeChildren()
            }
            ov.minSize = or.size

            if (ZIsTVBox()) {
                ov!!.HandlePressedInPosFunc = { pos ->
                    owner.HandleRowSelected(index)
                }
            }
            return outView!!
//        }
//        return vi
    }
}
