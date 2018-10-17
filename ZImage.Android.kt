//
//  ZImage.Android.kt
//
//  Created by Tor Langballe on /15/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.*
import android.graphics.Bitmap
import android.graphics.LightingColorFilter


private fun getScaleFromName(name:String) : Float {
    val stub = ZFileUrl.GetPathParts(name).stub
    val end = ZStr.TailUntil(stub, "@")
    val (has, n) = ZStr.HasSuffixWithRest(end, "x")
    if (has) {
        return ZStr.ToDouble(n, 1.0)!!.toFloat()
    }
    return 1f
}

/*
private fun namedImageRes(name:String) : Bitmap? {
    val res = zMainActivityContext!!.resources
    val pn = zMainActivityContext!!.getPackageName()
    val (stem, _) = ZStr.GetStemAndExtension(fileName = name)
    val resourceId = res.getIdentifier(stem, "drawable", pn)

    var o = BitmapFactory.Options()
    o.inScaled = false
    val bitmap = BitmapFactory.decodeResource(res, resourceId, o)
    return bitmap
}
*/

private fun namedImageSingle(name:String) : Bitmap? {
    try {
        val bstream = zMainActivityContext!!.getAssets().open("images/" + name)
        val (stem, _) = ZStr.GetStemAndExtension(fileName = name)

        var o = BitmapFactory.Options()
        o.inScaled = false
        val bitmap = BitmapFactory.decodeStream(bstream, null, o)
        val density = getScaleFromName(name)
        bitmap.density = density.toInt()
        return bitmap
    }
    catch(e:Exception) {
        return null
    }
}

private fun namedImage(name:String) : Bitmap? {
    var bmp = namedImageSingle(name)
    if (bmp != null) {
        return bmp
    }
    val (stem, ext) = ZStr.GetStemAndExtension(fileName = name)
    for (i in 2 .. 3) {
        val n = stem +"@$i" + "x." + ext
        bmp = namedImageSingle(n)
        if (bmp != null) {
            return bmp
        }
    }
    return null
}

class ZImage(var bitmap:Bitmap? = null) {
    var ninepatch:NinePatch? = null
    var scale:Float = 1f

    constructor(named:String) :
        this(namedImage(named)) {
            if (bitmap != null) {
                scale = bitmap!!.density.toFloat()
            }
        }

    companion object {
        fun Colored(color: ZColor, size: ZSize) : ZImage {
            ZNOTIMPLEMENTED()
            return ZImage()
        }
        var MainCache = ZImageCache()

        fun DownloadFromUrl(url: String, cache: Boolean = true, maxSize: ZSize? = null, done: ((image: ZImage?) -> Unit)? = null) : ZURLSessionTask? {
            ZNOTIMPLEMENTED()
            return ZURLSessionTask()
        }
        fun FromFile(file: ZFileUrl) : ZImage? {
            ZNOTIMPLEMENTED()
            return null
        }
        fun GetNamedImagesFromWildcard(wild: String) : List<ZImage> {
            return listOf<ZImage>()
        }
    }

    fun Make9PatchImage(capInsets: ZRect) : ZImage {
        if (bitmap != null) {
            val image = ZImage(null)
            val left = capInsets.Min.x.toInt()
            val top = capInsets.Min.y.toInt()
            val right = (Size.w - capInsets.Max.x).toInt()
            val bottom = (Size.h - capInsets.Max.y).toInt()
            image.ninepatch = NinePatchBitmapFactory.createNinePatch(zMainActivityContext!!.resources, bitmap!!, top, left, bottom, right)
            image.scale = scale
            return image
        }
        return this
    }

    val Size: ZSize
        get() {
            if (ninepatch != null) {
                val s = ZSize(ninepatch!!.width.toDouble(), ninepatch!!.height.toDouble())
                return s
            }
            if (bitmap != null) {
                return ZSize(bitmap!!.width, bitmap!!.height) / scale.toDouble()
            }
            return ZSize(0, 0)
        }

    fun TintedWithColor(color: ZColor) : ZImage {
        val s = Size * scale.toDouble()
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val bmp = Bitmap.createBitmap(s.w.toInt(),s.h.toInt(), conf) // this creates a MUTABLE bitmap
        val canvas = Canvas(bmp)

        val filter = LightingColorFilter(color.color.toArgb(), 0x00000000)
        val paint = Paint()
        paint.setColorFilter(filter)

        val r = ZRectToAndroidRectF(ZRect(size = s))
        if (ninepatch != null) {
            var np = ninepatch!!
            np.paint = paint
            np.draw(canvas, r)
        } else if(bitmap != null) {
            canvas.drawBitmap(bitmap!!, null, r, paint)
        }
        val i = ZImage()
        i.bitmap = Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false)
        i.scale = scale

