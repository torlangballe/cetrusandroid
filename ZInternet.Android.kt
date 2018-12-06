package com.github.torlangballe.cetrusandroid

import android.net.TrafficStats
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.io.BufferedReader
import java.io.InputStreamReader

class ZIPAddress {
    var address: InetAddress? = null
    fun GetString() : String {
        if (address == null) {
            return ""
        }
        return address!!.hostAddress!!
    }
}

class ZInternet {
    companion object {
        fun ResolveAddress(address: String, got: (a: ZIPAddress) -> Unit) {
            ZGetBackgroundQue().async {
                var ip = ZIPAddress()
                ip.address = InetAddress.getByName(address)
                got(ip)
            }
        }

        fun SendWithUDP(address: ZIPAddress, port: Int, data: ZData, done: (e: ZError?) -> Unit) {
            var ds: DatagramSocket? = null
            try {
                ds = DatagramSocket()
                val dp: DatagramPacket
                dp = DatagramPacket(data.data, data.length, address.address, port)
                ds.setBroadcast(true)
                ds.send(dp)
            } catch (e: Exception) {
                done(ZNewError(e.localizedMessage))
            } finally {
                ds?.close()
                done(null)
            }
        }

        fun GetNetworkTrafficBytes(processUid: Int? = null): Long {
            if (processUid != null) {
                return TrafficStats.getUidRxBytes(processUid!!)
            }
            return TrafficStats.getTotalRxBytes()
        }

        fun PingAddressForLatency(ipAddress: ZIPAddress): Double? {
            val a = ipAddress.GetString()
            val pingCommand = "/system/bin/ping -c 1 $a"
            var inputLine: String? = ""
            var avgRtt:Double? = null

            try {
                // execute the command on the environment interface
                val process = Runtime.getRuntime().exec(pingCommand)
                // gets the input stream to get the output of the executed command
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

                inputLine = bufferedReader.readLine()
                while (inputLine != null) {
                    if (inputLine.length > 0 && inputLine.contains("avg")) {  // when we get to the last line of executed ping command
                        break
                    }
                    inputLine = bufferedReader.readLine()
                }
            } catch (e: IOException) {
                ZDebug.Print("getLatency: EXCEPTION")
                e.printStackTrace()
            }

            if (inputLine != null) {
                // Extracting the average round trip time from the inputLine string
                val afterEqual = inputLine!!.substring(inputLine.indexOf("="), inputLine.length).trim { it <= ' ' }
                val afterFirstSlash =
                    afterEqual.substring(afterEqual.indexOf('/') + 1, afterEqual.length).trim { it <= ' ' }
                val strAvgRtt = afterFirstSlash.substring(0, afterFirstSlash.indexOf('/'))
                avgRtt = java.lang.Double.valueOf(strAvgRtt) / 1000.0
            }
            return avgRtt
        }
    }
}


