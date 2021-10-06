package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class UnMagnifyAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.apply {
            scaleX = 1.25F
            scaleY = 1.25F
            pivotX = measuredWidth / 2F
            pivotY = measuredHeight / 2F
        }
    }

    override fun animateShowImpl(view: View) {
        view.animate().apply {
            alpha(1.0F)
            duration = this@UnMagnifyAnimator.duration
            interpolator = null
        }.start()

        view.animate().apply {
            scaleX(1.0F)
            scaleY(1.0F)
            duration = this@UnMagnifyAnimator.duration
            interpolator = this@UnMagnifyAnimator.interpolator
        }.start()
    }
}