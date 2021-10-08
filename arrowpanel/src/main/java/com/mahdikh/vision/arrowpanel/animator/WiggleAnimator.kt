package com.mahdikh.vision.arrowpanel.animator

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

open class WiggleAnimator : FadeAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.pivotX = v.measuredWidth / 2F
        v.pivotY = v.measuredHeight / 2F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v, duration / 2)
        val pRotation = PropertyValuesHolder.ofKeyframe(
            "rotation",
            Keyframe.ofFloat(0.0F, -3.0F),
            Keyframe.ofFloat(0.2F, 3.0F),
            Keyframe.ofFloat(0.4F, -3.0F),
            Keyframe.ofFloat(0.6F, 1.0F),
            Keyframe.ofFloat(0.8F, -1.0F),
            Keyframe.ofFloat(1.0F, 0.0F)
        )
        ObjectAnimator.ofPropertyValuesHolder(v, pRotation).apply {
            duration = this@WiggleAnimator.duration
            interpolator = this@WiggleAnimator.interpolator
        }.start()
    }
}