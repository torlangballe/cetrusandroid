//
//  ZSoundPlayer.swift
//
//  Created by Tor Langballe on /25/11/15.
//
package com.github.torlangballe.cetrusandroid

import android.media.MediaPlayer

var audioPlayer:MediaPlayer? = null

enum class ZAudioRemoteCommand { none, play, pause, stop, togglePlayPause, nextTrack, previousTrack, beginSeekingBackward, endSeekingBackward, beginSeekingForward, endSeekingForward }

class ZSoundPlayer { // NSObject, AVAudioPlayerDelegate
    companion object {
//        var lastPlayer: AVAudioPlayer? = null
        var current: ZSoundPlayer? = null

        fun StopLastPlayedSound() {
            audioPlayer?.stop()
        }

        fun SetCurrentTrackPos(pos: Double, duration: Double) {
            ZMainQue.sync {
            }
        }

        fun SetCurrentTrackPlayingMetadata(image: ZImage?, title: String, album: String = "", pos: Double? = null) {
            ZMainQue.sync {
                var songInfo = mutableMapOf<String, Any>()
            }
        }

        fun Vibrate() {
        }
    }

    var done: (() -> Unit)? = null
    private var loop = false
//    var audioPlayer: AVAudioPlayer? = null

//    fun audioPlayerDidFinishPlaying(myaudio: AVAudioPlayer, successfully: Boolean) {
//        ZSoundPlayer.current = null
//        if (loop) {
//            audioPlayer?.play()
//        } else {
//            done?.invoke()
//        }
//    }

    fun PlayUrl(url: String, volume: Float = -1f, loop: Boolean = false, stopLast: Boolean = true, done: (() -> Unit)? = null) {
        var vurl = url
        if (!ZStr.HasPrefix(vurl, "file:")) {
            val file = ZGetResourceFileUrl("sound/" + vurl)
            if (file.Exists()) {
                vurl = file.AbsString
            } else {
                done?.invoke()
                return
            }
        }
        if (stopLast) {
            StopLastPlayedSound()
        }

        audioPlayer = MediaPlayer()

        try {
            val file = ZFileUrl(string = vurl)
            audioPlayer!!.setDataSource(file.FilePath)
            audioPlayer!!.prepare()
            audioPlayer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (done != null || loop) {
            current = this
            this.done = done
            this.loop = loop
//            audioPlayer?.delegate = this
//            if (!audioPlayer!!.play()) {
//                ZDebug.Print("audioPlayer play failed")
//                return
//            }
        }
    }

    fun Stop() {
        done?.invoke()
        audioPlayer?.stop()
    }
}
