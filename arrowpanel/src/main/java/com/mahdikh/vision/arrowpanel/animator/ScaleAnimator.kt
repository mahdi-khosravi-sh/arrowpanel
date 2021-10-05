package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class ScaleAnimator : Animator() {
    var fromScale: Float = 0.8F

    override fun animateShow(view: View) {
        view.alpha = 0.0F
        view.scaleX = fromScale
        view.scaleY = fromScale

        view.animate()
            .alpha(1.0F)
            .scaleX(1.0F)
            .scaleY(1.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(view: View) {
        view.animate()
            .alpha(0.0F)
            .scaleX(fromScale)
            .scaleY(fromScale)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }
}