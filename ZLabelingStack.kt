
//
//  ZLabelingStack.swift
//
//  Created by Tor Langballe on 15/02/2019.
//
package com.github.torlangballe.cetrusandroid

class ZLabelingStack: ZStackView {
    
    constructor(view: ZView, text: String, color: ZColor = ZColor.White(), font: ZFont? = null) : super(name = "zlabelstack:" + text) {
        space = 4.0
        Add(view.View(), align = ZAlignment.Left or ZAlignment.VertCenter)
        val label = ZLabel(text = text, font = font, color = color)
        Add(label, align = ZAlignment.Left or ZAlignment.VertCenter)
    }
}
