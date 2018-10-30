
//
//  ZSize.swift
//
//  Created by Tor Langballe on /23/9/14.
//  Copyright (c) 2014 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

data class ZSize(
        var w: Double = 0.0,
        var h: Double = 0.0) {

    constructor() : this(0, 0) {}

    constructor(aw: Int, ah: Int) : this(aw.toDouble(), ah.toDouble()) {}

    constructor(aw: Float, ah: Float) : this(aw.toDouble(), ah.toDouble()) {}

    fun GetPos() : ZPos =
            ZPos(w, h)

    fun IsNull() : Boolean =
            w == 0.0 && h == 0.0

    operator fun get(vertical: Boolean) : Double {
        if (vertical) { return h }
        return w
    }
    operator fun set(vertical:Boolean, v:Double) {
        if (vertical) { h = v }
        else { w = v }
    }

    fun Max() : Double =
            maxOf(w, h)

    fun Min() : Double =
            minOf(w, h)

    fun EqualSided() : ZSize {
        val m = maxOf(w, h)
        return ZSize(m, m)
    }

    fun Area() : Double =
            w * h

    operator fun compareTo(s: ZSize) : Int =
            ((Area() - s.Area() * 1000)).toInt()
    // will loose presision so half-ass * 1000 hack

    fun Maximize(a: ZSize) {
        w = maxOf(w, a.w)
        h = maxOf(h, a.h)
    }

    fun Minimize(a: ZSize) {
        w = minOf(w, a.w)
        h = minOf(h, a.h)
    }

    fun Swap() {
        val t = w
        w = h
        h = t
    }

    operator fun unaryMinus() : ZSize =
            ZSize(-w, -h)

    fun equals(a: ZSize) : Boolean =
            w == a.w && h == a.h

    operator fun plus(a: ZSize) : ZSize =
            ZSize(w + a.w, h + a.h)

    operator fun minus(a: ZSize) : ZSize =
            ZSize(w - a.w, h - a.h)

    operator fun times(a: ZSize) : ZSize =
            ZSize(w * a.w, h * a.h)

    operator fun times(a: Double) : ZSize =
            ZSize(w * a, h * a)

    operator fun div(a: ZSize) : ZSize =
            ZSize(w / a.w, h / a.h)

    operator fun div(a: Double) : ZSize =
            ZSize(w / a, h / a)
}

//func operator_plus(_ me:ZSize, _ a:ZSize) -> ZSize    { return ZSize(me.w + a.w, me.h + a.h) }
//func operator_minus(_ me:ZSize, _ a:ZSize) -> ZSize   { return ZSize(me.w - a.w, me.h - a.h) }
//func operator_times(_ me:ZSize, _ a:ZSize) -> ZSize   { return ZSize(me.w * a.w, me.h * a.h) }
//func operator_times(_ me:ZSize, _ a:Double) -> ZSize  { return ZSize(me.w * a, me.h * a)     }
//func operator_div(_ me:ZSize, _ a:ZSize) -> ZSize     { return ZSize(me.w / a.w, me.h / a.h) }
//func operator_div(_ me:ZSize, _ a:Double) -> ZSize    { return ZSize(me.w * a, me.h * a)     }
