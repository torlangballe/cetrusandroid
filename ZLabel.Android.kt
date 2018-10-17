//
//  ZLabel.swift
//
//  Created by Tor Langballe on /2/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View.MeasureSpec.getMode
import android.view.View.MeasureSpec.getSize
import android.widget.TextView

private fun toColor(c:Int) : Color {
    return Color.valueOf(c)
}

open class ZLabel: TextView, ZView {
    override var objectName: String = "ZLabel"
    override var isHighlighted: Boolean = false
    override var Usable: Boolean = true // for now
    var touchInfo: ZTouchInfo = ZTouchInfo()
    var tapTarget: ZCustomView? = null
    var minWidth: Double = 0.0
    var maxWidth: Double = 0.0
    var maxHeight: Double? = null
    var margin = ZRect()
    var font: ZFont = ZFont.Nice(20.0)
    var adjustsFontSizeToFitWidth: Boolean = false // these don't do anything
    var minimumScaleFactor: Double = 0.5           // these don't do anything
    var xAlignment: ZAlignment = ZAlignment.None

    var debugText = ""

    var Color: ZColor
        get() {
            return ZColor(color = toColor(currentTextColor))
        }
        set(c) {
            setTextColor(c.color.toArgb())
        }

    var numberOfLines: Int
        get() {
            return lineCount
        }
        set(n) {
            setLines(n)
        }

    constructor(text: String = "", minWidth: Double = 0.0, maxWidth: Double = 0.0, lines: Int = 1, font: ZFont? = null, align: ZAlignment = ZAlignment.Left, color: ZColor = ZColor.White()) : super(zMainActivityContext!!) {
        this.minWidth = minWidth
        this.maxWidth = maxWidth
        if (font != null) {
            this.font = font
        }
        this.setTypeface(this.font.typeface)
        this.setTextSize(this.font.size.toFloat())
        this.setLines(lines)
        this.text = text
        debugText = text
        this.SetAlignment(align)
        this.Color = color
        this.isClickable = false
    }

