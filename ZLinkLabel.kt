
//
//  ZLinkLabel.swift
//
//  Created by Tor Langballe on /22/12/17.
//
package com.github.torlangballe.cetrusandroid

class ZLinkLabel: ZLabel {
    constructor(text: String = "", minWidth: Double = 0.0, maxWidth: Double = 0.0, lines: Int = 1, font: ZFont? = null, align: ZAlignment = ZAlignment.Left, color: ZColor = ZColor.White()) : super(text, minWidth, maxWidth, lines, font, align, color) {
    }

    fun SetUrl(url: String) {
        this.HandlePressedInPosFunc = { pos: ZPos ->
            mainZApp?.HandleOpenUrl(ZUrl(string = url))
        }
    }
}
