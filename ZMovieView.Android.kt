
//
//  ZMoviePlayer.swift
//
//  Created by Tor Langballe on /18/12/17.
//

package com.github.torlangballe.cetrusandroid

import android.media.MediaPlayer
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

    constructor() : super(zMainActivityContext!!) {
        this.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(mp:MediaPlayer, what:Int, extra:Int) : Boolean {
                //Return true to get rid of auto-generated alerts
                var err = ""
                if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
                    err = "unknown"
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
                    err = "server died"
                if (extra == MediaPlayer.MEDIA_ERROR_IO)
                    err += ", io"
                if (extra == MediaPlayer.MEDIA_ERROR_MALFORMED)
                    err += ", malformed"
                if (extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED)
                    err += ", unsupported"
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT)
                    err += ", timed-out"
                return true
            }
        })

        this.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp:MediaPlayer, what:Int, extra:Int) : Boolean {
                var info = ""
                //if(what == MediaPlayer.MEDIA_INFO_UNKNOWN)
                //	info = "unknown";
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING)
                    info = "track lagging"
                //if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                //	info = "rendering started";
                //if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                //	info = "buffering started";
                //if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                //	info = "buffering ended";
                if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING)
                    info = "bad interleaving"
                if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE)
                    info = "not seekable"
                if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE)
                    info = "metadata updated"

                if (info.length != 0) {
//                    EventPropagator.sendPlayerEvent(channelnr, getAppContext(), "INFO (" + info + ")" + quickStats())
                }
                return false
            }
        })

        this.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer) {
                val width = mp.getVideoWidth();
                val height = mp.getVideoHeight();
                minSize = ZSize(100.0, 100.0 / width * height)
                val p = parent as? ZContainerView
                if (p != null) {
                    p.ArrangeChildren()
                }
            }
        })
    }

//    deinit {
//        player?.removeObserver(this, forKeyPath = "rate", context = null)
//    }

    fun SetUrl(url: String) {
        val uri = Uri.parse(url)
        super.setVideoURI(uri)
        Play()
//            player?.addObserver(this, forKeyPath = "rate", options = NSKeyValueObservingOptions(), context = null)
//        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        holder.setSizeFromLayout()
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val s = minSize * ZScreen.Scale
        setMeasuredDimension(ZMath.Ceil(s.w).toInt(), ZMath.Ceil(s.h).toInt())
    }
}

/*
*/