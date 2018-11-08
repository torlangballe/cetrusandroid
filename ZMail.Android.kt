//
//  ZMail.Android.kt
//
//  Created by Tor Langballe on /07/11/18.
//

package com.github.torlangballe.cetrusandroid

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast

class ZMail {
    data class Address(
            var address:String = "",
            var name:String = "")
    var sender = mutableListOf<Address>()
    var replyto = mutableListOf<Address>()
    var cc = mutableListOf<Address>()
    var bcc = mutableListOf<Address>()
    var from = mutableListOf<Address>()
    var to = mutableListOf<Address>()
    var subject = ""
    var body = ""
}

class ZMailComposer {
    var doneHandler: ((sent: Boolean) -> Unit)? = null

    fun CanSend() : Boolean =
            true

    fun PopDraft(mail: ZMail, files: MutableList<ZFileUrl> = mutableListOf<ZFileUrl>(), isHtml: Boolean = false, done: (sent: Boolean) -> Unit) {
        val i = Intent(Intent.ACTION_SEND)
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, getAddresses(mail.to).toTypedArray())
        i.putExtra(Intent.EXTRA_SUBJECT, mail.subject)
        i.putExtra(Intent.EXTRA_TEXT , mail.body)
        try {
            zMainActivity!!.startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex:ActivityNotFoundException) {
            Toast.makeText(zMainActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun getAddresses(addresses: MutableList<ZMail.Address>) : List<String> {
    var all = mutableListOf<String>()
    for (a in addresses) {
        all.append(a.address)
    }
    return all
}
