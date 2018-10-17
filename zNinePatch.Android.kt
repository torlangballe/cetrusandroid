package com.github.torlangballe.cetrusandroid

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.NinePatch

import java.nio.ByteBuffer
import java.nio.ByteOrder

object NinePatchBitmapFactory {
    private val NO_COLOR = 0x00000001 // The 9 patch segment is not a solid color.
    private val TRANSPARENT_COLOR = 0x00000000 // The 9 patch segment is completely transparent.

    fun createNinePatch(res: Resources, bitmap: Bitmap, top: Int, left: Int, bottom: Int, right: Int): NinePatch {
        val buffer = getByteBuffer(top, left, bottom, right)
        val is9 = NinePatch.isNinePatchChunk(buffer.array())
        return NinePatch(bitmap, buffer.array())
    }

    private fun getByteBuffer(top: Int, left: Int, bottom: Int, right: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(84).order(ByteOrder.nativeOrder())
        buffer.put(0x01.toByte())
        buffer.put(0x02.toByte())
        buffer.put(0x02.toByte())
        buffer.put(0x09.toByte())
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(left)
        buffer.putInt(right)
        buffer.putInt(top)
        buffer.putInt(bottom)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        return buffer
    }
}
