package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View
import android.view.animation.OvershootInterpolator

class ScaleTouchAnimator : TouchAnimator() {
    var toScale: Float = 0.9F

    init {
        duration = 400
        interpolator = OvershootInterpolator()
    }

    override fun animateTouchDownImpl(view: View) {
        view.animate().apply {
            scaleX(toScale)
            scaleY(toScale)
            interpolator = this@ScaleTouchAnimator.interpolator
            duration = this@ScaleTouchAnimator.duration
        }.start()
    }

    override fun animateTouchUpImpl(view: View) {
        view.animate().apply {
            scaleX(1.0F)
            scaleY(1.0F)
            interpolator = this@ScaleTouchAnimator.interpolator
            duration = this@ScaleTouchAnimator.duration
        }.start()
    }
}