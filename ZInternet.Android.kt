package com.github.torlangballe.cetrusandroid

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ZIPAddress {
    var address: InetAddress? = null
}

class ZInternet {
    companion object {
        fun ResolveAddress(address: String, got: (a: ZIPAddress) -> Unit) {
            var ip = ZIPAddress()
            ip.address = InetAddress.getByName(address)
            got(ip)
        }

        fun SendWithUDP(address: ZIPAddress, port: Int, data: ZData, done: (e: ZError?) -> Unit) {
            var ds: DatagramSocket? = null
            try {
                ds = DatagramSocket()
                val dp: DatagramPacket
                dp = DatagramPacket(data.data, data.length, address.address, port)
                ds!!.setBroadcast(true)
                ds!!.send(dp)
            } catch (e: Exception) {
                done(ZNewError(e.localizedMessage))
            } finally {
                if (ds != null) {
                    ds!!.close()
                }
                done(null)
            }
        }
    }
}