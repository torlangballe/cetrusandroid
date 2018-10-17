package com.github.torlangballe.cetrusandroid

class ZTextView : ZTextField {
    constructor(text: String = "", minWidth: Double = 0.0, maxWidth: Double = 0.0, font: ZFont? = null, alignment: ZAlignment = ZAlignment.Left, lines:Int = 0, margin: ZSize = ZSize(0.0, 0.0), clearColor: ZColor? = null) :
    super(text, minWidth, maxWidth, font, alignment, margin) {
        this.setSingleLine(false)
    }

    fun SetMargins(margins: ZRect) {
        margin = -margins.size
//        self.contentInset = UIEdgeInsetsMake(CGFloat(margins.Min.y), CGFloat(margins.Min.x), -CGFloat(margins.Max.y), -CGFloat(margins.Max.x))
    }
}

