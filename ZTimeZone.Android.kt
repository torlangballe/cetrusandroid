//
//  ZTimeZoneAndroid.swift

//  Created by Tor Langballe on /3/12/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

import java.util.TimeZone

class ZTimeZone (val timezone:TimeZone = TimeZone.getDefault()) {
    companion object {
        val UTC: ZTimeZone
            get() = ZTimeZone(TimeZone.getTimeZone("UTC"))
        val GTM: ZTimeZone
            get() = ZTimeZone(TimeZone.getTimeZone("GMT"))
        val DeviceZone: ZTimeZone
            get() = ZTimeZone(TimeZone.getDefault())
    }

    constructor(identifier:String) : this(TimeZone.getTimeZone(identifier)) {}

    val NiceName: String
        get() {
            return timezone.getDisplayName()
        }
    val HoursFromUTC: Double
        get() = timezone.rawOffset.toDouble() * 1000 * 3600

    fun CalculateOffsetHours(time: ZTime = ZTime.Now()) : Pair<Double, Double> {
        val secs = timezone.getOffset(time.instant.toEpochMilli()) * 1000
//        val lsecs = TimeZone.autoupdatingCurrent.secondsFromGMT(for = time.date)
//        localDeltaHours = (secs - lsecs).toDouble() / 3600
        return Pair(secs.toDouble() / 3600, 0.0)
    }

    fun IsUTC() : Boolean =
            this.timezone.id == UTC.timezone.id

    fun compareTo(t: ZTime) : Int {
        ZNOTIMPLEMENTED()
        return 1
    }
}
