package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FlipHorizontallyAnimator : FadeAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.rotationY = 90F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            rotationY(0.0F)
            interpolator = this@FlipHorizontallyAnimator.interpolator
            duration = this@FlipHorizontallyAnimator.duration
        }.start()
    }
}