        if (ninepatch != null) {
            return i.Make9PatchImage(ZRect(6.0, 13.0, 6.0, 13.0))
        }

        return i
    }

    fun GetScaledInSize(size: ZSize, proportional: Boolean = true) : ZImage? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun GetCropped(crop: ZRect) : ZImage {
        ZNOTIMPLEMENTED()
        return this
    }

    fun GetLeftRightFlipped() : ZImage {
        ZNOTIMPLEMENTED()
        return this
    }

    fun Normalized() : ZImage {
        return this
    }

    fun GetRotationAdjusted(flip: Boolean = false) : ZImage {
        ZNOTIMPLEMENTED()
        return this
    }

    fun Rotated(deg: Double, around: ZPos? = null) : ZImage? {
        ZNOTIMPLEMENTED()
        return this
    }

    fun FixedOrientation() : ZImage? {
        return this
    }

    fun SaveToPng(file: ZFileUrl) : ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun SaveToJpeg(file: ZFileUrl, quality: Float = 0.8f) : ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun ForPixels(got: (pos: ZPos, color: ZColor) -> Unit) {
        val height = bitmap!!.getHeight()
        val width = bitmap!!.getWidth()

        // Iterate over each row (y-axis)
        for (y in 0 .. height) {
            for (x in 0 .. width) {
                val pixel = bitmap!!.getPixel(x, y)
                val col = ZColor(colorInt = pixel)
                got(ZPos(x.toDouble(), y.toDouble()), col)
//                bitmap.setPixel(x, y, pixel);
            }
        }
    }

    fun ClipToCircle(fit: ZSize = ZSize(0, 0)) : ZImage? {
        ZNOTIMPLEMENTED()
        return null
    }

    class ZImageUploader {
        var url: String = ""
        var strId: String = ""
        var error: ZError? = null
        var image: ZImage? = null
        var done: ((uploader: ZImageUploader) -> Unit)? = null

        fun SetDone(done: (uploader: ZImageUploader) -> Unit) {
            this.done = done
            if (!this.url.isEmpty()) {
                done(this)
            }
        }

        fun SetUrl(url: String) {
            this.url = url
            done?.invoke(this)
            // done can be nil and ignored here...
        }
    }
}

fun ZMakeImageFromDrawFunction(size: ZSize, scale: Float = 0f, draw: (size: ZSize, canvas: ZCanvas) -> Unit) : ZImage {
    ZNOTIMPLEMENTED()
    return ZImage()
}

data class _Cache(
        var image: ZImage? = null,
        var stamp: ZTime = ZTime(),
        var getting: Boolean = true) {
}

class ZImageCache{
    var maxHours = 24.0
    var maxSize: ZSize? = null
    var maxByteSize: Long? = null

    var cache = mutableMapOf<String, _Cache>()

    fun DownloadFromUrl(url: String, done: ((image: ZImage?) -> Unit)? = null) : ZURLSessionTask? {
        if (url.isEmpty()) {
            done?.invoke(null)
            return null
        }
        var c = cache[url]
        if (c != null) {
            if (c.getting) {
                if (c.stamp.Since() > 60) {
                    cache.remove(key = url) // is this right?
                    done?.invoke(null)
                    return null
                }
                ZPerformAfterDelay(2.0) { ->
                    this.DownloadFromUrl(url, done = done)
                }
                return null
            }
            c.stamp = ZTime.Now()
            cache[url] = c
            done?.invoke(c.image)
            return null
        }
        var totalSize: Long = 0
        for ((u, ca) in cache) {
            if (ca.stamp.Since() > maxHours * 3600 || !ca.getting && ca.image == null && ZMath.RandomN(10) == 5) {
                cache.remove(key = u)
            } else {
                totalSize += imageSize(ca.image)
            }
        }
        //fix:
        /*
        while (maxByteSize != null && maxByteSize!! < totalSize && cache.size > 0) {
            val oldestTupple = cache.reduce(cache.firstOrNull()!!) { r, t  ->
                if (t.1.stamp < r.1.stamp) t else r
            }
            totalSize -= imageSize(oldestTupple.1.image)
            cache.removeValue(forKey = oldestTupple.0)
        }
            */
        val c2 = _Cache()
        c2.stamp = ZTime.Now()
        c2.getting = true
        this.cache[url] = c2
        return ZImage.DownloadFromUrl(url, cache = false, maxSize = maxSize) { image ->
            val c3 = this.cache[url]
            if (c3 != null) {
                c3.image = image
                c3.getting = false
                this.cache[url] = c3
                if (image == null) {
                    com.github.torlangballe.cetrusandroid.ZDebug.Print("null image:", url)
                }
            }
            done?.invoke(image)
        }
    }

    private fun imageSize(image: ZImage?) : Long {
        if (image != null) {
            return (image.Size.Area() * 3).toLong() / 5
        }
        return 0
    }
}


