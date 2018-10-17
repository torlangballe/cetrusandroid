
//
//  ZConfirmStack.swift
//
//  Created by Tor Langballe on /26/6/16.
//
package com.github.torlangballe.cetrusandroid

class ZConfirmStack: ZStackView {
    companion object {

        fun PushViewWithTitleBar(view: ZView, title: String) : ZStackView {
            val v1 = ZVStackView(space = 0.0)
            val cv = view as? ZContainerView
            if (cv != null) {
                v1.portraitOnly = cv.portraitOnly
            }
            val titleBar = ZTitleBar(text = title, closeType = ZTitleBar.CloseButtons.cross)
            v1.Add(titleBar, align = ZAlignment.Top or ZAlignment.HorCenter or ZAlignment.HorExpand or ZAlignment.NonProp)
            v1.Add(view.View(), align = ZAlignment.HorCenter or ZAlignment.Bottom or ZAlignment.Expand or ZAlignment.NonProp)
            ZPresentView(v1)
            return v1
        }
    }

    var done: ((result: Boolean) -> Unit)? = null

    constructor(useOk: Boolean = true, strokecolor: ZColor = ZColor.White(), done: ((result: Boolean) -> Unit)? = null) : super(name = "confirm") {
        this.done = done
        margin = ZRect(30.0, 0.0, -30.0, 0.0)
        var ca = ZAlignment.HorCenter
        if (useOk) {
            val set = createShape("check", strokeColor = strokecolor, align = ZAlignment.Right)
            set.accessibilityLabel = ZWords.GetSet()
            ca = ZAlignment.Left
        }
        val cancel = createShape("cross", strokeColor = strokecolor, align = ca)
        cancel.accessibilityLabel = ZWords.GetCancel()
    }

    private fun createShape(name: String, strokeColor: ZColor, align: ZAlignment) : ZShapeView {
        val shape = ZShapeView(type = ZShapeView.ShapeType.circle, minSize = ZSize(64.0, 64.0))
        shape.image = ZImage(named = name + ".png")
        shape.objectName = name
        shape.strokeColor = strokeColor
        shape.strokeWidth = 2.0
        shape.AddTarget(this, forEventType = ZControlEventType.pressed)
        Add(shape, align = align or ZAlignment.VertCenter)
        return shape
    }

    override fun HandlePressed(sender: ZView, pos: ZPos) {
        if (sender.objectName == "check") {
            done?.invoke(true)
        } else if (sender.objectName == "cross") {
            done?.invoke(false)
            if (FindCellWithName("check") == null) {
                ZPopTopView()
            }
        }
    }

    fun WrapForPushWithView(view: ZView) : ZCustomView {
        val v1 = ZVStackView(space = 40.0)
        v1.Add(this, align = ZAlignment.Center or ZAlignment.HorExpand or ZAlignment.NonProp)
        v1.Add(view.View(), align = ZAlignment.Center)
        return v1
    }
}
