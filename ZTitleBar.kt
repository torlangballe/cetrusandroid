
//
//  ZTitleBar.swift
//
//  Created by Tor Langballe on /16/11/15.
//
package com.github.torlangballe.cetrusandroid

class ZTitleBar: ZStackView {
    companion object {
        var Color = ZColor(r = 0.2, g = 0.3, b = 1.0)
    }

    enum class CloseButtons(val rawValue: String) {
        left("arrow.left"), down("arrow.down"), cross("cross"), none("");
        companion object : ZEnumCompanion<String, CloseButtons>(CloseButtons.values().associateBy(CloseButtons::rawValue))
    }

    var closeButton: ZImageView
    val notchInc = 16
    val title: ZLabel
    var sizeCalculated = false
    var closeHandler: ZViewHandler? = null

    constructor(text: String = "", closeType: CloseButtons = CloseButtons.cross, closeAlignX: ZAlignment = ZAlignment.Left) : super(name = "titlebar") {
        closeButton = ZImageView(namedImage = closeType.rawValue + ".png")
        title = ZLabel(text = text, maxWidth = ZScreen.Main.size.w, font = ZFont.Nice(25.0), align = ZAlignment.Left)
        title.Color = ZColor.White()
        title.adjustsFontSizeToFitWidth = true
        title.minimumScaleFactor = 0.5

        space = 0.0
        margin = ZRect(0.0, 0.0, 0.0, -4.0)
        accessibilityLabel = text
        minSize = ZSize(100, 60)
        //        if ZScreen.HasNotch() {
        //            minSize.h += 88
        //        }
        closeButton.AddTarget(this, forEventType = ZControlEventType.pressed)
        closeButton.accessibilityLabel = ZWords.GetClose()
        AddTarget(this, forEventType = ZControlEventType.pressed)
        Add(closeButton, align = closeAlignX or ZAlignment.Bottom)
        Add(title, align = ZAlignment.HorCenter or ZAlignment.Bottom, marg = ZSize(0, 5))
        SetBackgroundColor(Color)
    }

    override fun HandlePressed(sender: ZView, pos: ZPos) {
        when (sender.View()) {
            closeButton -> {
                if (closeHandler != null) {
                    closeHandler!!.HandleClose(sender = this)
                } else {
                    ZPopTopView()
                    //overrideDuration:0) //, overrideTransition:.fade)
                }
            }
            else -> ZTextDismissKeyboard()
        }
    }

    override fun HandleBeforeLayout() {
        if (!sizeCalculated) {
            sizeCalculated = true
            RangeChildren() { view  ->
                if (view.View() != title) {
                    this.title.maxWidth -= (view.Rect.size.w + space)
                }
                true
            }
        }
        minSize.h = 40.0 //+ ZScreen.StatusBarHeight
    }

    fun ShowActivity(show: Boolean = true) {
        if (show) {
            if (FindCellWithName("activity") == null) {
                val activity = ZActivityIndicator(big = false)
                Add(activity, align = ZAlignment.VertCenter or ZAlignment.Right)
                ArrangeChildren()
                activity.Start()
            }
        } else {
            RemoveNamedChild("activity")
            ArrangeChildren()
        }
    }
//
//    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
//        canvas.SetColor(ZColor.Green())
//        val r = rect
//        r.size.h = 3.0
//        val path = ZPath(rect = r)
//        canvas.FillPath(path)
//    }
}
