
//
//  ZButton.swift
//
//  Created by Tor Langballe on /14/12/17.
//
package com.github.torlangballe.cetrusandroid

class ZButton: ZShapeView {
    var insets = ZSize()

    constructor(title: String, colorName: String, w: Double, insets: ZSize = ZSize(6.0, 13.0), titleColor: ZColor = ZColor.White()) : super(type = ZShapeView.ShapeType.none, minSize = ZSize(w, 44.0) * ZScreen.SoftScale) {
        this.insets = insets
        canFocus = true
        SetColorName(colorName)
        text.text = title
        text.font = ZFont.Nice(22.0, style = ZFont.Style.bold)
        text.color = titleColor
        fillBox = true
        imageMargin = ZSize(5.0, 5.0) * ZScreen.SoftScale
    }

    fun SetColorName(col: String) {
        var cimage = ZImage.Named(col + "Button.png")
        cimage = cimage!!.Make9PatchImage(capInsets = ZRect(insets.w, insets.h, insets.w, insets.h))
        SetImage(cimage, "")
    }
}
