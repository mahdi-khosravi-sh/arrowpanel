package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

class FoldOutVerticallyAnimator : BaseAnimator {
    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator?) {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.pivotY = view.measuredHeight / 2F
        view.scaleY = 0.1F
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)
        view.animate().apply {
            scaleY(1.0F)
            interpolator = this@FoldOutVerticallyAnimator.interpolator
            duration = this@FoldOutVerticallyAnimator.duration
        }.start()
    }
}