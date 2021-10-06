package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View

class FadeTouchAnimator : TouchAnimator() {
    var toAlpha: Float = 0.85F

    override fun animateTouchDownImpl(view: View) {
        view.animate().apply {
            alpha(toAlpha)
            duration = this@FadeTouchAnimator.duration
            interpolator = null
        }.start()
    }

    override fun animateTouchUpImpl(view: View) {
        view.animate().apply {
            alpha(1.0F)
            duration = this@FadeTouchAnimator.duration
            interpolator = null
        }.start()
    }
}