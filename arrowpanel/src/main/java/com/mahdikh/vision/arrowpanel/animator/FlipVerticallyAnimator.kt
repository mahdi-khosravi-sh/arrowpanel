package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FlipVerticallyAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.rotationX = 90F
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)
        view.animate().apply {
            rotationX(0.0F)
            interpolator = this@FlipVerticallyAnimator.interpolator
            duration = this@FlipVerticallyAnimator.duration
        }.start()
    }
}