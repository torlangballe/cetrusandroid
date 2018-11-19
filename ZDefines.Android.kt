
//
//  ZDefines.Android.kt
//
//  Created by Tor Langballe on /13/07/18.
//

package com.github.torlangballe.cetrusandroid

import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import android.content.res.Configuration
import java.lang.Exception
import java.util.Comparator

typealias AnyHashable = String

typealias ZAnyObject = Any

fun ZIsIOS() : Boolean {
    return false
}

fun ZIsTVBox() : Boolean {
    val uiModeManager = zMainActivity!!.getSystemService(UI_MODE_SERVICE) as? UiModeManager
    if (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
        return true
    }
    return false
}


fun <T>MutableList<T>.append(a:T) {
    add(a)
}

fun <T>List<T>.indexWhere(w:(T) -> Boolean) : Int? {
    val i = this.indexOfFirst(w)
    if (i == -1) {
        return null
    }
    return i
}

fun ZBitwiseInvert(v:Int) : Int {
    return v.inv()
}

fun String.lowercased() : String {
    return toLowerCase()
}

fun String.uppercased() : String {
    return toUpperCase()
}

fun String.removedLast() : String {
    return removeRange(length - 1, length)
}
// special helper companion class that is inserted into enums with values, for fromRaw conversion:
open class ZEnumCompanion<T, V>(private val valueMap: Map<T, V>) {
    fun rawValue(type: T) = valueMap[type]
}

fun <T>List<T>.writable() : MutableList<T> {
    return toMutableList()
}

fun <T>List<T>.head(count:Int) : MutableList<T> {
    return take(count).writable()
}

fun <T>MutableList<T>.removeFirst() : T {
    return removeAt(0)
}

fun <T>MutableList<T>.popFirst() : T? {
    if (count() == 0) {
        return null
    }
    try {
        val e = first()
        removeFirst()
        return e
    } catch(e:Exception) {
        return null
    }
}

fun <T>MutableList<T>.popLast() : T? {
    if (count() == 0) {
        return null
    }
    val e = last()
    removeLast()
    return e
}

fun <T>MutableList<T>.sortWithCondition(sortFunc:(a:T, b:T) -> Int) {
    val c = Comparator<T>(sortFunc)
    sortWith(c)
}

fun <T>MutableList<T>.removeLast() : T {
    return removeAt(count() - 1)
}

fun <T>MutableList<T>.removeAll() {
    clear()
}

fun <T>MutableList<T>.insert(e:T, at:Int) {
    return add(index = at, element = e)
}

// remove a value from a map
fun <K, V> MutableMap<K, V>.removeByValue(value:V) : Boolean {
    var del = false
    forEach { k, v ->
        if (v == value) {
            remove(k)
            del = true
        }
    }
    return del
}

// remove a value from a list
fun <T> MutableList<T>.removeByValue(value:T) : Boolean {
    return remove(value)
}

// ZTS is used for internationalization, currently dummy does nothing, returns stirng
fun ZTS(str: String, langCode: String = "", filePath: String = "", args:List<Any> = listOf()) : String {
    val a = args.toTypedArray()
    return String.format(str, * a)
}

fun <T : Comparable<T>> List <T>.Max() : T {
    var mi = -1
    for (i in 0 .. this.count()) {
        if (get(mi).compareTo(get(i)) < 0) {
            mi = i
        }
    }
    return get(mi)
}

fun <K, T : Comparable<T>> List <K>.Max(field:(t:K) -> T) : K {
    var mi = -1
    for (i in 0 .. this.lastIndex) {
        if (mi == -1 || field(get(mi)).compareTo(field(get(i))) < 0) {
            mi = i
        }
    }
    return get(mi)
}

fun <K, T : Comparable<T>> List <K>.Min(field:(t:K) -> T) : K {
    var mi = -1
    for (i in 0 .. this.lastIndex) {
        if (mi == -1 || field(get(mi)).compareTo(field(get(i))) > 0) {
            mi = i
        }
    }
    return get(mi)
}

