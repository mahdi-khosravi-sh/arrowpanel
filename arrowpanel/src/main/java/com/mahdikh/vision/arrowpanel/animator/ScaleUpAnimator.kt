package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class ScaleUpAnimator : FadeAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            pivotY = measuredHeight.toFloat()
            scaleY = 0.6F
        }
    }

    override fun animateShowImpl(v: View) {
        super.animateShowImpl(v)
        v.animate().apply {
            scaleY(1.0F)
            duration = this@ScaleUpAnimator.duration
            interpolator = this@ScaleUpAnimator.interpolator
        }.start()
    }
}