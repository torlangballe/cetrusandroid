
//
//  ZButton.swift
//
//  Created by Tor Langballe on /14/12/17.
//
package com.github.torlangballe.cetrusandroid

class ZButton: ZShapeView {
    var insets = ZSize()

    constructor(title: String, colorName: String, w: Double, insets: ZSize = ZSize(6.0, 13.0), titleColor: ZColor = ZColor.White()) : super(type = ShapeType.none, minSize = ZSize(w, 44.0)) {
        this.insets = insets
        SetColorName(colorName)
        text.text = title
        text.font = ZFont.Nice(22.0, style = ZFont.Style.bold)
        text.color = titleColor
        fillBox = true
        imageMargin = ZSize(0.0, 5.0)
    }

    fun SetColorName(col: String) {
        val cimage = ZImage(named = col + "Button.png")
        val nimage = cimage!!.Make9PatchImage(capInsets = ZRect(insets.w, insets.h, insets.w, insets.h))
        SetImage(nimage, "")
    }
}
