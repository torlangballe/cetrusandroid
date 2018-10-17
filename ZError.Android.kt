//
//  ZErrorAndroid.swift
//
//  Created by Tor Langballe on /9/8/2018
//

// https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-error/index.html

package com.github.torlangballe.cetrusandroid

typealias ZError = Error

fun ZError.GetMessage() : String {
    return message ?: ""
}

fun ZError.Description() : String {
    return "$this"
}

fun ZNewError(message:String, code:Int = 0, domain:String = "") : ZError {
    return ZError(message)
}

var ZGeneralError = ZNewError(message = "Zed")
val ZUrlErrorDomain = "ZUrlErrorDomain"
val ZCapsuleErrorDomain = "fm.capsule.error"

