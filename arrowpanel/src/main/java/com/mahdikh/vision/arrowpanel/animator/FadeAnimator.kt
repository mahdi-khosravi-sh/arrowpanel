package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class FadeAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        view.alpha = 0.0F
    }

    override fun animateShowImpl(view: View) {
        view.animate().apply {
            alpha(1.0F)
            duration = this@FadeAnimator.duration
            interpolator = null
        }.start()
    }
}