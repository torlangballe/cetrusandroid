
//
//  ZMoviePlayer.swift
//
//  Created by Tor Langballe on /18/12/17.
//

package com.github.torlangballe.cetrusandroid

import android.net.Uri
import android.widget.VideoView

class ZMovieView: VideoView, ZView {
    override var objectName: String = "ZSlider"
    override var isHighlighted: Boolean = false
    override var Usable: Boolean = true
    var seeking = false
    var handlePlayPause: ((play: Boolean) -> Unit)? = null
    var minSize = ZSize(100.0, 100.0)

    override fun View() : UIView = this

//    var playerLayer: AVPlayerLayer? = null

    constructor() : super(zMainActivityContext!!) {
    }

//    deinit {
//        player?.removeObserver(this, forKeyPath = "rate", context = null)
//    }

    fun SetUrl(url: String) {

        val uri = Uri.parse(url)
        super.setVideoURI(uri)

//            val playerItem = AVPlayerItem(url = u)
//            // [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playFinished:) name:AVPlayerItemDidPlayToEndTimeNotification object:playerItem];
//            player = ZMediaPlayer(playerItem = playerItem)
//            playerLayer = AVPlayerLayer(player = player!!)
//            playerLayer!!.videoGravity = .resizeAspect
//                    this.layer.addSublayer(playerLayer!!)
//            playerLayer!!.frame = this.bounds
//            player?.play()
//            player?.addObserver(this, forKeyPath = "rate", options = NSKeyValueObservingOptions(), context = null)
//        }
    }

    fun HandleAfterLayout() {
//        playerLayer?.frame = this.bounds
    }

    fun Play() {
        if (isPlaying) {
            super.resume()
        } else {
            super.start()
        }
        handlePlayPause?:(true)
    }

    fun Pause() {
        super.pause()
        handlePlayPause?:(false)
    }
    var Pos: Double
        get() {
            return super.getCurrentPosition().toDouble() / 1000.0
        }
        set(p) {
            seeking = true
            super.seekTo((p * 1000).toInt())
            seeking = false
        }
    val Duration: Double
        get() {
            return super.getDuration().toDouble() / 1000.0
        }
}
