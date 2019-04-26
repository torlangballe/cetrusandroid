package com.github.torlangballe.cetrusandroid

import java.util.*
import kotlin.math.pow

fun ZMath.Companion.Pow(a:Double, power:Double) : Double {
    return a.pow(power)
}

fun ZMath.Companion.Random1() : Double {
    val random = Random()
    return random.nextDouble()
}
fun ZMath.Companion.RandomN(n:Int = Int.MAX_VALUE) : Int {
    val random = Random()
    return random.nextInt(n)
}

fun ZMath.Companion.NanCheck(d:Double, set:Double = -1.0) : Double {
     if (d.isNaN()) {
        return set
     }
    return d
}

