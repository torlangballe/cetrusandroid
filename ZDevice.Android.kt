
//
//  ZDevice.swift
//
//  Created by Tor Langballe on /24/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.Manifest
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.content.Context
import android.os.Build
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION
import android.os.StatFs
import android.os.Environment.getDataDirectory
import android.app.Activity
import android.app.ActivityManager
import java.io.IOException
import java.io.RandomAccessFile
import android.telephony.TelephonyManager
import android.net.ConnectivityManager
import android.text.TextUtils
import android.net.wifi.WifiInfo
import android.support.v4.content.ContextCompat.getSystemService
import android.net.NetworkInfo
import android.provider.Settings
import android.provider.Settings.Secure




data class ZDevice (val _dummy: Int = 0) {
    companion object {
        val IsIPad: Boolean
            get() = false

        val IsIPhone: Boolean
            get() = false

        val DeviceName: String
            get() {
                return Build.PRODUCT
            }

        val FingerPrint: String
            get() {
                return Build.FINGERPRINT
            }

        val IdentifierForVendor: String?
            get() = Secure.getString(zMainActivityContext!!.getContentResolver(), Secure.ANDROID_ID)

        val Manufacturer: String = Build.MANUFACTURER

        val BatteryLevel: Float
            get() {
                return 1f
            }

        val IsDeviceCharging: Int
            get() {
                return 0
            }

        val OSVersionString: String
            get() {
                return Build.VERSION.RELEASE
            }

        val TimeZone: ZTimeZone
            get() = ZTimeZone.DeviceZone

        val DeviceType: Triple<String, Int, String>
            get() {
                return Triple(Build.DEVICE, 1, "")
            }

        val HardwareType: String = Build.HARDWARE
        val HardwareModel: String = Build.MODEL
        val HardwareBrand: String = Build.BRAND

        private fun getAvailableInternalMemorySize(): Long {
            val path = getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long
            val availableBlocks: Long
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.blockSizeLong
                availableBlocks = stat.availableBlocksLong
            } else {
                blockSize = stat.blockSize.toLong()
                availableBlocks = stat.availableBlocks.toLong()
            }
            return availableBlocks * blockSize
        }

