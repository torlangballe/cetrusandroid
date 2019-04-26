
package com.github.torlangballe.cetrusandroid

import android.app.*
import android.content.Intent
import android.app.PendingIntent
import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.*
import android.support.v4.content.LocalBroadcastManager

// https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android
// https://stackoverflow.com/questions/42126979/cannot-keep-android-service-alive-after-app-is-closed

var zServiceContext : Context? = null

open class ZService : Service() {
    val NOTIFICATION_ID = 543
    var keepAlive = false
    var isServiceRunning = false

    companion object {
        inline fun Start(cls: Class<*>) {
            zMainActivity!!.startService(Intent(zMainActivityContext!!, cls)) //ProbeBoxService::class.java))
        }
    }

    open fun DoService() {
    }

    fun SendMessage() {
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        zServiceContext = this
        startServiceWithNotification()
        GlobalScope.launch {
            DoService()
            while (keepAlive) {
                delay(100)
            }
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        zServiceContext = null
        isServiceRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }

    fun startServiceWithNotification() {
        return
        if (isServiceRunning) {
            return
        }
        isServiceRunning = true

        val notificationIntent = Intent(applicationContext, ZActivity::class.java)
        notificationIntent.action = Intent.ACTION_MAIN  // A string containing the action name
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

//        val icon = BitmapFactory.decodeResource(resources, R.drawable.my_icon)

        val androidChannel = NotificationChannel("tv.bridgetech.boxprobe.Android", "ANDROID CHANNEL", NotificationManager.IMPORTANCE_DEFAULT)
        val notification = Notification.Builder(zMainActivityContext!!, androidChannel.id)
            .setContentTitle(zMainActivity!!.packageName)
            .setTicker(zMainActivity!!.packageName)
//            .setContentText(resources.getString(R.string.my_string))
//            .setSmallIcon(R.drawable.my_icon)
//            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            //                .setDeleteIntent(contentPendingIntent)  // if needed
            .build()
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_ID, notification)
    }

    fun stopService() {
        stopForeground(true)
        stopSelf()
        isServiceRunning = false
    }
}

