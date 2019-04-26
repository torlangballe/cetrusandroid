package com.github.torlangballe.cetrusandroid

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

// looks betteri: https://blog.obdev.at/representing-socket-addresses-in-swift-using-enums/

// simple class to send UDP at the moment all methods run synchronously, address lookup also takes time
class ZSocket {
    var inetAddress: InetAddress? = null
    var port = 0

    companion object {
        fun MakeUDP(address: ZIPAddress, port: Int): Pair<ZSocket?, ZError?> {
            var s = ZSocket()
            s.port = port
            s.inetAddress = InetAddress.getByName(address.GetIp4String())
            return Pair(s, null)
        }

        fun SendWithUDP(address: ZIPAddress, port: Int, data: ZData): ZError? {
            val (s, err) = MakeUDP(address, port)
            if (err != null) {
                return err
            }
            return s!!.SendWithUDP(data)
        }
    }
    fun SendWithUDP(data: ZData) : ZError? {
        val socket = DatagramSocket()
        try {
            val dp: DatagramPacket
            dp = DatagramPacket(data.data, data.Length, inetAddress!!, port)
            socket.setBroadcast(true)
            socket.send(dp)
            socket.close()
        } catch (e: Exception) {
            return ZNewError(e.localizedMessage)
        }
        return null
    }
}
