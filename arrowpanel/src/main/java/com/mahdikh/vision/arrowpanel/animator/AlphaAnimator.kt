package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class AlphaAnimator : Animator() {
    override fun initBeforeShow(view: View) {
        super.initBeforeShow(view)
        view.alpha = 0.0F
    }

    override fun animateShow(view: View) {
        view.animate()
            .alpha(1.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(view: View) {
        view.animate()
            .alpha(0.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }
}