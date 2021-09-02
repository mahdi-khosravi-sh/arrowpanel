package com.mahdikh.vision.arrowpanel.animator

import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

open class ScaleAnimator : Animator() {
    var fromScale: Float = 0.8F

    override fun initBeforeShow(arrowContainer: ArrowContainer) {
        super.initBeforeShow(arrowContainer)
        arrowContainer.alpha = 0.0F
        arrowContainer.scaleX = fromScale
        arrowContainer.scaleY = fromScale
    }

    override fun animateShow(arrowContainer: ArrowContainer) {
        arrowContainer.animate()
            .alpha(1.0F)
            .scaleX(1.0F)
            .scaleY(1.0F)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(arrowContainer: ArrowContainer) {
        arrowContainer.animate()
            .alpha(0.0F)
            .scaleX(fromScale)
            .scaleY(fromScale)
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }
}