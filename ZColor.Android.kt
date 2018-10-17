//
//  ZColorAndroid.swift
//
//  Created by Tor Langballe on /13/7/18.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Color

class ZColor (
    var color: Color = Color(),
    var undefined: Boolean = true) {
    var tileImage: ZImage? = null

    fun equals(lhs: ZColor, rhs: ZColor) : Boolean {
        return lhs.color == rhs.color
    }

    constructor(color: Color) : this(color, undefined = false)

    constructor(colorInt: Int) : this(Color.valueOf(colorInt), undefined = false)

    constructor(white:Double, a:Double = 1.0) :
            this(Color.valueOf(white.toFloat(), white.toFloat(), white.toFloat(), a.toFloat()))

    constructor(r:Double, g:Double, b:Double, a:Double = 1.0) :
            this(Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat()))

    constructor(h: Double, s: Double, b: Double, a: Double = 1.0, dummy:Boolean = false) :
            this(hsbaToColor(h, s, b, a))

    constructor(tile: ZImage) : this(Color(), undefined = false) {
        this.tileImage = tile
    }

    val HSBA: ZHSBA
        get() {
            var hsv = FloatArray(4)
            val r = (color.red() * 255).toInt()
            val g = (color.green() * 255).toInt()
            val b = (color.blue() * 255).toInt()
            Color.RGBToHSV(r, g, b, hsv)
            var c = ZHSBA()
            c.h = hsv[0].toDouble()
            c.s = hsv[1].toDouble()
            c.b = hsv[2].toDouble()
            c.a = color.alpha() / 255.0
            return c
        }
/*
    init(pattern:ZImage) {
        undefined = false
        color = UIColor(patternImage:pattern)
    }
*/
    val RGBA: ZRGBA
        get() {
            val c = ZRGBA()
            c.r = minOf(color.red().toDouble(), 1.0)
            c.g = minOf(color.green().toDouble(), 1.0)
            c.b = minOf(color.blue().toDouble(), 1.0)
            c.a = minOf(color.alpha().toDouble(), 1.0)
            return c
        }

    val GrayScaleAndAlpha: Pair<Double, Double>
        get () {
            val c = HSBA
            return Pair(c.b, c.a)
        }

    val GrayScale: Double = GrayScaleAndAlpha.first

    var Opacity: Double = RGBA.a

    var rawColor: Color = color

    companion object {
        fun White(): ZColor = ZColor(Color.valueOf(Color.WHITE))
        fun Black(): ZColor = ZColor(Color.valueOf(Color.BLACK))
        fun Gray(): ZColor = ZColor(Color.valueOf(Color.GRAY))
        fun Clear(): ZColor = ZColor(Color.valueOf(Color.TRANSPARENT))
        fun Blue(): ZColor = ZColor(Color.valueOf(Color.BLUE))
        fun Red(): ZColor = ZColor(Color.valueOf(Color.RED))
        fun Yellow(): ZColor = ZColor(Color.valueOf(Color.YELLOW))
        fun Green(): ZColor = ZColor(Color.valueOf(Color.GREEN))
        fun Orange(): ZColor = ZColor(1.0, 0.5, 0.0)
    }
}

fun hsbaToColor(h: Double, s: Double, b: Double, a: Double) : Color {
    val c = Color.HSVToColor((a * 255).toInt(), floatArrayOf(h.toFloat(), s.toFloat(), b.toFloat()))
    return Color.valueOf(c)
}

