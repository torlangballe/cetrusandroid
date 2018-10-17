//
//  ZSoundPlayer.swift
//
//  Created by Tor Langballe on /25/11/15.
//
package com.github.torlangballe.cetrusandroid

enum class ZAudioRemoteCommand { none, play, pause, stop, togglePlayPause, nextTrack, previousTrack, beginSeekingBackward, endSeekingBackward, beginSeekingForward, endSeekingForward }

class ZSoundPlayer { // NSObject, AVAudioPlayerDelegate
    companion object {
//        var lastPlayer: AVAudioPlayer? = null
        var current: ZSoundPlayer? = null

        fun StopLastPlayedSound() {
//            ZSoundPlayer.lastPlayer?.stop()
        }

        fun SetCurrentTrackPos(pos: Double, duration: Double) {
            ZMainQue.async {
            }
        }

        fun SetCurrentTrackPlayingMetadata(image: ZImage?, title: String, album: String = "", pos: Double? = null) {
            ZMainQue.async {
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
                vurl = "$file.url!!"
            } else {
                done?.invoke()
                return
            }
        }
        if (stopLast) {
            StopLastPlayedSound()
        }
//        do {
//            audioPlayer = AVAudioPlayer(contentsOf = ZUrl(string = vurl).url!! as URL)
//        } catch let error {
//            ZDebug.Print("sound playing error:", error.localizedDescription, ZFileUrl(string = vurl).DataSizeInBytes, vurl)
//        }
//        if (volume != -1) {
//            audioPlayer?.volume = volume
//        }
        ZDebug.Print("Play Audio:", url)
//        audioPlayer?.play()
//        ZSoundPlayer.lastPlayer = audioPlayer
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
//        audioPlayer?.stop()
    }
}
