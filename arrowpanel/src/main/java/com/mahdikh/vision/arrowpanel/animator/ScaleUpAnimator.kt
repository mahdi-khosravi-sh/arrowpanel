package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class ScaleUpAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.apply {
            pivotY = measuredHeight.toFloat()
            scaleY = 0.6F
        }
    }

    override fun animateShowImpl(view: View) {
        view.animate().apply {
            alpha(1.0F)
            duration = this@ScaleUpAnimator.duration
            interpolator = null
        }.start()

        view.animate().apply {
            scaleY(1.0F)
            duration = this@ScaleUpAnimator.duration
            interpolator = this@ScaleUpAnimator.interpolator
        }.start()
    }
}