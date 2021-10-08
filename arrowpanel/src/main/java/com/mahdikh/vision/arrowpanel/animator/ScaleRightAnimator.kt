package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class ScaleRightAnimator : SimpleScaleAnimator() {
    init {
        fromScale = 0.65F
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            scaleY = 1.0F
            pivotX = 0.0F
        }
    }

    override fun fadeIn(v: View, duration: Long) {
        super.fadeIn(v, this.duration)
    }
}