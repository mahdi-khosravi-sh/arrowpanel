package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class ScaleAnimator : SimpleScaleAnimator() {
    override fun animateHideImpl(v: View) {
        super.animateHideImpl(v)
        v.animate().apply {
            scaleX(fromScale).scaleY(fromScale)
            duration = this@ScaleAnimator.duration
            interpolator = this@ScaleAnimator.interpolator
        }.start()
    }
}