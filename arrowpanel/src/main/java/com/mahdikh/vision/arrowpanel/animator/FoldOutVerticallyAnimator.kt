package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

open class FoldOutVerticallyAnimator : FadeAnimator {
    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator?) {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.pivotY = v.measuredHeight / 2F
        v.scaleY = 0.1F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            scaleY(1.0F)
            interpolator = this@FoldOutVerticallyAnimator.interpolator
            duration = this@FoldOutVerticallyAnimator.duration
        }.start()
    }
}