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

    private var ladapter: listAdapter? = null

    override var isHighlighted:Boolean = false
    override var Usable = true
    override fun View() : UIView = this

    var selectionIndex: ZTableIndex = ZTableIndex()
    var owner: ZTableViewDelegate? = null
    var selectable = true
    var deleteHandler: (() -> Unit)? = null
    var selectedColor = ZColor()

    constructor(name: String = "customview") : super(zMainActivityContext!!) {
        selectionIndex = ZTableIndex(-1)
        isFocusableInTouchMode = true

        layoutMode = ViewGroup.LayoutParams.MATCH_PARENT // was to make getView() work, might not be needed

        Rect = ZRect(0.0, 0.0, 100.0, 300.0)

        this.setOnItemClickListener { parent, view, position, id ->
            val index = ZTableIndex(position)
            owner!!.HandleRowSelected(index)
            selectionIndex = index
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        margins = ZSize(0, 0) // hack to remove margins on android
        if (first && owner != null) {
            ladapter = listAdapter(zMainActivityContext!!, owner!!, this)
            adapter = ladapter
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        if (first) {
            //    allowsSelection = true // selectable
            if (selectionIndex.row != -1) {
                Select(selectionIndex.row)
            }
//                contentInset = UIEdgeInsetsMake(CGFloat(margins.h), 0, CGFloat(margins.h), 0)
            if (selectable) {
                choiceMode = ListView.CHOICE_MODE_SINGLE
            }
            first = false
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        super.onLayout(p0, p1, p2, p3, p4)
//        val sin = ZSize((p3 - p1).toDouble(), (p4 - p2).toDouble())
//        val s = CalculateSize(sin)
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

    fun ScrollToMakeRowVisible(row: Int, animated: Boolean = true) {
        if (row < 0 || row>= getCount()) {
            return
        }

        val first = getFirstVisiblePosition()
        val last = getLastVisiblePosition()

        if (row < first) {
            setSelection(row)
            return
        }

        if (row >= last) {
            setSelection(1 + row - (last - first))
            return
        }
    }

    fun ReloadData(row: Int? = null, animate: Boolean = false) {
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

    /*
    private fun getZViewChild(v: UIView) : ZView? {
        for (c in v.subviews) {
            val z = c as? ZView
            if (z != null) {
                return z
            }
        }
        for (c in v.subviews) {
            val z = getZViewChild(c)
            if (z != null) {
                return z
            }
        }
        return null
    }
    */


    fun GetRowViewFromIndex(index: Int) : ZView? {
        val firstListItemPosition = getFirstVisiblePosition()
        val lastListItemPosition = firstListItemPosition + getChildCount() - 1

        if (index< firstListItemPosition || index > lastListItemPosition ) {
            val l = ladapter!!
            val v = l.getView(index, null, this)
            return v as ZView
        }
        val childIndex = index - firstListItemPosition
        val v = getChildAt(childIndex)
        return v as ZView
    }

    fun GetIndexFromRowView(view: ZView) : Int? {
        ZNOTIMPLEMENTED()
        return null
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
        System.out.println("------ get view")
        val vi: View? = convertView
        if (vi == null) {
            val index = ZTableIndex(position)
            val p = parent as? ZTableView
            var size = ZSize()
            if (p == null) {
                size.w = ZScreen.Main.size.w
            } else {
                size.w = p.LocalRect.size.w
            }
            size.h = owner.TableViewGetHeightOfItem(index)
            val r = ZRect(size = size).Expanded(ZSize(-table.margins.w, 0.0))
            val ov = owner.TableViewSetupCell(cellSize = r.size, index = index)
            zLayoutViewAndScale(ov!!, r)

            val cv = ov as? ZContainerView
            if (cv != null) {
                cv.ArrangeChildren()
            }
//            ov!!.left = r.Min.x.toInt()
//            ov!!.top = r.Min.y.toInt()
//            ov!!.right = r.Max.x.toInt()
//            ov!!.bottom= r.Max.y.toInt()
            ov!!.minSize = r.size

            return ov
        }
        return vi
    }
}
