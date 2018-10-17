//

//  ZPos.swift
//
//  Created by Tor Langballe on /23/9/14.
//  Copyright (c) 2014 Capsule.fm. All rights reserved.
package com.github.torlangballe.cetrusandroid

// https://github.com/seivan/VectorArithmetic/blob/master/VectorArithmetic/VectorArithmetic.swift
// http://practicalswift.com/2014/06/14/the-swift-standard-library-list-of-built-in-functions/
import kotlin.math.*

data class ZPos(
        var x: Double = 0.0,
        var y: Double = 0.0) {
    operator fun get(vertical: Boolean) : Double {
        if (vertical) { return y }
        return x
    }
    operator fun set(vertical:Boolean, v:Double) {
        if (vertical) { y = v }
        else { x = v }
    }

    val Size: ZSize
        get() = ZSize(x, y)

    fun Set(ax: Float, ay: Float) {
        x = ax.toDouble()
        y = ay.toDouble()
    }

    fun Set(ax: Double, ay: Double) {
        x = ax
        y = ay
    }

    constructor() : this(0.0, 0.0) {}

    constructor(ax: Float, ay: Float) : this(ax.toDouble(), ay.toDouble()) {}

    constructor(ax: Int, ay: Int) : this(ax.toDouble(), ay.toDouble()) {}

    constructor(fp: ZFPos) : this((fp.x).toDouble(), (fp.y).toDouble()) {}

    fun Swap() {
        val t = x
        x = y
        y = t
    }

    fun GetRot90CW() : ZPos =
            ZPos(y, -x)

    fun Dot(a: ZPos) : Double =
            x * a.x + y * a.y

    fun Length() : Double =
            sqrt(x * x + y * y)

    fun IsNull() : Boolean =
            x == 0.0 && y == 0.0

    fun GetNormalized() : ZPos =
            this / Length()

    fun Sign() : ZPos =
            ZPos(sign(x), sign(y))

    fun Abs() : ZPos =
            ZPos(if (x < 0) -x else x, if (y < 0) -y else y)

    fun IsSameDirection(p: ZPos) : Boolean {
        if (this == p) {
            return true
        }
        if (sign((p.x).toDouble()) != sign(x.toDouble()) || sign((p.y).toDouble()) != sign(y.toDouble())) {
            return false
        }
        if (p.y == 0.0) {
            return y == 0.0
        }
        if (y == 0.0) {
            return p.y == 0.0
        }
        if (x / y == p.x / p.y) {
            return true
        }
        return false
    }

    fun RotatedCCW(angle: Double) : ZPos {
        val s = sin(angle)
        val c = cos(angle)
        return ZPos(x * c - y * s, x * s + y * c)
    }

    operator fun plus(a: Double) : ZPos =
            ZPos(x + a, y + a)

    operator fun minus(a: Double) : ZPos =
            ZPos(x - a, y - a)

    operator fun times(a: Double) : ZPos =
            ZPos(x * a, y * a)

    operator fun div(a: Double) : ZPos =
            ZPos(x / a, y / a)

    operator fun plus(a: ZPos) : ZPos =
            ZPos(x + a.x, y + a.y)

    operator fun minus(a: ZPos) : ZPos =
            ZPos(x - a.x, y - a.y)

    operator fun times(a: ZPos) : ZPos =
            ZPos(x * a.x, y * a.y)

    operator fun div(a: ZPos) : ZPos =
            ZPos(x / a.x, y / a.y)

    operator fun unaryMinus() : ZPos =
            ZPos(-x, -y)

    operator fun plus(s: ZSize) : ZPos =
            ZPos(x + s.w, y + s.h)

    fun equals(a: ZPos) : Boolean =
            x == a.x && y == a.y
}

data class ZFPos(
        var x: Float = 0.0.toFloat(),
        var y: Float = 0.0.toFloat()) {
    val DPos: ZPos
        get() {
            return ZPos(fp = this)
        }

    constructor(p: ZPos) : this((p.x).toFloat(), (p.y).toFloat()) {}
}

fun ZForVectors(positions: List<ZPos>, close: Boolean = false, handle: (s: ZPos, v: ZPos) -> Boolean) {
    var i = 0
    while (i < positions.size) {
        val s = positions[i]
        var e = ZPos()
        if (i == positions.size - 1) {
            if (close) {
                e = positions[0] - s
            } else {
                break
            }
        } else {
            e = positions[i + 1]
        }
        if (!handle(s, e - s)) {
            break
        }
        i += 1
    }
}

fun ZGetTPositionInPosPath(path: List<ZPos>, t: Double, close: Boolean = false) : ZPos {
    var len = 0.0
    var resultPos = ZPos()
    if (t <= 0) {
        return path[0]
    }
    ZForVectors(positions = path, close = close) { s, v ->
        len += v.Length()
        true
    }
    if (t >= 1) {
        return if (close) path[0] else path.lastOrNull()!!
    }
    val tlen = t * len
    len = 0.0
    ZForVectors(positions = path, close = close) { s, v ->
        val vlen = v.Length()
        val l = len + vlen
        if (l >= tlen) {
            val ldiff = tlen - len
            val f = ldiff / vlen
            resultPos = s + v * f
            return@ZForVectors false
        }
        len = l
        true
    }
    return resultPos
}
