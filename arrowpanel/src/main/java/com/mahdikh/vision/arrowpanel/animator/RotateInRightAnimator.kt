package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class RotateInRightAnimator : RotationAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            val h = measuredHeight
            pivotX = measuredWidth / 4F
            pivotY = h - h / 4F
            translationX = v.x - 100
        }
    }

    override fun animateShowImpl(v: View) {
        super.animateShowImpl(v)
        v.animate()
            .translationX(v.translationX + 100)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .start()
    }
}