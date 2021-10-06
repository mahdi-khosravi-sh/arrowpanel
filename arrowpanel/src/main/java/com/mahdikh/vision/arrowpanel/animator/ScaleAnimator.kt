package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator

class ScaleAnimator : BaseAnimator {
    var fromScale: Float = 0.8F

    constructor() : super() {
        interpolator = OvershootInterpolator()
    }

    constructor(interpolator: TimeInterpolator) : super() {
        this.interpolator = interpolator
    }

    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.scaleX = fromScale
        view.scaleY = fromScale
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)
        view.animate().apply {
            scaleX(1.0F)
            scaleY(1.0F)
            duration = this@ScaleAnimator.duration
            interpolator = this@ScaleAnimator.interpolator
        }.start()
    }


    override fun animateHideImpl(view: View) {
        super.animateHideImpl(view)
//        view.animate().apply {
//            scaleX(fromScale)
//            scaleY(fromScale)
//            duration = this@ScaleAnimator.duration
//            interpolator = this@ScaleAnimator.interpolator
//        }.start()
    }
}