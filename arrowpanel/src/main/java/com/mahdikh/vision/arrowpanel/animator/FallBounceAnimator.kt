package com.mahdikh.vision.arrowpanel.animator

import android.view.View
import android.view.animation.BounceInterpolator

class FallBounceAnimator : BaseAnimator() {
    var translationY = 80.0F

    init {
        duration = 600
    }

    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.translationY = view.y - translationY
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)

        view.animate().apply {
            translationY(view.translationY + translationY)
            duration = this@FallBounceAnimator.duration
            interpolator = BounceInterpolator()
        }.start()
    }
}