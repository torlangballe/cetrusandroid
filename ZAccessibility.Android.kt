//
//  ZAccessibility.Android.kt
//
//  Created by Tor Langballe on /20/08/18.
//

package com.github.torlangballe.cetrusandroid

class ZAccessibilty {
    companion object {
        val IsOn: Boolean
            get() {
                return false
            }

        fun ConvertRect(rect: ZRect, view: ZView) : ZRect = rect

        fun SayNotification(message: String) {
        }

        fun SayScrollNotification(message: String) {
        }

        fun SendScreenUpdateNotification(message: String = "") {
        }
    }
}

