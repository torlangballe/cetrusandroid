
//
//  ZAlignment.swift
//  Cetrus
//
//  Created by Tor Langballe on /23/9/14.
//
package com.github.torlangballe.cetrusandroid

data class ZAlignment(var rawValue: Int) {
    companion object {
        val None = ZAlignment(0)
        val Left = ZAlignment(1)
        val HorCenter = ZAlignment(2)
        val Right = ZAlignment(4)
        val Top = ZAlignment(8)
        val VertCenter = ZAlignment(16)
        val Bottom = ZAlignment(32)
        val HorExpand = ZAlignment(64)
        val VertExpand = ZAlignment(128)
        val HorShrink = ZAlignment(256)
        val VertShrink = ZAlignment(512)
        val HorOut = ZAlignment(1024)
        val VertOut = ZAlignment(2048)
        val NonProp = ZAlignment(4096)
        val HorJustify = ZAlignment(8192)
        val MarginIsOffset = ZAlignment(16384)
        val ScaleToFitProportionally = ZAlignment(32768)
        val Center = HorCenter or VertCenter
        val Expand = HorExpand or VertExpand
        val Shrink = HorShrink or VertShrink
        val HorScale = HorExpand or HorShrink
        val VertScale = VertExpand or VertShrink
        val Scale = HorScale or VertScale
        val Out = HorOut or VertOut
        val Vertical = Top or VertCenter or Bottom or VertExpand or VertShrink or VertOut
        val Horizontal = Left or HorCenter or Right or HorExpand or HorShrink or HorOut
    }

    constructor(str: String) : this(rawValue = stringToRaw(str)) {
    }

    constructor(fromVector: ZPos) : this(rawValue = rawFromVector(fromVector)) {
    }

    fun FlippedVertical() : ZAlignment {
        var r = this
        r.AndWith(Horizontal)
        if (this and Top) {
            r.UnionWith(Bottom)
        }
        if (this and Bottom) {
            r.UnionWith(Top)
        }
        return r
    }

    fun FlippedHorizontal() : ZAlignment {
        var r = this
        r.AndWith(Vertical)
        if (this and Left) {
            r.UnionWith(Right)
        }
        if (this and Right) {
            r.UnionWith(Left)
        }
        return r
    }

    fun Subtracted(sub: ZAlignment) : ZAlignment =
            ZAlignment(this.rawValue and ZBitwiseInvert(sub.rawValue))
    operator fun get (vertical: Boolean) : ZAlignment {
        if (vertical) {
            return this.Subtracted(Horizontal or HorExpand or HorShrink or HorOut)
        }
        return this.Subtracted(Vertical or VertExpand or VertShrink or VertOut)
    }
    val description: String
        get() {
            return StringStorage
        }
    val StringStorage: String
        get() {
            var parts = mutableListOf<String>()
            if (this and Left) {
                parts.append("left")
            }
            if (this and HorCenter) {
                parts.append("horcenter")
            }
            if (this and Right) {
                parts.append("right")
            }
            if (this and Top) {
                parts.append("top")
            }
            if (this and VertCenter) {
                parts.append("vertcenter")
            }
            if (this and Bottom) {
                parts.append("bottom")
            }
            if (this and HorExpand) {
                parts.append("horexpand")
            }
            if (this and VertExpand) {
                parts.append("vertexpand")
            }
            if (this and HorShrink) {
                parts.append("horshrink")
            }
            if (this and VertShrink) {
                parts.append("vertshrink")
            }
            if (this and HorOut) {
                parts.append("horout")
            }
            if (this and VertOut) {
                parts.append("vertout")
            }
            if (this and NonProp) {
                parts.append("nonprop")
            }
            if (this and HorJustify) {
                parts.append("horjustify")
            }
            return ZStr.Join(parts, sep = " ")
        }
    val debugDescription: String
        get() = //
            StringStorage

    fun UnionWith(a: ZAlignment) {
        rawValue = rawValue or a.rawValue
    }

    fun AndWith(a: ZAlignment) {
        rawValue = rawValue and a.rawValue
    }
}

infix fun ZAlignment.or(a: ZAlignment) : ZAlignment =
        ZAlignment(rawValue = rawValue or a.rawValue)
infix fun ZAlignment.and(a: ZAlignment) : Boolean =
        ((this.rawValue and a.rawValue) != 0)

private fun stringToRaw(str: String) : Int {
    var a = 0
    for (s in ZStr.Split(str, sep = " ")) {
        when (s) {
            "left" -> a = a or ZAlignment.Left.rawValue
            "horcenter" -> a = a or ZAlignment.HorCenter.rawValue
            "right" -> a = a or ZAlignment.Right.rawValue
            "top" -> a = a or ZAlignment.Top.rawValue
            "vertcenter" -> a = a or ZAlignment.VertCenter.rawValue
            "bottom" -> a = a or ZAlignment.Bottom.rawValue
            "horexpand" -> a = a or ZAlignment.HorExpand.rawValue
            "vertexpand" -> a = a or ZAlignment.VertExpand.rawValue
            "horshrink" -> a = a or ZAlignment.HorShrink.rawValue
            "vertshrink" -> a = a or ZAlignment.VertShrink.rawValue
            "horout" -> a = a or ZAlignment.HorOut.rawValue
            "vertout" -> a = a or ZAlignment.VertOut.rawValue
            "nonprop" -> a = a or ZAlignment.NonProp.rawValue
            "horjustify" -> a = a or ZAlignment.HorJustify.rawValue
        }
    }
    return a
}

private fun rawFromVector(vector: ZPos) : Int {
    var raw = 0
    var angle = ZMath.PosToAngleDeg(vector)
    if (angle < 0) {
        angle += 360
    }
    if (angle < 45 * 0.5) {
        raw = ZAlignment.Right.rawValue
    } else if (angle < 45 * 1.5) {
        raw = ZAlignment.Right.rawValue or ZAlignment.Top.rawValue
    } else if (angle < 45 * 2.5) {
        raw = ZAlignment.Top.rawValue
    } else if (angle < 45 * 3.5) {
        raw = ZAlignment.Top.rawValue or ZAlignment.Left.rawValue
    } else if (angle < 45 * 4.5) {
        raw = ZAlignment.Left.rawValue
    } else if (angle < 45 * 5.5) {
        raw = ZAlignment.Left.rawValue or ZAlignment.Bottom.rawValue
    } else if (angle < 45 * 6.5) {
        raw = ZAlignment.Bottom.rawValue
    } else if (angle < 45 * 7.5) {
        raw = ZAlignment.Bottom.rawValue or ZAlignment.Right.rawValue
    } else {
        raw = ZAlignment.Right.rawValue
    }
    return raw
}
