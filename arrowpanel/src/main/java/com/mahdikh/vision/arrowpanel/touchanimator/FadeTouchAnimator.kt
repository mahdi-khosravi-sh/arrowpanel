package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View

class FadeTouchAnimator : ActionTouchAnimator() {
    var toAlpha: Float = 0.85F

    override fun animateTouchDownImpl(view: View) {
        view.animate()
            .alpha(toAlpha)
            .setInterpolator(null)
            .setDuration(duration)
            .start()
    }

    override fun animateTouchUpImpl(view: View) {
        view.animate()
            .alpha(1.0F)
            .setInterpolator(null)
            .setDuration(duration)
            .start()
    }
}