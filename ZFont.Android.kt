//
//  ZFont.swift
//
//  Created by Tor Langballe on /21/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Typeface

private fun getNativeStyle(style: ZFont.Style) : Int {
    return when (style) {
        ZFont.Style.bold -> Typeface.BOLD
        ZFont.Style.italic -> Typeface.ITALIC
        ZFont.Style.boldItalic -> Typeface.BOLD_ITALIC
        else -> Typeface.NORMAL
    }
}

private fun makeTypeface(fontName: String, style: ZFont.Style = ZFont.Style.normal) : Typeface {
    val t = Typeface.create(fontName, getNativeStyle(style))
    return t
}

class ZFont {
    enum class Style(val rawValue: String) {
        normal("normal"), bold("bold"), italic("italic"), boldItalic("bold-italic");
        companion object : ZEnumCompanion<String, Style>(Style.values().associateBy(Style::rawValue))
    }

    val lineHeight: Int
        get() {
            return size.toInt()
        }

    var size = 20.0
    var name = ""
    var typeface:Typeface

    companion object {
        fun Nice(size: Double, style: Style = Style.normal): ZFont {
            return ZFont(fontName = "Helvetica", style = style, pointsize = size * ZScreen.SoftScale)
        }

        var appFont = Nice(20.0)
    }

    constructor(fontName: String, pointsize: Double, style: Style = Style.normal) {
        typeface = makeTypeface(fontName, style)
        name = fontName
        size = pointsize
    }

    fun NewWithSize(size: Double): ZFont? =
            ZFont(this.name, size)
}


