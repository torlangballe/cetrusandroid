//
//  ZApp.Android.kt
//
//  Created by Tor Langballe on /15/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.widget.Toast
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager


var zMainActivityContext: Context? = null
var zMainActivity: Activity? = null
var lastOrientation = -1

fun zGetCurrentContext() : Context? {
    if (zMainActivityContext == null) {
        return zServiceContext
    }
    return zMainActivityContext
}

open class ZApp {
    companion object {
        var appFile: ZFileUrl? = null

        val Version: Triple<String, Float, Int>
            get() {
                val p = zGetCurrentContext()!!.getPackageManager()
                val v = p.getPackageInfo(zGetCurrentContext()!!.getPackageName(), 0)
                val s = v.versionName
                // version string, version with comma 1.2, build
                return Triple(s, 0f, 0)
            }

        fun SetupActivity(activity:Activity, first:Boolean) {
            zMainActivity = activity
            zMainActivityContext = activity.applicationContext
            if (ZIsTVBox()) {
                ZScreen.SoftScale = 2.0
            }
            if (!first) {
                zHandleOrientationChanged()
            }
        }

        fun handleMessage(message: Message) {
        }

        fun GetProcessId() : Int {
            return zGetCurrentContext()!!.getApplicationInfo().uid
        }
    }

    var activationTime = ZTimeNull
    var backgroundTime = ZTimeNull
    var startTime = ZTime.Now()
    var startedCount = 0
    var oldVersion = 0.0
    val IsActive: Boolean
        get() = !activationTime.IsNull
    val IsBackgrounded: Boolean
        get() = !backgroundTime.IsNull

    fun GetRuntimeSecs() : Double =
            ZTime.Now() - activationTime

    fun GetbackgroundTimeSecs() : Double =
            ZTime.Now() - backgroundTime

    constructor() : super() {
        activationTime = ZTime.Now()
        mainZApp = this
    }

    fun Quit() {
        zMainActivity?.finishAffinity()
        System.exit(0)
    }

    fun setVersions() {
        // this needs to be called by inheriting class, or strange stuff happens if called by ZApp
        val (_, ver, _) = Version
        oldVersion = ZKeyValueStore.DoubleForKey("ZVerson")
        ZKeyValueStore.SetDouble(ver.toDouble(), key = "ZVerson")
    }

    fun EnableAudioRemote(command: ZAudioRemoteCommand, on: Boolean) {
    }

    open fun HandleAppNotification(notification: ZNotification, action: String) {}

    open fun HandlePushNotificationWithDictionary(dict: MutableMap<String, ZAnyObject>, fromStartup: Boolean, whileActive: Boolean) {}

    open fun HandleLocationRegionCross(regionId: String, enter: Boolean, fromAdd: Boolean) {}

    open fun HandleMemoryNearFull() {}

    open fun HandleAudioInterrupted() {}

    open fun HandleAudioResume() {}

    open fun HandleAudioRouteChanged(reason: Int) {}

    open fun HandleAudioRemote(command: ZAudioRemoteCommand) {}

    open fun HandleRemoteAudioSeekTo(posSecs: Double) {}

    open fun HandleVoiceOverStatusChanged() {}

    open fun HandleBackgrounded(background: Boolean) {}

    open fun HandleActivated(activated: Boolean) {}

    open fun HandleOpenedFiles(files: MutableList<ZFileUrl>, modifiers: Int) {}

    fun ShowDebugText(str: String) {
        ZDebug.Print(str)
    }

    open fun HandleGotPushToken(token: String) {}

    open fun HandleLanguageBCPChanged(bcp: String) {}

    open fun HandleAppWillTerminate() {}

    open fun HandleShake() {}

    open fun HandleOpenUrl(url: ZUrl, showMessage: Boolean = true, done: (() -> Unit)? = null) : Boolean =
            false

    open fun HandleExit() {}
}

private class DataUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val url = ZUrl(string = intent.dataString)
        mainZApp?.HandleOpenUrl(url)
    }
}

private var dataUpdateReceiver: DataUpdateReceiver? = null
// https://stackoverflow.com/questions/2463175/how-to-have-android-service-communicate-with-activity

open class ZActivity: Activity() {
    val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
        }
    }

    override fun onBackPressed() {
        val view = ZGetCurrentyPresentedView()
        view.HandleBackButton()
    }

    override fun onPause() {
        if (dataUpdateReceiver != null) {
            unregisterReceiver(dataUpdateReceiver)
        }
        super.onPause()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("custom-event-name"));
        super.onResume()
    }

    override fun onStop() {
        mainZApp?.HandleExit()
        super.onStop()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean { // seems crazy we do this globally and not on focused view. Is this right????
        if (!ZIsTVBox()) {
            return super.onKeyUp(keyCode, event)
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            val focused = getCurrentFocus()
            if (focused != null) {
                val cv = focused as? ZCustomView
                if (cv != null) {
                    focused.HandlePressedInPosFunc?.invoke(ZPos(0.0, 0.0))
                }
            }
            return true
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_D -> {
                true
            }
            KeyEvent.KEYCODE_F -> {
                true
            }
            KeyEvent.KEYCODE_J -> {
                true
            }
            KeyEvent.KEYCODE_K -> {
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ZDebug.Print("got permession")
            } else {
                ZDebug.Print("denied permession")
            }
        }
    }


}
// var ZMainFunc:((args: List<String>)->Unit)? = null

var mainZApp: ZApp? = null
