//
//  ZApp.Android.kt
//
//  Created by Tor Langballe on /15/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import android.R.attr.orientation
import android.content.res.Configuration


var zMainActivityContext: Context? = null
var zMainActivity: Activity? = null
var lastOrientation = -1

open class ZApp {
    companion object {
        var appFile: ZFileUrl? = null

        val Version: Triple<String, Float, Int>
            get() {
                val p = zMainActivityContext!!.getPackageManager()
                val v = p.getPackageInfo(zMainActivityContext!!.getPackageName(), 0)
                val s = v.versionName
                // version string, version with comma 1.2, build
                return Triple(s, 0f, 0)
            }

        fun SetupActivity(activity:Activity, first:Boolean) {
            zMainActivity = activity
            zMainActivityContext = activity.applicationContext

            if (!first) {
                zHandleOrientationChanged()
            }
/*
            val orientationEventListener = object : OrientationEventListener(activity.applicationContext, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    val winManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
                    if (orientation != lastOrientation) {
                        val p = when(orientation) {
                            90 -> Pair(ZAlignment.Right, ZScreenLayout.landscapeLeft)
                            270 -> Pair(ZAlignment.Left, ZScreenLayout.landscapeRight)
                            180 -> Pair(ZAlignment.Bottom, ZScreenLayout.portraitUpsideDown)
                            else -> Pair(ZAlignment.Top, ZScreenLayout.portrait)
                        }
                        ZScreen.orientation = p.second
                        zHandleRotation(p.first, orientation)
                    }
                    lastOrientation = orientation
                }
            }

            if (orientationEventListener.canDetectOrientation()) {
                orientationEventListener.enable()
            }
*/
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build() // without this host resolving fails https://stackoverflow.com/questions/22395417/error-strictmodeandroidblockguardpolicy-onnetwork
//            StrictMode.setThreadPolicy(policy)
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
}

// var ZMainFunc:((args: List<String>)->Unit)? = null

var mainZApp: ZApp? = null
