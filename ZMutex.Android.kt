//
//  ZMutex.swift
//
//  Created by Tor Langballe on /11/12/16.
//

package com.github.torlangballe.cetrusandroid

import java.util.concurrent.locks.ReentrantLock

class ZMutex {
    val lock = ReentrantLock()

    fun Lock() {
        lock.lock()
    }

    fun Unlock() {
        lock.unlock()
    }
}


