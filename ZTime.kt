//
//  ZTime.swift
//  Zed
//
//  Created by Tor Langballe on /31/10/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

val ZLocaleEngUsPosix = "en_US_POSIX"
enum class ZWeekday(val rawValue: Int) {
    none(0), mon(1), tue(2), wed(3), thu(4), fri(5), sat(6), sun(7);
    companion object : ZEnumCompanion<Int, ZWeekday>(ZWeekday.values().associateBy(ZWeekday::rawValue))
}
enum class ZMonth(val rawValue: Int) {
    none(0), jan(1), feb(2), mar(3), apr(4), may(5), jun(6), jul(7), aug(8), sep(9), oct(10), nov(11), dec(12);
    companion object : ZEnumCompanion<Int, ZMonth>(ZMonth.values().associateBy(ZMonth::rawValue))
}

data class ZGregorianParts(
        var year: Int = 0,
        var month: ZMonth = ZMonth.none,
        var day: Int = 0,
        var hour: Int = 0,
        var minute: Int = 0,
        var second: Int = 0,
        var nano: Int = 0,
        var weekday: ZWeekday = ZWeekday.none) {}

fun ZTime.Since() : Double =
        ZTime.Now() - this

fun ZTime.Until() : Double =
        this - ZTime.Now()

fun ZTime.IsAm(hour: Int) : Pair<Boolean, Int> {
    // isam, 12-hour hour
    var h = hour
    var am = true
    if (hour >= 12) {
        am = false
    }
    h %= 12
    if (h == 0) {
        h = 12
    }
    return Pair(am, h)
}

fun ZTime.Get24Hour(hour: Int, am: Boolean) : Int {
    var h = hour
    if (h == 12) {
        h = 0
    }
    if (!am) {
        h += 12
    }
    h %= 24
    return h
}

fun ZTime.GetNiceString(locale: String = ZLocaleEngUsPosix, timezone: ZTimeZone? = null) : String {
    if (IsToday()) {
        return GetString(format = "HH:mm", locale = locale, timezone = timezone) + " " + ZWords.GetToday()
    }
    return GetString(format = ZTimeNiceFormat, locale = locale, timezone = timezone)
}

fun ZTime.GetNiceDaysSince(locale: String = ZLocaleEngUsPosix, timezone: ZTimeZone? = null) : String {
    val now = ZTime.Now()
    val isPast = (now > this)
    val (day, _, _, _) = GetGregorianTimeDifferenceParts(now, timezone = timezone)
    var preposition = ZTS("ago")
    // generic word for 5 days ago
    if (!isPast) {
        preposition = ZTS("until")
    }
    // generic word for 5 days until
    when (day) {
        0 -> return ZWords.GetToday()
        1 -> return if (isPast) ZWords.GetYesterday() else ZWords.GetTomorrow()
        2, 3, 4, 5, 6, 7 -> return "${day} " + ZWords.GetDay(plural = true) + " " + preposition
        else -> return GetString(format = "MMM dd", locale = locale, timezone = timezone)
    }
}

fun ZTime.GetIsoString(format: String = ZTimeIsoFormat, useNull: Boolean = false) : String {
    if (useNull && IsNull) {
        return "null"
    }
    return GetString(format = format, timezone = ZTimeZone(identifier = "UTC"))
}

fun ZTime.Companion.GetDurationSecsAsHMSString(dur: Double) : String {
    var str = ""
    val h = dur.toInt() / 3600
    var m = dur.toInt() / 60
    if (h > 0) {
        m %= 60
        str = "${h}:"
    }
    val s = dur.toInt() % 60
    str += ZStr.Format("%02d:%02d", m, s)
    return str
}

val ZTimeIsoFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
// UploadFileToBucket
val ZTimeIsoFormatWithMSecs = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
val ZTimeIsoFormatCompact = "yyyyMMdd'T'HHmmss'Z'"
val ZTimeIsoFormatWithZone = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
// UploadFileToBucket
val ZTimeIsoFormatWithMSecsWithZone = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
val ZTimeIsoFormatCompactWithZone = "yyyyMMdd'T'HHmmssZZZZZ"
val ZTimeCompactFormat = "yyyy-MM-dd' 'HH:mm:ss"
val ZTimeNiceFormat = "HH:mm' 'dd-MMM-yyyy"
val ZTimeHTTPHeaderDateFormat = "EEEE, dd LLL yyyy HH:mm:ss zzz"
val ZTimeMinute = 60.0
val ZTimeHour = 3600.0
val ZTimeDay = 86400.0

class ZDeltaTimeGetter {
    var lastGetTime:ZTime = ZTimeNull
    var lastGetValue:Double? = null

    fun Get(get:() -> Double) : Pair<Double, Double> {
        var v = get()
        var delta = 0.0
        var interval = 0.0
        val t = ZTime.Now()
        if (lastGetValue != null) {
            delta = v - lastGetValue!!
            interval = t - lastGetTime
        }
        lastGetValue = v
        lastGetTime = ZTime.Now()

        return Pair(delta, interval)
    }
}

