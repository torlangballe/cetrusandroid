//
//  ZPasteboard.Android.kt
//
//  Created by Tor Langballe on /18/9/18.
//

package com.github.torlangballe.cetrusandroid

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context

class ZPasteboard {
    companion object {
        var PasteString: String
            get() {
                val clipboard = zMainActivityContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                if (clipboard != null && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                    val item = clipboard.getPrimaryClip().getItemAt(0);
                    val text = item.getText().toString()
                    return text
                }
                return ""
            }
            set(text) {
                val clipboard = zMainActivityContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                clipboard?.setPrimaryClip(ClipData.newPlainText(text, text))
            }
    }
}
