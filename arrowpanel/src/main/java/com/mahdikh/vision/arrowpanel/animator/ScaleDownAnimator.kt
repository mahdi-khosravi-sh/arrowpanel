package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class ScaleDownAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.pivotY = 0.0F
        view.scaleY = 0.5F
    }

    override fun animateShowImpl(view: View) {
        view.animate().apply {
            alpha(1.0F)
            duration = this@ScaleDownAnimator.duration
            interpolator = null
        }.start()

        view.animate().apply {
            scaleY(1.0F)
            duration = this@ScaleDownAnimator.duration
            interpolator = this@ScaleDownAnimator.interpolator
        }.start()
    }
}