
//
//  ZNotification.swift
//
//  Created by Tor Langballe on /25/11/15.
//
package com.github.torlangballe.cetrusandroid

// https://useyourloaf.com/blog/local-notifications-with-ios-10/
data class ZNotificationAction(
        var sid:String = "",
        var title:String = "",
        var background:Boolean = false,
        var destructive:Boolean = false,
        // red button?
        var authRequired:Boolean = false,
        var inMini:Boolean = true) {// if false, it doesn't show in small notif
}

data class ZNotificationInfo(
        var sendInSecs: Float = 0f,
        var repeats: Boolean = false,
        var triggerTime: ZTime = ZTimeNull,
        var soundName:String = "",
        var title:String = "",
        var body:String = "",
        var userInfo: MutableMap<AnyHashable, Any> = mutableMapOf<AnyHashable, Any>(),
        var categoryId:String = "") {
}

class ZNotification {
    constructor(suid: String, info: ZNotificationInfo) {
        var secs = (info.sendInSecs).toDouble()
        if (!info.triggerTime.IsNull) {
            secs = info.triggerTime.Until()
        }
//        val trigger = UNTimeIntervalNotificationTrigger(timeInterval = secs, repeats = info.repeats)
//        val c = UNMutableNotificationContent()
//        c.title = info.title
//        c.body = info.body
//        c.userInfo = info.userInfo
        if (!info.soundName.isEmpty()) {
//            c.sound = UNNotificationSound(named = info.soundName)
        }
        if (!info.categoryId.isEmpty()) {
//            c.categoryIdentifier = info.categoryId
        }
    }

    fun SendLocal(done: (err: ZError?) -> Unit) {
//        val center = UNUserNotificationCenter.current()
//        center.add(this) { error ->
//            ZDebug.Print("ZNotifcation.SendLocal done:", error)
//            done(error)
//        }
//        ZDebug.Print("ZNotification.SendLocal:", this.trigger!!)
    }

    fun CancelAllLocal() {
//        val center = UNUserNotificationCenter.current()
//        center.removeAllPendingNotificationRequests()
    }

    fun SetBadgeNumber(n: Int) {
//        UIApplication.shared.applicationIconBadgeNumber = n
    }

    fun RegisterForNotifications(actions: List<ZNotificationAction> = mutableListOf(), categoryId: String = "") {
//        val center = UNUserNotificationCenter.current()
//        val options: UNAuthorizationOptions = mutableListOf(. alert, .sound)
//        center.requestAuthorization(options = options) { granted, error ->
//            if (!granted) {
//                ZDebug.Print("Notification authentication not granted:", error)
//            }
//        }
        if (categoryId.isEmpty()) {
            return
        }
//        for (c in registeredCategories) {
//            if (c.identifier == categoryId) {
//                return
//            }
//        }
//        var unActions = mutableListOf<UNNotificationAction>()
//        for (action in actions) {
//            var opts = UNNotificationActionOptions()
//            if (action.authRequired) {
//                opts.update(with = . authenticationRequired)
//            }
//            if (action.destructive) {
//                opts.update(with = . destructive)
//            }
//            if (!action.background) {
//                opts.update(with = . foreground)
//            }
//            val a = UNNotificationAction(identifier = action.sid, title = action.title, options = opts)
//            unActions.append(a)
//        }
//        val category = UNNotificationCategory(identifier = categoryId, actions = unActions, intentIdentifiers = mutableListOf(), options = UNNotificationCategoryOptions())
//        registeredCategories.update(with = category)
//        center.setNotificationCategories(registeredCategories)
    }

    fun RegisterForPushNotifications() {
//        val center = UNUserNotificationCenter.current()
//        center.requestAuthorization(options = mutableListOf(. sound, . alert, .badge)) { granted, error ->
//            if (error == null) {
//                UIApplication.shared.registerForRemoteNotifications()
//            }
//        }
    }

    fun IsRegisteredForRemoteNotifications(): Boolean {
//            UIApplication.shared.isRegisteredForRemoteNotifications
        return false
    }
}

// var registeredCategories = Set<UNNotificationCategory>()
