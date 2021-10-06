package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FlipHorizontallyAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.rotationY = 90F
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)

        view.animate().apply {
            rotationY(0.0F)
            interpolator = this@FlipHorizontallyAnimator.interpolator
            duration = this@FlipHorizontallyAnimator.duration
        }.start()
    }
}