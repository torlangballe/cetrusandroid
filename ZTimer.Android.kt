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
import java.util.*

import java.util.concurrent.CountDownLatch

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

    fun Set(secs: Double, now: Boolean = false, done: () -> Boolean) {
        super.Stop()
        if (now) {
            if (!done()) {
                return
            }
        }
        androidTimer = Timer()
        androidTimer!!.schedule(object : TimerTask() {
            override fun run() {
                ZMainQue.sync() {
                    if (!done()) {
                        Stop()
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
    private val syncLatch = CountDownLatch(1)

    init {
        name = threadName
        start()
    }

    private fun sendMessage(msg: Message, delay: Int) {
        try {
            syncLatch.await()
            if (delay <= 0) {
                handler!!.sendMessage(msg)
            } else {
                handler!!.sendMessageDelayed(msg, delay.toLong())
            }
        } catch (e: Exception) {
            System.out.print(e)
        }

    }

    fun cancelRunnable(runnable: Runnable) {
        try {
            syncLatch.await()
            handler!!.removeCallbacks(runnable)
        } catch (e: Exception) {
            System.out.print(e)
        }

    }

    @JvmOverloads fun postRunnable(delay: Long = 0, runnable: Runnable) {
        try {
            syncLatch.await()
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
            syncLatch.await()
            handler!!.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            System.out.print(e)
        }
    }

    override fun run() {
        Looper.prepare()
        handler = Handler()
        syncLatch.countDown()
        Looper.loop()
    }


    fun async(delay:Double = 0.0, f:()->Unit) {
        if (name == "@main") {
            if (delay != 0.0) {
                postRunnable((delay * 1000).toLong(), Runnable {
                    zMainActivity!!.runOnUiThread(Runnable { f() })
                })
            } else {
                zMainActivity!!.runOnUiThread(Runnable { f() })
            }
        } else {
            postRunnable((delay * 1000).toLong(), Runnable { f() })
        }
    }

    fun sync(delay:Double = 0.0, f:()->Unit) {
        async(delay, f)
    }
}
