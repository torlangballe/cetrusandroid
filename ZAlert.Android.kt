
//
//  ZAlert.swift
//
//  Created by Tor Langballe on /7/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.app.AlertDialog
import android.content.DialogInterface

class ZAlert {
    companion object {

        fun Say(text: String, ok: String = "🆗", cancel: String = "", other: String = "", destructive: String = "", subText: String = "", pressed: ((result: Result) -> Unit)? = null) {
            var vok = ok
            var vcancel = cancel
            if (vok == "🆗") {
                vok = ZWords.GetOk()
            }
            if (vcancel == "❌") {
                vcancel = ZWords.GetCancel()
            }

            val alertDialog = AlertDialog.Builder(zMainActivityContext).create()
            alertDialog.setTitle("")
            alertDialog.setMessage(text)
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, vok,
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
            alertDialog.show()

        }

        fun GetText(title: String, content: String = "", placeholder: String = "", ok: String = "", cancel: String = "", other: String? = null, subText: String = "", keyboardInfo: ZKeyboardInfo? = null, done: (text: String, result: Result) -> Unit) {
        }

        fun ShowError(text: String, error: ZError) {
            Say(text, subText = error.GetMessage())
            ZDebug.Print("Show Error:\n", text)
        }
    }

    enum class Result(val rawValue: Int) {
        ok(1), cancel(2), destructive(3), other(4);
    }
}
