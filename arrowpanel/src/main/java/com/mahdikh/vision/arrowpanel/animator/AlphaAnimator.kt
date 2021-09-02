package com.mahdikh.vision.arrowpanel.animator

import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

open class AlphaAnimator : Animator() {
    override fun initBeforeShow(arrowContainer: ArrowContainer) {
        super.initBeforeShow(arrowContainer)
        arrowContainer.alpha = 0.0F
    }

    override fun animateShow(arrowContainer: ArrowContainer) {
        arrowContainer.animate()
            .alpha(1.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(arrowContainer: ArrowContainer) {
        arrowContainer.animate()
            .alpha(0.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }
}