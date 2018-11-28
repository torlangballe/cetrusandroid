
//
//  ZMath.swift
//  Created by Tor Langballe on /23/9/14.
//
package com.github.torlangballe.cetrusandroid

import kotlin.math.*
import java.util.Random

data class ZMath (val _dummy: Int = 0) {
    companion object {
        val PI: Double = 3.141592653589793
        val DegreesToMeters = (111.32 * 1000)
        val MetersToDegrees = 1 / DegreesToMeters

        fun RadToDeg(rad: Double) : Double =
            rad * 180 / PI

        fun DegToRad(deg: Double) : Double =
            deg * PI / 180

        fun AngleDegToPos(deg: Double) : ZPos =
            ZPos(sin(DegToRad(deg)), -cos(DegToRad(deg)))

        fun PosToAngleDeg(pos: ZPos) : Double =
            RadToDeg(ArcTanXYToRad(pos))

        fun GetDistanceFromLongLatInMeters(pos1: ZPos, pos2: ZPos) : Double {
            val R = 6371.0
            // Radius of the earth in km
            val dLat = DegToRad(pos2.y - pos1.y)
            val dLon = DegToRad(pos2.x - pos1.x)
            val a = (Pow(sin(dLat / 2.0), 2.0) + cos(DegToRad(pos1.y))) * cos(DegToRad(pos2.y)) * Pow(sin(dLon / 2.0), 2.0)
            val c = 2.0 * (asin(sqrt(abs(a)))).toDouble()
            return c * R * 1000.0
        }

        fun Fraction(v: Double) : Double =
            v - Floor(v)

        fun Floor(v: Double) : Double =
            floor(v)

        fun Ceil(v: Double) : Double =
            ceil(v)

        fun Log10(d: Double) : Double =
            log10(d)

        fun GetNiceIncsOf(d: Double, incCount: Int) : Double {
            val l = floor(log10(d))
            var n = Pow(10.0, l)
            while (d / n < incCount.toDouble()) {
                n = n / 2.0
            }
            return n
        }

        fun ArcTanXYToRad(pos: ZPos) : Double {
            var a = (atan2(pos.y, pos.x)).toDouble()
            if (a < 0) {
                a += PI * 2
            }
            return a
        }

        fun MixedArrayValueAtIndex(array: MutableList<Double>, index: Double) : Double {
            if (index < 0.0) {
                return array[0]
            }
            if (index >= (array.size).toDouble() - 1) {
                return array.lastOrNull()!!
            }
            val n = index
            val f = (index - n)
            var v = array[n.toInt()] * (1 - f)
            if (n.toInt() < array.size) {
                v += array[(n + 1).toInt()] * f
                return v
            }
            return array.lastOrNull() ?: 0.0
        }

        fun MixedArrayValueAtT(array: MutableList<Double>, t: Double) : Double =
            MixedArrayValueAtIndex(array, index = ((array.size).toDouble() - 1) * t)
    }
}
