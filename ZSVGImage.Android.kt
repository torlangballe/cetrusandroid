//
//  ZSVGImage.Android.swift
//
//  Created by Tor Langballe on 19/03/2019.
//

// https://bigbadaboom.github.io/androidsvg/

package com.github.torlangballe.cetrusandroid

import com.caverock.androidsvg.SVG

class ZSVGImage {
    var image: SVG? = null
    
    constructor(data: ZData) {
        image = SVG.getFromString(data.GetString())
    }
    
    fun Draw(canvas: ZCanvas, rect: ZRect = ZRect.Null) {
        if (rect.IsNull) {
            image!!.renderToCanvas(canvas.context)
        } else {
            image!!.renderToCanvas(canvas.context, ZRectToAndroidRectF(rect))
        }
    }
}