    var HandlePressedInPosFunc: ((pos: ZPos) -> Unit)?
        get() {
            return touchInfo.handlePressedInPosFunc
        }
        set(newValue) {
            touchInfo.handlePressedInPosFunc = newValue
            this.isClickable = true
//            isUserInteractionEnabled = true
            isAccessibilityElement = true
//            accessibilityTraits |= UIAccessibilityTraitButton
        }

//    override fun drawText(rect: CGRect) {
//        val insets = UIEdgeInsets.init(top = CGFloat(margin.Min.y), left = CGFloat(margin.Min.x), bottom = CGFloat(-margin.Max.y), right = CGFloat(-margin.Max.x))
//        super.drawText(in = UIEdgeInsetsInsetRect(rect, insets))
//        if (handlePressedInPosFunc != null) {
//            // we hack this in here...
//            isUserInteractionEnabled = true
//        }
//    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (touchInfo.gestureDetector != null && touchInfo.gestureDetector!!.onTouchEvent(event)) {
            return true
        }
        super.onTouchEvent(event)
        if (!isClickable) {
            return false
        }
        return handleTouch(this, event, touchInfo)
    }

    override fun CalculateSize(total: ZSize): ZSize {
        if (text == "" && lineCount == 0) {
            return ZSize(0.0, 0.0)
        }
        var s = total
        if (maxWidth != 0.0 && maxWidth < s.w) {
            s.w = maxWidth
        }
        s.h = 29999.0
        if (maxHeight != null && maxHeight!! > s.h) {
            s.h = maxHeight!!
        }

        var tdraw = ZTextDraw()
        tdraw.rect = ZRect(size = s)
        tdraw.font = font
        tdraw.alignment = xAlignment
        tdraw.text = text.toString()
        var bsize = tdraw.GetBounds().size
        bsize.w += 1

        return bsize
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wmode = getMode(widthMeasureSpec)
        val w = getSize(widthMeasureSpec).toDouble()
        val h = getSize(heightMeasureSpec).toDouble()

        val scale = ZScreen.Scale
//        var total = ZSize(w.toDouble(), h.toDouble())
//        total.Maximize(ZSize(100, 16))
//        total /= scale

        val total = ZSize(800, 100)
        if (w > 0 && w < ZScreen.Main.size.w) {
            total.w = w
        }
        if (h > 0 && h < ZScreen.Main.size.h) {
            total.h = h
        }
        var s = CalculateSize(total)
//        s.h += 8
        s *= scale
        setMeasuredDimension((s.w).toInt(), s.h.toInt())
    }

    /*
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val scale = ZScreen.Scale
        var box = ZSize()
        if (maxWidth != 0.0) {
            box.w = maxWidth * scale
        }

        val metrics = this.paint.getFontMetrics()
        val lineHeight = metrics.ascent + metrics.descent + metrics.leading

        val size = ZSize(width.toDouble(), height.toDouble())

        box.w = maxOf(box.w, (size.w).toDouble())
        if (this.lineCount > 1) {
            box = ZSize((size.w).toDouble(), (font.size).toDouble() * (this.lineCount).toDouble() * 1.1)
        } else {
            box.h = 99999.0
        }
        var gs = ZSize(measuredWidth.toDouble(), measuredHeight.toDouble()) / scale
        if (minWidth != 0.0) {
            gs.w = maxOf(gs.w, minWidth * scale)
        }
        gs.h = maxOf(gs.h, lineHeight * 1.2)
        gs.w -= margin.size.w
        // margin is typically 10, -10, so must subtract
        gs.h -= margin.size.h
        if (maxWidth != 0.0) {
            gs.w = minOf(gs.w, maxWidth * scale)
        }
        if (maxHeight != null) {
            gs.h = minOf(gs.h, maxHeight!! * scale)
        }
        gs *= scale
        setMeasuredDimension((gs.w).toInt(), gs.h.toInt())
    }
*/

    override fun View() : UIView =
            this

    fun SetAlignment(a: ZAlignment) {
        xAlignment = a
        var g = 0
        if (a and ZAlignment.Left) {
            g =  g or Gravity.LEFT
        }
        if (a and ZAlignment.Right) {
            g =  g or Gravity.RIGHT
        }
        if (a and ZAlignment.HorCenter) {
            g =  g or Gravity.CENTER_HORIZONTAL
        }
        if (a and ZAlignment.Top) {
            g =  g or Gravity.TOP
        }
        if (a and ZAlignment.Bottom) {
            g =  g or Gravity.BOTTOM
        }
        if (a and ZAlignment.VertCenter) {
            g =  g or Gravity.CENTER_VERTICAL
        }
        gravity = g
    }

    fun SetText(newText: String, animationDuration: Float = 0f) {
        if (this.text != newText) {
            if (animationDuration != 0f) {
                ZNOTIMPLEMENTED()
            } else {
                this.text = newText
            }
        }
    }

    fun SetLinebreakMode(mode: ZTextWrapType) {
        // TODO: Not done yet
    }

    /*
    override fun touchesBegan(touches: Set<UITouch>, event: UIEvent?) {
        if (isUserInteractionEnabled) {
            if (tapTarget != null) {
                val pos = ZPos(touches.firstOrNull()!!.location(in = this))
                tapTarget?.HandleTouched(this, state = .began, pos = pos, inside = true)
            }
            isHighlighted = true
            Expose()
        }
    }

    override fun touchesEnded(touches: Set<UITouch>, event: UIEvent?) {
        if (isUserInteractionEnabled) {
            if (tapTarget != null) {
                val pos = ZPos(touches.firstOrNull()!!.location(in = this))
                val inside = this.Rect.Contains(pos)
                tapTarget?.HandleTouched(this, state = .ended, pos = pos, inside = inside)
            }
            isHighlighted = false
            this.PerformAfterDelay(0.05) {   ->
                this.Expose()
            }
            val pos = ZPos(touches.firstOrNull()!!.location(in = this))
            if (handlePressedInPosFunc != null) {
                handlePressedInPosFunc!!.invoke(pos)
            } else if (tapTarget != null) {
                tapTarget?.HandlePressed(this, pos = pos)
            }
        }
    }

    override fun touchesCancelled(touches: Set<UITouch>, event: UIEvent?) {
        if (isUserInteractionEnabled) {
            if (tapTarget != null) {
                tapTarget?.HandleTouched(this, state = .canceled, pos = ZPos(), inside = false)
            }
            isHighlighted = false
            Expose()
        }
    }
*/
    fun AddTarget(t: ZCustomView?, forEventType: ZControlEventType) {
        tapTarget = t
        assert(forEventType == ZControlEventType.pressed)
//        this.isUserInteractionEnabled = true
    }
}
