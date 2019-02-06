package com.github.torlangballe.cetrusandroid

import java.security.NoSuchAlgorithmException

class ZCrypto {
    companion object {
        fun MD5(data:ZData) : ByteArray {
            val MD5 = "MD5"
            try {
                // Create MD5 Hash
                val digest = java.security.MessageDigest
                    .getInstance(MD5)
                digest.update(data.data)
                val messageDigest = digest.digest()

                // Create Hex String
                var bytes = ByteArray(messageDigest.size)
                messageDigest.forEachIndexed { i, b ->
                    bytes[i] = (0xFF and b.toInt()).toByte()
                }
                return bytes

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ByteArray(0)
        }
    }
}
