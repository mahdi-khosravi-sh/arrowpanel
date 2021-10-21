package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

open class SimpleScaleAnimator : FadeAnimator {
    var fromScale: Float = 0.8F

    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator) : super() {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.scaleX = fromScale
        v.scaleY = fromScale
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            scaleX(1.0F).scaleY(1.0F)
            duration = this@SimpleScaleAnimator.duration
            interpolator = this@SimpleScaleAnimator.interpolator
        }.start()
    }
}