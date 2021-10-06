package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

class FoldOutSideAnimator : BaseAnimator {
    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator?) {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.pivotX = view.measuredWidth / 2F
        view.scaleX = 0.1F
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)

        view.animate().apply {
            scaleX(1.0F)
            interpolator = this@FoldOutSideAnimator.interpolator
            duration = this@FoldOutSideAnimator.duration
        }.start()
    }
}