package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class RotationAnimator : FadeAnimator() {
    var fromRotation = -45.0F

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.rotation = fromRotation
    }

    override fun animateShowImpl(v: View) {
        super.animateShowImpl(v)
        v.animate().apply {
            rotation(0.0F)
            interpolator = this@RotationAnimator.interpolator
            duration = this@RotationAnimator.duration
        }.start()
    }
}