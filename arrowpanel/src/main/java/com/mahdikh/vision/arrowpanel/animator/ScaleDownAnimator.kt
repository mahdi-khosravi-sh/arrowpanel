package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class ScaleDownAnimator : FadeAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.pivotY = 0.0F
        v.scaleY = 0.5F
    }

    override fun animateShowImpl(v: View) {
        super.animateShowImpl(v)
        v.animate().apply {
            scaleY(1.0F)
            duration = this@ScaleDownAnimator.duration
            interpolator = this@ScaleDownAnimator.interpolator
        }.start()
    }
}