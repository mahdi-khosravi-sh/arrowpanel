package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

open class FoldOutSideAnimator : FadeAnimator {
    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator?) {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.pivotX = v.measuredWidth / 2F
        v.scaleX = 0.1F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            scaleX(1.0F)
            interpolator = this@FoldOutSideAnimator.interpolator
            duration = this@FoldOutSideAnimator.duration
        }.start()
    }
}