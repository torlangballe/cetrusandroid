//
//  ZActivityIndicator.Android.kt
//
//  Created by Tor Langballe on /12/09/18.
//

package com.github.torlangballe.cetrusandroid

import android.widget.ProgressBar

class ZActivityIndicator: ProgressBar, ZView {
    override fun View() : UIView = this
    override var objectName = "activity"
    override var isHighlighted: Boolean = false
    override var Usable:Boolean = true

    constructor(big: Boolean = true, dark: Boolean = false) : super(zMainActivityContext) {
        isIndeterminate = true
    }

    fun Start(start: Boolean = true, whenVisible: Boolean = true) {
    }
}
