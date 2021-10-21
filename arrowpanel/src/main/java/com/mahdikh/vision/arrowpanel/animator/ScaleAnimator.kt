package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View

open class ScaleAnimator : SimpleScaleAnimator {
    constructor() : super()
    constructor(interpolator: TimeInterpolator) : super(interpolator)

    override fun animateHideImpl(v: View) {
        super.animateHideImpl(v)
        v.animate().apply {
            scaleX(fromScale).scaleY(fromScale)
            duration = this@ScaleAnimator.duration
            interpolator = this@ScaleAnimator.interpolator
        }.start()
    }
}