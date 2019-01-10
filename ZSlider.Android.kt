
//
//  ZSlider.swift
//
//  Created by Tor Langballe on /24/10/15.
//
package com.github.torlangballe.cetrusandroid

import android.graphics.PorterDuff
import android.widget.SeekBar


class ZSlider: SeekBar, ZView, ZControl {
    override var objectName: String = "ZSlider"
    override var isHighlighted: Boolean = false
    override var Usable: Boolean = true
    override var High: Boolean = false
    var vertical: Boolean = false
    var minLength: Int = 140
    var ValueString: ((v:Float)->String) = { v -> "$v" }

    override fun View() : ZNativeView = this

    var value:Float
        get() {
            return progress.toFloat() / 1000f
        }
        set(v) {
            progress = (v * 1000f).toInt()
        }

    var handleValueChanged: (() -> Unit)? = null

    constructor(vertical: Boolean = false, minLength: Int = 100, dark:Boolean = false) : super(zMainActivityContext!!) {
        this.vertical = vertical
        this.minLength = minLength
        min = 0
        max = 1000

        if (!dark) {
            getProgressDrawable().setColorFilter(ZColor.White().color.toArgb(), PorterDuff.Mode.SRC_IN);
//            getThumb().setColorFilter(Utils.getAccentColor(this), PorterDuff.Mode.SRC_IN);
        }
        this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) { // we don't do this if we set it ourself, as we can get into a loop then
                    value = progress.toFloat() / 1000f
                    handleValueChanged?.invoke()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) { }
            override fun onStopTrackingTouch(seekBar: SeekBar) { }
        })
    }

//    @objc fun valueChanged(sender: UISlider) {
//        setNeedsDisplay()
//        handleValueChanged?.invoke()
//    }

    override fun AddTarget(target: Any?, forEventType: ZControlEventType) {
    }

    fun SetValue(value: Float, animationDuration: Double = 0.0) {
        if (animationDuration != 0.0) {
            setProgress((value * 100f).toInt(), true)
        } else {
            this.value = value
        }
    }
/*
    override fun endTracking(touch: UITouch?, event: UIEvent?) {
        super.endTracking(touch, with = event)
        for ((_, v) in ticks) {
            val delta = (maximumValue - minimumValue) / 10
            if (abs(v - value) < delta) {
                setValue(v, animated = true)
                break
            }
        }
    }
    override var accessibilityValue: String?
        get() {
            return ValueString(value)
        }
        set(newValue) {}
*/
    fun PopInView(parent: ZContainerView, target: ZView, popInRectMarg: ZRect, done: (result: Float) -> Unit) {
        var w = ZStackView(name = "sliderpop")
        w.vertical = vertical
        w.space = 4.0
        w.margin = ZRect(16.0, 16.0, -16.0, -16.0)
        w.SetBackgroundColor(ZColor(white = 0.3))
        w.SetCornerRadius(16.0)
        w.Add(this, align = ZAlignment.Top or ZAlignment.HorCenter or ZAlignment.VertExpand or ZAlignment.NonProp)
        w.Show(false)
        w.SetAlpha(0.0)
        val close = ZImageView(namedImage = "cross.small.png")
        close.HandlePressedInPosFunc = { pos  ->
            parent.RemoveChild(w)
            done(this.value)
        }
        w.Add(close, align = ZAlignment.Bottom or ZAlignment.HorCenter)
        parent.Add(w, align = ZAlignment.None)
        val s = w.CalculateSize(ZSize(320, 320))
        var r = ZRect(size = s).Centered(parent.GetViewsRectInMyCoordinates(target).Center)
        r.MoveInto(parent.Rect)
        r += popInRectMarg
        w.Rect = r
        w.ArrangeChildren()
        w.Show(true)
        val oldHandle = parent.touchInfo.handlePressedInPosFunc
//        AddTarget(parent, forEventType = ZControlEventType.pressed)
        parent.touchInfo.handlePressedInPosFunc = { pos  ->
            done(this.value)
            parent.RemoveChild(w)
            parent.touchInfo.handlePressedInPosFunc = oldHandle
        }
    ZAnimation.Do(duration = 0.5, animations = { w.SetAlpha(1.0) })
    }

    override fun CalculateSize(total: ZSize): ZSize {
        var s = ZSize(43, minLength)
        if (!vertical) {
            var h = 43
            s = ZSize(minLength, h)
        }
        return s
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var s = ZSize(43, minLength)
        if (!vertical) {
            var h = 43
            s = ZSize(minLength, h)
        }
        s *= ZScreen.Scale
        setMeasuredDimension((s.w).toInt(), s.h.toInt())
    }
}

