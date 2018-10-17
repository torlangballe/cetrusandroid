package com.github.torlangballe.cetrusandroid

//
//  ZAnimation.swift
//
//  Created by Tor Langballe on /20/08/18.
//

class ZAnimation {
    companion object {
        fun Do(duration: Double = 0.4, animations: () -> Unit, completion: ((done: Boolean) -> Unit)? = null) {
            if (duration == 0.0) {
                animations()
                completion?.invoke(true)
            } else {
                completion?.invoke(true) // TODO: Do something!!
            }
        }

        fun RemoveAllFromView(view: UIView) {
            // TODO: ZAnimation.RemoveAllFromView
            ZDebug.Print("ZAnimation.RemoveAllFromView not made yet")
        }

        fun ViewHasAnimations(view: UIView) : Boolean {
            ZNOTIMPLEMENTED()
            return false
        }

        fun PulseView(view: UIView, scale: Double, duration: Double, fromScale: Double = 1.0, repeatCount: Double = Double.MAX_VALUE) {
            // TODO: ZAnimation.PulseView
            ZDebug.Print("ZAnimation.PulseView not made yet")
        }

        fun ScaleView(view: UIView, scaleTo: Double, duration: Double) {
            animateView(view, from = 1.0, to = scaleTo, duration = duration, type = "transform.scale", repeatCount = 1.0, autoreverses = false)
        }

        fun FadeView(view: UIView, to: Double, duration: Double, from: Double = 1.0) {
            animateView(view, from = from, to = to, duration = duration, type = "opacity", repeatCount = 0.0, autoreverses = false)
        }

        fun PulseOpacity(view: UIView, to: Double, duration: Double, from: Double = 1.0, repeatCount: Double = Double.MAX_VALUE) {
            animateView(view, from = from, to = to, duration = duration, type = "opacity", repeatCount = repeatCount)
        }

        fun RippleWidget(view: UIView, duration: Double) {
            ZNOTIMPLEMENTED()
        }

        fun MoveViewOnPath(view: ZView, path: ZPath, duration: Double, repeatCount: Double = Double.MAX_VALUE, begin: Double = 0.0) {
            ZNOTIMPLEMENTED()
        }

        fun RotateView(view: ZView, degreesClockwise: Double = 360.0, secs: Double, repeatCount: Double = Double.MAX_VALUE) {
            ZNOTIMPLEMENTED()
        }

//        fun AddGradientAnimationToView(view: ZView, colors: List<ZColor>, locations: List<List<Double>>, duration: Double, autoReverse: Boolean = false, speed: Double = 1f, opacity: Double = 1f, min: ZPos = ZPos(0, 0), max: ZPos = ZPos(0, 1)) : ZGradientLayer {
//            ZNOTIMPLEMENTED()
//        }
//
        fun SetViewLayerSpeed(view: ZView, speed: Double, resetTime: Boolean = false) {
    ZNOTIMPLEMENTED()
        }

        fun FlipViewHorizontal(view: UIView, duration: Double = 0.8, left: Boolean, animate: (() -> Unit)? = null) {
            ZNOTIMPLEMENTED()
        }
    }
}

private fun animateView(view: UIView, from: Double, to: Double, duration: Double, type: String, repeatCount: Double = Double.MAX_VALUE, autoreverses: Boolean = true) {
}
