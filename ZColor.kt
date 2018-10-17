
//
//  ZColor.swift
//
//  Created by Tor Langballe on /23/9/14.
//  Copyright (c) 2014 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

data class ZHSBA(
        var h: Double = 0.0,
        var s: Double = 0.0,
        var b: Double = 0.0,
        var a: Double = 0.0) {}

data class ZRGBA(
        var r: Double = 0.0,
        var g: Double = 0.0,
        var b: Double = 0.0,
        var a: Double = 0.0) {}

fun ZColor.OpacityChanged(opacity: Double) : ZColor {
    val c = RGBA
    return ZColor(r = c.r, g = c.g, b = c.b, a = opacity)
}

fun ZColor.Mix(withColor: ZColor, amount: Double) : ZColor {
    val wc = withColor.RGBA
    var c = RGBA
    c.r = (1 - amount) * c.r + wc.r * amount
    c.g = (1 - amount) * c.g + wc.g * amount
    c.b = (1 - amount) * c.b + wc.b * amount
    c.a = (1 - amount) * c.a + wc.a * amount
    return ZColor(r = c.r, g = c.g, b = c.b, a = c.a)
}

fun ZColor.MultipliedBrightness(multiply: Double) : ZColor {
    val hsba = this.HSBA
    return ZColor(h = hsba.h, s = hsba.s, b = hsba.b * multiply, a = hsba.a)
}

fun ZColor.AlteredContrast(contrast: Double) : ZColor {
    val multi = ZMath.Pow(((1.0 + contrast).toDouble()) / 1.0, 2.0)
    var c = this.RGBA
    c.r = (c.r - 0.5) * multi + 0.5
    c.g = (c.g - 0.5) * multi + 0.5
    c.b = (c.b - 0.5) * multi + 0.5
    return ZColor(r = c.r, g = c.g, b = c.b, a = c.a)
}

fun ZColor.GetContrastingGray() : ZColor {
    val g = GrayScale
    if (g < 0.5) {
        return ZColor.White()
    }
    return ZColor.Black()
}
