package com.mahdikh.vision.arrowpanel.animator

import android.view.View
import android.view.animation.BounceInterpolator

open class FallBounceAnimator : FadeAnimator() {
    var translationY = 80.0F

    init {
        duration = 600
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.translationY = v.y - translationY
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        v.animate().apply {
            translationY(v.translationY + translationY)
            duration = this@FallBounceAnimator.duration
            interpolator = BounceInterpolator()
        }.start()
    }
}