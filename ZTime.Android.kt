//
//  ZTime.Android.kt
//
//  Created by Tor Langballe on /31/10/15.
//

package com.github.torlangballe.cetrusandroid

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

//import java.time.format.DateTimeFormatter

val ZTimeNull = ZTime(Instant.MIN)
val ZTimeDistantFuture = ZTime(Instant.MAX)

class ZTime (val instant: Instant = Instant.now()) {

    val IsNull: Boolean = (instant == Instant.MIN) // don't compare to ZTimeNull here, too early

    val SecsSinceEpoc: Double
        get() {
            if (instant == Instant.MIN) {
                return Double.MIN_VALUE
            }
            if (instant == Instant.MAX) {
                return Double.MAX_VALUE
            }
            val m = instant.toEpochMilli()
            return m.toDouble() / 1000
        }

    companion object {
        fun Now() = ZTime()
    }

    constructor(year: Int, month: ZMonth = ZMonth.none, day: Int = -1, hour: Int = 0, minute: Int = 0, second: Int = 0, nano: Int = 0, timezone: ZTimeZone? = null) :
    this(intsToInstant(year, month, day, hour, minute, second, nano, timezone)) {
    }

    constructor(iso8601Z: String) : this(fromIso(iso8601Z)) {
    }

    constructor(format: String, dateString: String, locale: String = "", timezone: ZTimeZone? = null) :
        this(fromFormat(format, dateString, locale, timezone)) {
    }

    fun GetGregorianTimeParts(useAm: Boolean = false, timezone:ZTimeZone? = null) : ZGregorianParts {
        var g = ZGregorianParts()
        // not implemented
        return g
    }

    fun GetGregorianDateParts(timezone: ZTimeZone? = null) : ZGregorianParts {
        var g = ZGregorianParts()

        var tid = ZoneId.of(TimeZone.getDefault().id)
        if (timezone != null) {
            tid = timezone!!.timezone.toZoneId()
        }
        val date = LocalDateTime.ofInstant(instant, tid)
        g.day = date.dayOfMonth
        g.month = when(date.month) {
            Month.JANUARY -> ZMonth.jan
            Month.FEBRUARY -> ZMonth.feb
            Month.MARCH -> ZMonth.mar
            Month.APRIL -> ZMonth.apr
            Month.MAY -> ZMonth.may
            Month.JUNE-> ZMonth.jun
            Month.JULY-> ZMonth.jul
            Month.AUGUST -> ZMonth.aug
            Month.SEPTEMBER -> ZMonth.sep
            Month.OCTOBER -> ZMonth.oct
            Month.NOVEMBER -> ZMonth.nov
            Month.DECEMBER -> ZMonth.dec
        }
        g.year = date.year
        g.weekday = when(date.dayOfWeek) {
            DayOfWeek.MONDAY-> ZWeekday.mon
            DayOfWeek.TUESDAY -> ZWeekday.tue
            DayOfWeek.WEDNESDAY -> ZWeekday.wed
            DayOfWeek.THURSDAY -> ZWeekday.thu
            DayOfWeek.FRIDAY -> ZWeekday.fri
            DayOfWeek.SATURDAY-> ZWeekday.sat
            DayOfWeek.SUNDAY -> ZWeekday.sun
        }
        return g
    }

    fun GetGregorianDateDifferenceParts(toTime: ZTime, timezone: ZTimeZone? = null) : ZGregorianParts {
        var g = ZGregorianParts()
        // not implemented
        return g
    }

    fun GetGregorianTimeDifferenceParts(toTime: ZTime, timezone: ZTimeZone? = null) : ZGregorianParts {
        var g = ZGregorianParts()
        // not implemented
        return g
    }

    fun IsToday() : Boolean {
        return DateUtils.isToday(instant.toEpochMilli())
    }

    fun GetString(format: String = ZTimeIsoFormat, locale: String = ZLocaleEngUsPosix, timezone: ZTimeZone? = null) : String {
        var loca:Locale? = null
        var tid = ZoneId.of(TimeZone.getDefault().id)
        if (timezone != null) {
            tid = timezone!!.timezone.toZoneId()
        }
        val date = LocalDateTime.ofInstant(instant, tid)
        val (lang, ccode) = ZLocale.GetLangCodeAndCountryFromLocaleId(locale)
        loca = Locale(lang, ccode)
        val df = DateTimeFormatter.ofPattern(format, loca)
        return df.format(date)
    }

    operator fun plus(add:Double) : ZTime {
        return ZTime(instant.plusMillis((add * 1000).toLong()))
    }

    operator fun minus(sub:Double) : ZTime {
        return ZTime(instant.minusMillis((sub * 1000).toLong()))
    }

    operator fun minus(minus: ZTime) : Double {
        return SecsSinceEpoc - minus.SecsSinceEpoc
    }

    operator fun compareTo(a: ZTime) : Int {
        return instant.compareTo(a.instant)
    }
}

private fun fromIso(iso:String) : Instant {
//    val zdt = ZonedDateTime.parse(iso, iso)
//    var format = ZTimeIsoFormat
//    if (iso.contains(".")) {
//        format = ZTimeIsoFormatWithMSecs
//    }
//    val zone = ZTimeZone(identifier = "UTC")
//    return fromFormat(format, iso, "", zone)
    ZNOTIMPLEMENTED()
    return Instant.now()
}

private fun intsToInstant(year: Int, month: ZMonth, day: Int, hour: Int, minute: Int, second: Int, nano: Int, timezone: ZTimeZone?) : Instant {
    var sid = "UTC"
    if (timezone != null) {
        sid = timezone?.timezone.id
    }
    val m = month.rawValue
    val zdt = ZonedDateTime.of(year, month.rawValue, day, hour, minute, second, nano, ZoneId.of(sid))
    return zdt.toInstant()
}

private fun fromFormat(format:String, dateString:String, locale:String, timezone: ZTimeZone?) : Instant {
//    val zdt = ZonedDateTime.parse(dateString, format)
    ZNOTIMPLEMENTED()
    return Instant.now()
}

/*
private fun getDateComponent(time: ZTime, timezone: ZTimeZone?) : Pair<DateComponents, Calendar> {
    var cal = Calendar(identifier = Calendar.Identifier.gregorian)
    cal.firstWeekday = 2
    val comps = (cal as NSCalendar).components(listOf(.day, .month, .year), from = time)(// , .weekday
    comps as NSDateComponents).timeZone = timezone as TimeZone?
    return (comps, cal)
}
*/

// we need these here though they are in ZTime.kt but that isn't accessable yet
//private val isoFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
//private val isoFormatWithMSecs = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

