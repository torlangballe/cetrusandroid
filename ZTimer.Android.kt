//
//  ZTimer.swift
//
//  Created by Tor Langballe on /18/11/15.
//

package com.github.torlangballe.cetrusandroid

import android.os.SystemClock.sleep
import java.io.Closeable
import android.os.Handler
import android.os.Looper
import android.os.Message
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.CoroutineContext

open class ZTimerBase : Closeable {
    var androidTimer: Timer? = null

    override fun close() {
        Stop()
    }
    fun Stop() {
        androidTimer?.purge()
        androidTimer?.cancel()
        androidTimer = null
    }
    val Valid: Boolean
        get() = androidTimer != null
}

class ZRepeater: ZTimerBase() {
    var closure: (() -> Boolean)? = null

    fun Set(secs: Double, now: Boolean = false, onMainThread:Boolean = true, done: () -> Boolean) {
        super.Stop()
        if (now) {
            if (!done()) {
                return
            }
        }
        androidTimer = Timer()
        androidTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (onMainThread) {
                    if (zMainActivity == null) {
                        print("MainActivity in timer is Null!!!")
                        return
                    }
                    zMainActivity?.runOnUiThread {
                        if (!done()) {
                            Stop()
                        }
                    }
                } else {
                    if (!done()) {
                        if (zMainActivity == null) {
                            print("MainActivity in timer is Null2!!!")
                            return
                        }
                        zMainActivity!!.runOnUiThread {
                            Stop()
                        }
                    }
                }
            }
        }, 0, (secs * 1000).toLong())
    }
}

class ZTimer: ZTimerBase() {
    //        owner?.AddTimer(self)
    companion object {

        fun Sleep(secs: Double) {
            sleep((secs * 1000.0).toLong())
        }
    }

    fun Set(secs: Double, done: () -> Unit) {
        super.Stop()
        androidTimer = Timer()
        androidTimer!!.schedule(object : TimerTask() {
            override fun run() {
                ZMainQue.sync() {
                    done()
                }
            }
        }, (secs * 1000).toLong())
        return
    }
}

/*
fun ZPerformAfterDelay(afterDelay: Double, block: () -> Unit) {
    ZMainQue.sync(afterDelay, block)
}

val ZMainQue: ZDispatchQueue
    get() {
        return ZDispatchQueue("@main")
    }

fun ZGetBackgroundQue(name: String? = null, serial: Boolean = false) : ZDispatchQueue {
    return ZDispatchQueue(name ?: "background-que")
}

// use kotlin coroutines? :
// https://medium.com/@macastiblancot/android-coroutines-getting-rid-of-runonuithread-and-callbacks-cleaner-thread-handling-and-more-234c0a9bd8eb

class ZDispatchQueue(threadName: String) : Thread() {

    @Volatile
    private var handler: Handler? = null
    private var waitLatch = CountDownLatch(1)
    private val syncLatch = CountDownLatch(1)

    init {
        name = threadName
        start()
    }

//    private fun sendMessage(msg: Message, delay: Int) {
//        try {
//            waitLatch.await()
//            if (delay <= 0) {
//                handler!!.sendMessage(msg)
//            } else {
//                handler!!.sendMessageDelayed(msg, delay.toLong())
//            }
//        } catch (e: Exception) {
//            System.out.print(e)
//        }
//
//    }
//
    fun cancelRunnable(runnable: Runnable) {
        try {
            waitLatch.await()
            handler!!.removeCallbacks(runnable)
        } catch (e: Exception) {
            System.out.print(e)
        }
    }

    @JvmOverloads fun postRunnable(delay: Long = 0, runnable: Runnable) {
        try {
            waitLatch.await()
            if (delay <= 0) {
                handler!!.post(runnable)
            } else {
                handler!!.postDelayed(runnable, delay)
            }
        } catch (e: Exception) {
            System.out.print(e)
        }

    }

    fun cleanupQueue() {
        try {
            waitLatch.await()
            handler!!.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            System.out.print(e)
        }
    }

    override fun run() {
        Looper.prepare()
        handler = Handler()
        waitLatch.countDown()
        Looper.loop()
    }


    fun async(delay:Double = 0.0, f:()->Unit) {
        if (name == "@main") {
            if (delay != 0.0) {
                postRunnable((delay * 1000).toLong(), Runnable {
                    zMainActivity!!.runOnUiThread { f() }
                })
            } else {
                zMainActivity!!.runOnUiThread { f() }
            }
        } else {
            postRunnable((delay * 1000).toLong(), Runnable { f() })
        }
    }

    fun sync(delay:Double = 0.0, f:()->Unit) {
        waitLatch = CountDownLatch(1)
        async(delay) {
            f()
            waitLatch.countDown()
        }
        waitLatch.await()
    }
}
*/

fun ZPerformAfterDelay(afterDelay: Double, block: () -> Unit) {
    ZMainQue.async(afterDelay, block)
}

val ZMainQue: ZDispatchQueue
    get() {
        return ZDispatchQueue("@main")
    }

fun ZGetBackgroundQue(name: String? = null, serial: Boolean = false) : ZDispatchQueue {
    return ZDispatchQueue(name ?: "background-que")
}

// use kotlin coroutines? :
// https://medium.com/@macastiblancot/android-coroutines-getting-rid-of-runonuithread-and-callbacks-cleaner-thread-handling-and-more-234c0a9bd8eb

class ZDispatchQueue(threadName: String) : Thread() {
    init {
        name = threadName
    }
    fun run(wait: Boolean, delaySecs:Double = 0.0, f:()->Unit) {
        val waitLatch = CountDownLatch(1)
        val cx = (if (name == "@main") Dispatchers.Main else Dispatchers.Default)
        GlobalScope.launch(cx) {
            if (delaySecs != 0.0) {
                delay((delaySecs * 1000).toLong())
            }
            f()
            if (wait) {
                waitLatch.countDown()
            }
        }
        if (wait && name != "@main") {
            waitLatch.await()
        }
    }

    fun async(delaySecs:Double = 0.0, f:()->Unit) {
        run(wait = false, delaySecs = delaySecs, f = f)
    }

    fun sync(delaySecs:Double = 0.0, f:()->Unit) {
        run(wait = true, delaySecs = delaySecs, f = f)
    }
}
