//
//  ZCanvas.swift
//
//  Created by Tor Langballe on /15/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.*

typealias ZMatrix = Matrix

enum class ZCanvasBlendMode {
    normal, multiply, screen, overlay, darken, lighten, colorDodge, colorBurn, softLight, hardLight, difference, exclusion, hue, saturation, color, luminosity
}

fun ZMatrixForRotatingAroundPoint(point: ZPos, deg: Double) : ZMatrix {
    var transform = ZMatrix() // is identity
    transform.setTranslate(point.x.toFloat(), point.y.toFloat())
    transform.setRotate(deg.toFloat())
    //    transform = transform.concatenating(r)
    transform.setTranslate(-point.x.toFloat(), -point.y.toFloat())
    return transform
}

fun ZMatrixForRotationDeg(deg: Double) : ZMatrix {
    var transform = ZMatrix() // is identity
    transform.setRotate(deg.toFloat())
    return transform
}

data class ZCanvas(var context: Canvas) {
    var paint = Paint()
    var tileImage: ZImage? = null

    fun SetColor(color: ZColor, opacity: Double = -1.0) {
        if (color.tileImage!= null) {
            this.tileImage = color.tileImage
        }
        var vcolor = color
        if (opacity != -1.0) {
            vcolor = vcolor.OpacityChanged(opacity)
        }
        paint.color = vcolor.rawColor.toArgb()
//        context.setStrokeColor(vcolor.color.cgColor)
//        context.setFillColor(vcolor.color.cgColor)
    }

    fun FillPath(path: ZPath, eofill: Boolean = false) {
        if (tileImage != null) {
            val patternBMPshader = BitmapShader(tileImage!!.bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            var paint = Paint()

            val matrix = Matrix()
            val m = (1.0 / tileImage!!.scale).toFloat()
            matrix.setScale(m, m)
            patternBMPshader.setLocalMatrix(matrix)
            paint.setColor(Color.CYAN)
            paint.setShader(patternBMPshader)
            context.drawPath(path.path, paint)
        } else {
            paint.style = Paint.Style.FILL
            context.drawPath(path.path, paint)
        }
    }

    fun SetMatrix(matrix: ZMatrix) {
        ZNOTIMPLEMENTED() // this isn't done properly yet, this is for resetting from zero, might not work
        context.concat(matrix)
    }

    fun Transform(matrix: ZMatrix) {
        context.concat(matrix)
    }
/*
    fun drawTile(bitmap:ZImage) {
        val bmp = BitmapFactory.decodeResource(zMainActivityContext!!.resources, R.drawable.redbutton_2x)

        val patternBMPshader = BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        var paint = Paint()
        paint.setColor(Color.CYAN)
        paint.setShader(patternBMPshader)

        val r = RectF(0f, 0f, 2000f, 200f)
        canvas.drawRect(r, paint)
    }
*/

    fun ClipPath(path: ZPath, exclude: Boolean = false, eofill: Boolean = false) {
        ZNOTIMPLEMENTED() // doesn't handle exclude etc yet
        context.clipPath(path.path)
    }

    fun GetClipRect() : ZRect {
        val r = context.clipBounds
        var rf = RectF(r.left.toFloat(), r.top.toFloat(), r.right.toFloat(), r.bottom.toFloat())
        return ZAndroidRectFToZRect(rf)
    }

    fun StrokePath(path: ZPath, width: Double, type: ZPath.LineType = ZPath.LineType.round) {
        var tempPaint = paint

        tempPaint.style = Paint.Style.STROKE
        tempPaint.strokeCap = Paint.Cap.ROUND
        tempPaint.strokeWidth = width.toFloat()

        context.drawPath(path.path, tempPaint)
    }

    fun DrawPath(path: ZPath, strokeColor: ZColor, width: Double, type: ZPath.LineType = ZPath.LineType.round, eofill: Boolean = false) {
        FillPath(path)
        StrokePath(path, width, type)
    }

    fun DrawImage(image: ZImage, destRect: ZRect, align: ZAlignment = ZAlignment.None, opacity: Float = 1f, blendMode: ZCanvasBlendMode = ZCanvasBlendMode.normal, corner:Double? = null, margin: ZSize = ZSize()) : ZRect {
        var vdestRect = destRect
        if (align != ZAlignment.None) {
            vdestRect = vdestRect.Align(image.Size, align = align, marg = margin)
        } else {
            vdestRect = vdestRect.Expanded(-margin)
        }
        if (corner != null) {
            PushState()
            val path = ZPath(rect = vdestRect, corner = ZSize(corner!!, corner!!))
            ClipPath(path)
        }
        val r = ZRectToAndroidRectF(vdestRect)
        var o = (opacity * 255).toInt()
        if (image.ninepatch != null) {
            image.ninepatch!!.paint = Paint()
            image.ninepatch!!.paint.alpha = o
            image.ninepatch!!.draw(context, r)
        } else if (image.bitmap != null) {
            var paint = Paint()
            paint.alpha = o
            context.drawBitmap(image.bitmap, null, r, paint)
        }
        if (corner != null) {
            PopState()
        }
        return vdestRect
    }

    fun PushState() {
        context.save()
    }

    fun PopState() {
        context.restore()
    }

    fun ClearRect(rect: ZRect) {
        context.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun SetDropShadow(delta: ZSize = ZSize(3, 3), blur: Float = 3f, color: ZColor = ZColor.Black()) {
    }

    fun SetDropShadowOff(opacity: Float = -1f) {
    }

    fun DrawGradient(path: ZPath? = null, colors: List<ZColor>, pos1: ZPos, pos2: ZPos, locations: List<Float> = mutableListOf<Float>()) {
        if (colors.count() > 0) {
            val c = colors.first()
            var r = ZRect()
            r.Min = pos1
            r.Max = pos2
            var p = ZPath(rect = r)
            if (path != null) {
                p = path
            }
            SetColor(c)
            FillPath(p)
        }
    }

    fun DrawRadialGradient(path: ZPath? = null, colors: List<ZColor>, center: ZPos, radius: Double, endCenter: ZPos? = null, startRadius: Double = 0.0, locations: List<Float> = mutableListOf<Float>()) {
    }

    fun DrawText(text:String, font: ZFont, color: ZColor, pos: ZPos, align: ZAlignment) {
        var paint = Paint()
        paint.color = color.color.toArgb()
        var a = Paint.Align.LEFT
        if (align and ZAlignment.Right) {
            a = Paint.Align.RIGHT
        } else if(align and ZAlignment.HorCenter) {
            a = Paint.Align.CENTER
        }
        paint.setTextAlign(a)
        paint.setTextSize(font.size.toFloat())
        paint.setTypeface(font.typeface)
        context.drawText(text, pos.x.toFloat(), pos.y.toFloat(), paint)
    }
}

fun ZAndroidRectFToZRect(r:RectF) : ZRect {
    return ZRect(r.left.toDouble(), r.top.toDouble(), r.right.toDouble(), r.bottom.toDouble())
}

fun ZRectToAndroidRectF(r: ZRect) : RectF {
    return RectF(r.Min.x.toFloat(), r.Min.y.toFloat(), r.Max.x.toFloat(), r.Max.y.toFloat())
}

fun ZAndroidRectFToRect(r: RectF) : Rect {
    return Rect(r.left.toInt(), r.top.toInt(), r.right.toInt(), r.bottom.toInt())
}

