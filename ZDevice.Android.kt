
//
//  ZDevice.swift
//
//  Created by Tor Langballe on /24/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.os.Build

data class ZDevice (val _dummy: Int = 0) {
    companion object {
        val IsIPad: Boolean
            get() = false
        val IsIPhone: Boolean
            get() = false
        val DeviceName: String
            get() = "Android Thing"
        //mac:    nsstr = [(NSString *)SCDynamicStoreCopyComputerName(NULL, NULL) autorelease];  //  NSString *localHostname = [(NSString *)SCDynamicStoreCopyLocalHostName(NULL) autorelease];
        val IdentifierForVendor: String?
            get() = "xxx"
        //mac: ZStrLowerCased(ZEthernet::GetMainMACAddress().GetStripped(":"));
        val BatteryLevel: Float
            get() {
                return 1f
            }
        val IsDeviceCharging: Int
            get() {
                return 0
            }
        val FreeAndUsedDiskSpace: Pair<Int, Int>
            get() = Pair(1024 * 1024 * 300, 1024 * 1024 * 32)
        val OSVersionString: String
            get() {
                return Build.VERSION.RELEASE
            }
        val TimeZone: ZTimeZone
            get() = ZTimeZone.DeviceZone
        val DeviceType: Triple<String, Int, String>
            get() {
                return Triple("", 0, "")
            }

        fun GetMemoryUsedAndFree() : Pair<Long, Long> {
            return Pair(0, 0)
        }

        fun GetNetworkSSIDs() : List<String> {
            return listOf<String>()
        }
    }

    enum class RemoveCommand(val rawValue: Int) {
        togglePlaypause(0), play(1), pause(2), nextTrack(3), stop(4), previousTrack(5), beginSeekingBackward(6), endSeekingBackward(7), beginSeekingForward(8), endSeekingForward(9);
    }
}
