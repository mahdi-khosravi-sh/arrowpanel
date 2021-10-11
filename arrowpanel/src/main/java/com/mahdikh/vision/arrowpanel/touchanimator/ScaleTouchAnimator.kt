package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View
import android.view.animation.OvershootInterpolator

class ScaleTouchAnimator : ActionTouchAnimator() {
    var toScale: Float = 0.9F

    init {
        centerPivot = false
        duration = 400
        interpolator = OvershootInterpolator()
    }

    override fun animateTouchDownImpl(view: View) {
        view.animate()
            .scaleX(toScale)
            .scaleY(toScale)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .start()
    }

    override fun animateTouchUpImpl(view: View) {
        view.animate()
            .scaleX(1.0F)
            .scaleY(1.0F)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .start()
    }
}