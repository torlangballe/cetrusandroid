//
//  ZControl.swift
//
//  Created by Tor Langballe on /24/10/15.
//

package com.github.torlangballe.cetrusandroid

//typealias ZControlEvents = UIControlEvents
enum class ZControlEventType { valueChanged } // pressed,

interface ZControl {
    var High: Boolean
//    fun Control() : UIControl
    fun AddTarget(target: Any?, forEventType: ZControlEventType)
}
var ZControl.High: Boolean
    get() {
        return true
//        return Control().isHighlighted
    }
    set(newValue) {
//        Control().isHighlighted = newValue
    }

fun ZControl.AddTarget(target: Any?, forEventType: ZControlEventType) {
//    when (forEventType) {
//            Z.pressed -> Control().addTarget(target, action = #selector(ZCustomView.handlePressed(_:)), for = UIControlEvents.touchUpInside)
//        ZControlEventType.valueChanged -> Control().addTarget(target, action = #selector(ZCustomView.handleValueChanged(_:)), for = UIControlEvents.valueChanged)
//    }
}