        private fun getTotalInternalMemorySize(): Long {
            val path = getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long
            val totalBlocks: Long
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.blockSizeLong
                totalBlocks = stat.blockCountLong
            } else {
                blockSize = stat.blockSize.toLong()
                totalBlocks = stat.blockCount.toLong()
            }
            return totalBlocks * blockSize
        }

        fun FreeAndUsedDiskSpace() : Pair<Long, Long> {
            return Pair(getAvailableInternalMemorySize(), getTotalInternalMemorySize())
        }

        private fun getTotalRAM(): Long {
            var totalMemory: Long = 0
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                val mi = ActivityManager.MemoryInfo()
                val activityManager = zMainActivityContext!!.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                if (activityManager != null) {
                    activityManager.getMemoryInfo(mi)
                    totalMemory = mi.totalMem
                }
            } else {
                var reader: RandomAccessFile? = null
                val load: String
                try {
                    reader = RandomAccessFile("/proc/meminfo", "r")
                    load = reader!!.readLine().replace("\\D+", "")
                    totalMemory = Integer.parseInt(load).toLong()
                } catch (e: IOException) {
                    ZDebug.Print("getTotalRAM err:", e)
                } finally {
                    if (reader != null) {
                        try {
                            reader!!.close()
                        } catch (e: IOException) {
                            ZDebug.Print("getTotalRAM err2:", e)
                        }

                    }
                }
            }
            return totalMemory
        }

        fun GetMemoryFreeAndUsed() : Pair<Long, Long> {
            return Pair(getTotalRAM(), 0L)
        }

        fun GetNetworkSSIDs() : List<String> {
            return listOf<String>()
        }

        fun IsWifiEnabled(): Boolean {
            var wifiState = false
            if (ZDebug.HasPermission(ACCESS_WIFI_STATE)) {
                val wifiManager = zMainActivityContext!!.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiState = wifiManager.isWifiEnabled
            }
            return wifiState
        }

        fun GetIPv4Address(): String? {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.getInetAddresses())
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress()) {
                            val sAddr = addr.getHostAddress().toUpperCase(Locale.getDefault())
                            val isIPv4 = addr is Inet4Address
                            if (isIPv4) {
                                return sAddr
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                ZDebug.Print("GetIPv4Address err:", e)
            }
            return null
        }

        fun GetIPv6Address(): String? {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress.toUpperCase(Locale.getDefault())
                            val isIPv4 = addr is Inet4Address
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 port suffix
                                return if (delim < 0) sAddr else sAddr.substring(0, delim)
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                ZDebug.Print("GetIPv6Address err:", e)
            }
            return null
        }

        fun GetLanMAC() : String? {
            var macAddress:String? = null
            try {
                val allNetworkInterfaces = Collections.list(
                    NetworkInterface
                        .getNetworkInterfaces()
                )
                for (nif in allNetworkInterfaces) {
                    if (!nif.name.equals("eth0", ignoreCase = true))
                        continue

                    val macBytes = nif.hardwareAddress ?: return macAddress

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    macAddress = res1.toString()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return macAddress
        }

        fun GetWifiMAC(): String? {
            var result:String? = null
            if (ZDebug.HasPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Hardware ID are restricted in Android 6+
                    // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
                    var interfaces: Enumeration<NetworkInterface>? = null
                    try {
                        interfaces = NetworkInterface.getNetworkInterfaces()
                    } catch (e: SocketException) {
                        ZDebug.Print("getWifiMAC err:", e)
                    }

                    while (interfaces != null && interfaces.hasMoreElements()) {
                        val networkInterface = interfaces.nextElement()

                        var addr: ByteArray? = ByteArray(0)
                        try {
                            addr = networkInterface.hardwareAddress
                        } catch (e: SocketException) {
                            ZDebug.Print("getWifiMAC err2:", e)
                        }

                        if (addr == null || addr.size == 0) {
                            continue
                        }

                        val buf = StringBuilder()
                        for (b in addr) {
                            buf.append(String.format("%02X:", b))
                        }
                        if (buf.length > 0) {
                            buf.deleteCharAt(buf.length - 1)
                        }
                        val mac = buf.toString()
                        val wifiInterfaceName = "wlan0"
                        result = if (wifiInterfaceName == networkInterface.name) mac else result
                    }
                } else {
                    val wm = zMainActivityContext!!.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                    result = wm.connectionInfo.macAddress
                }
            }
            return result
        }

        enum class ZNetworkType { Unknown, WifiMax, CellularUnknown, Cellular2G, Cellular3G, Cellular4G, Cellular5G, CellularXG }

        fun GetWifiLinkSpeed(): String? {
            var result: String? = null
            if (ZDebug.HasPermission(Manifest.permission.ACCESS_WIFI_STATE) && ZDebug.HasPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                val cm = zMainActivityContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (cm != null) {
                    val networkInfo = cm.activeNetworkInfo
                    if (networkInfo == null) {
                        result = null
                    }

                    if (networkInfo != null && networkInfo.isConnected) {
                        val wifiManager =
                            zMainActivityContext!!.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                        if (wifiManager != null) {
                            val connectionInfo = wifiManager.connectionInfo
                            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                                result = connectionInfo.linkSpeed.toString() + " Mbps"
                            }
                        }
                    }
                }
            }
            return result
        }

        fun GetNetworkType(): ZNetworkType {
            var result = ZNetworkType.Unknown
            if (ZDebug.HasPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                val cm = zMainActivityContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                if (cm != null) {
                    val activeNetwork = cm.activeNetworkInfo
                    if (activeNetwork == null) {
                        result = ZNetworkType.Unknown
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_WIMAX) {
                        result = ZNetworkType.WifiMax
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        val manager = zMainActivityContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                        if (manager != null && manager.simState == TelephonyManager.SIM_STATE_READY) {
                            when (manager.networkType) {

                                // Unknown
                                TelephonyManager.NETWORK_TYPE_UNKNOWN -> result = ZNetworkType.CellularUnknown
                                // Cellular Data 2G
                                TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_1xRTT -> result =
                                        ZNetworkType.Cellular2G
                                // Cellular Data 3G
                                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> result =
                                        ZNetworkType.Cellular3G
                                // Cellular Data 4G
                                TelephonyManager.NETWORK_TYPE_LTE -> result = ZNetworkType.Cellular4G
                                // Cellular Data Unknown Generation
                                else -> result = ZNetworkType.CellularXG
                            }
                        }
                    }
                }
            }
            return result
        }
    }

    enum class RemoteCommand(val rawValue: Int) {
        togglePlaypause(0), play(1), pause(2), nextTrack(3), stop(4), previousTrack(5), beginSeekingBackward(6), endSeekingBackward(7), beginSeekingForward(8), endSeekingForward(9);
    }
}
