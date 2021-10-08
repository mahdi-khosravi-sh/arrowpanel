package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FlipVerticallyAnimator : FadeAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.rotationX = 90F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            rotationX(0.0F)
            interpolator = this@FlipVerticallyAnimator.interpolator
            duration = this@FlipVerticallyAnimator.duration
        }.start()
    }
}